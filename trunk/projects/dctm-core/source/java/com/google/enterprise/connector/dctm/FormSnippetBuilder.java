// Copyright 2006 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.dctm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.enterprise.connector.dctm.FormSnippet.*;
import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IDocbaseMap;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.spi.RepositoryException;

public class FormSnippetBuilder {
  private static final Logger logger =
      Logger.getLogger(FormSnippetBuilder.class.getName());

  private List<String> configKeys;

  private String includedObjectType;

  private String includedMeta;

  private String rootObjectType;

  public FormSnippetBuilder() {
  }

  /**
   * Set the keys that are required for configuration.
   *
   * @param configKeys a list of String keys
   */
  public void setConfigKeys(List<String> configKeys) {
    logger.fine("setConfigKeys List");
    if (configKeys == null) {
      throw new IllegalArgumentException();
    }
    this.configKeys = configKeys;
  }

  public void setIncluded_meta(String includedMeta) {
    this.includedMeta = includedMeta;
    logger.config("included_meta set to " + includedMeta);
  }

  public void setIncluded_object_type(String includedObjectType) {
    this.includedObjectType = includedObjectType;
    logger.config("included_object_type set to " + includedObjectType);
  }

  public void setRoot_object_type(String rootObjectType) {
    this.rootObjectType = rootObjectType;
    logger.config("root_object_type set to " + rootObjectType);
  }

  public FormSnippet build(Map<String, String> configMap,
      ResourceBundle resource, IClientX cl, ISession sess)
      throws RepositoryException {
    // Get the properties from the config map, if one is present, or
    // set some defaults.
    String rootType;
    String advConf;
    if (configMap != null) {
      rootType = configMap.get(ROOT_OBJECT_TYPE).trim();
      logger.config("rootType from configmap: " + rootType);
      advConf = configMap.get(ADVANCEDCONF);
      logger.config("advConf from configmap: " + advConf);
    } else {
      rootType = rootObjectType;
      logger.config("root_object_type: " + rootType);
      advConf = "";
    }

    List<String> docbases = getDocbases(cl);

    boolean isAdvancedOn = sess != null && advConf.equals("on");
    SortedSet<String> documentTypes;
    SortedSet<String> allTypes;
    Map<String, Set<String>> propertiesMap;
    Set<String> includedProperties;
    if (isAdvancedOn) {
      documentTypes = getListOfTypes("dm_document", cl, sess);
      documentTypes.remove("dm_document");
      allTypes = getListOfTypes(rootType, cl, sess);
      propertiesMap =
          getPropertiesMap(configMap.get(INCLUDED_OBJECT_TYPE), sess);
      includedProperties = getIncludedProperties(configMap.get(INCLUDED_META));
    } else {
      documentTypes = null;
      allTypes = null;
      propertiesMap = null;
      includedProperties = null;
    }

    FormSnippet snippet = new FormSnippet(configMap, resource, rootType,
        docbases, isAdvancedOn, documentTypes, allTypes, propertiesMap,
        includedProperties);
    snippet.setConfigKeys(configKeys);
    snippet.setIncluded_object_type(includedObjectType);
    snippet.setIncluded_meta(includedMeta);
    return snippet;
  }

  private SortedSet<String> getListOfTypes(String rootType, IClientX cl,
      ISession sess) {
    SortedSet<String> types = new TreeSet<String>();
    try {
      IQuery que = cl.getQuery();
      String queryString =
          "select r_type_name from dmi_type_info where any r_supertype = '"
          + rootType + "'";
      logger.config("queryString: " + queryString);
      que.setDQL(queryString);

      ICollection collec = que.execute(sess, IQuery.EXECUTE_READ_QUERY);
      while (collec.next()) {
        types.add(collec.getString("r_type_name"));
      }

      try {
        if (collec.getState() != ICollection.DF_CLOSED_STATE) {
          collec.close();
          logger.fine("collection closed");
        }
      } catch (RepositoryException re1) {
        logger.severe("Error while closing " + re1);
      }
    } catch (RepositoryException e) {
      logger.log(Level.WARNING, "Error getting the list of types", e);
    }
    return types;
  }

  private Set<String> getIncludedProperties(String includedMeta) {
    logger.config("string meta: " + includedMeta);
    String[] metaList = includedMeta.split(",");
    Set<String> includedProperties = new TreeSet<String>();
    for (String property : metaList) {
      includedProperties.add(property.trim());
    }
    return includedProperties;
  }

  private Set<String> getSysObjectAttributes(ISession sess)
      throws RepositoryException {
    IType type = sess.getType("dm_sysobject");
    HashSet<String> attributes = new HashSet<String>();
    for (int j = 0; j < type.getTypeAttrCount(); j++) {
      String attrName = type.getTypeAttrNameAt(j);
      logger.config("dm_sysobject attribute: " + attrName);
      attributes.add(attrName);
    }
    return attributes;
  }

  /**
   * Gets a map from all of the properties in the included object
   * types to the set of included base types that each appears in.
   *
   * @param configMap
   * @param sess the DFC session
   */
  /* @VisibleForTesting */
  Map<String, Set<String>> getPropertiesMap(String includedObjectType,
      ISession sess) throws RepositoryException {
    Map<String, Set<String>> propertiesMap =
        new TreeMap<String, Set<String>>();

    Set<String> sysObjectAttrs = getSysObjectAttributes(sess);

    // Loop of the selected types list.
    logger.config("Included object types: " + includedObjectType);
    String[] typeList = includedObjectType.split(",");
    for (String typeName : typeList) {
      IType type = sess.getType(typeName);
      logger.config("Type name: " + typeName + "; attribute count: "
          + type.getTypeAttrCount());

      // Loop of the properties of each selected type.
   ATTRIBUTES:
      for (int i = 0; i < type.getTypeAttrCount(); i++) {
        String attrName = type.getTypeAttrNameAt(i);
        logger.config("Attribute name " + i + ": " + attrName);

        Set<String> baseTypes = new HashSet<String>();
        if (sysObjectAttrs.contains(attrName)) {
          baseTypes.add("dm_sysobject");
        } else if (!propertiesMap.containsKey(attrName)) {
          baseTypes.add(typeName);
        } else {
          Set<String> currentTypes = propertiesMap.get(attrName);
          logger.config(attrName + " previously found in types: "
              + currentTypes);

          // Loop of the types we have already found for this attribute.
          for (String currentTypeName : currentTypes) {
            IType currentType = sess.getType(currentTypeName);
            if (currentType.isSubTypeOf(typeName)) {
              logger.config(typeName + " is supertype of " + currentTypeName);
              baseTypes.add(typeName);
            } else if (type.isSubTypeOf(currentTypeName)) {
              // We don't need typeName for this attribute, so skip to
              // the next attribute.
              logger.config(typeName + " is subtype of " + currentTypeName);
              continue ATTRIBUTES;
            } else {
              logger.config(typeName + " is unrelated to " + currentTypeName);
              baseTypes.add(typeName);
              baseTypes.add(currentTypeName);
            }
          }
        }

        logger.fine("Adding to properties map for attribute " + attrName + ": "
            + baseTypes);
        propertiesMap.put(attrName, baseTypes);
      }
    }

    for (String name : DctmSysobjectDocument.EXTENDED_PROPERTIES) {
      propertiesMap.put(name, Collections.singleton("dm_sysobject"));
    }
    
    return propertiesMap;
  }

  private List<String> getDocbases(IClientX cl) throws RepositoryException {
    IClient client = cl.getLocalClient();
    IDocbaseMap docbaseMap = client.getDocbaseMap();
    int count = docbaseMap.getDocbaseCount();
    ArrayList<String> docbases = new ArrayList<String>(count);
    for (int i = 0; i < count; i++) {
      docbases.add(docbaseMap.getDocbaseName(i));
    }
    return docbases;
  }
}
