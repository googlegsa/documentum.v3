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
    SortedSet<String> allTypes;
    Map<String, Set<String>> metasByTypes;
    HashSet<String> existingProperties;
    Set<String> includedProperties;
    SortedSet<String> documentTypes;
    if (isAdvancedOn) {
      allTypes = getListOfTypes(rootType, cl, sess);

      metasByTypes = new TreeMap<String, Set<String>>();
      existingProperties = new HashSet<String>();
      includedProperties = getIncludedProperties(configMap);
      fillProperties(configMap, sess, metasByTypes, existingProperties);

      documentTypes = getListOfTypes("dm_document", cl, sess);
      documentTypes.remove("dm_document");
    } else {
      allTypes = null;
      metasByTypes = null;
      existingProperties = null;
      includedProperties = null;
      documentTypes = null;
    }

    FormSnippet snippet = new FormSnippet(configMap, resource, rootType,
        docbases, isAdvancedOn, allTypes, metasByTypes, existingProperties,
        includedProperties, documentTypes);
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

  private Set<String> getIncludedProperties(Map<String, String> configMap) {
    logger.config("string meta: " + configMap.get(INCLUDED_META));
    String stMeta = configMap.get(INCLUDED_META);
    String[] metaList = stMeta.split(",");
    Set<String> includedProperties = new TreeSet<String>();
    for (String property : metaList) {
      includedProperties.add(property.trim());
    }
    return includedProperties;
  }

  private void fillProperties(Map<String, String> configMap,
      ISession sess, Map<String, Set<String>> metasByTypes,
      HashSet<String> existingProperties)
      throws RepositoryException {
    IType dmsysType = sess.getType("dm_sysobject");
    HashSet<String> hashDmSysMeta = new HashSet<String>();
    for (int j = 0; j < dmsysType.getTypeAttrCount(); j++) {
      IAttr dmsysattr = dmsysType.getTypeAttr(j);
      String dmsysattrname = dmsysattr.getName();
      logger.config("dmsysattrname " + dmsysattrname
          + " is metadata of dm_sysobject");
      hashDmSysMeta.add(dmsysattrname);
    }

    // Loop of the selected types list.
    logger.config("string type: " + configMap.get(INCLUDED_OBJECT_TYPE));
    String[] typeList = configMap.get(INCLUDED_OBJECT_TYPE).split(",");
    for (String stType : typeList) {
      IType mytype = sess.getType(stType);
      logger.config("stType is " + stType);
      // Loop of the properties of each selected type.
      for (int i = 0; i < mytype.getTypeAttrCount(); i++) {
        logger.config("Property count: " + mytype.getTypeAttrCount());
        HashSet<String> tempTypes = new HashSet<String>();
        IAttr attr = mytype.getTypeAttr(i);
        ///logger.config("attr is " + attr.toString());
        String data = attr.getName();
        logger.config("attr is " + data + " - attr of the type " + stType);
        if (!existingProperties.contains(data)) {
          existingProperties.add(data);
        }
        if (hashDmSysMeta.contains(data)) {
          // If the property is a dm_sysobject one, dm_sysobject is
          // added to the temporary types hashset.
          tempTypes.add("dm_sysobject");
          logger.config("attr " + data + " is a dm_sysobject attribute");
        } else if (!metasByTypes.containsKey(data)) {
          // If the property is not already present in the list of
          // available properties : the type is added to the
          // temporary types hashset.
          tempTypes.add(stType);
          logger.config("attr " + data
              + " is a new attribute for the metas list");
        } else {
          // If the property is not already present in the list of
          // available properties.
          logger.config("attr " + data
              + " is not a new attribute for the metas list");
          Set<String> hashTypes = metasByTypes.get(data);
          // Loop of the hashset of types whom the property can
          // belong to (among the selected types).
          for (String stCurrentType : hashTypes) {
            logger.config("the type " + stCurrentType
                + " is already known to have the meta " + data);
            IType currentType = sess.getType(stCurrentType);
            if (stCurrentType.equals("dm_sysobject")) {
              // If the selected type is dm_sysobject : dm_sysobject
              // is added to the temporary types hashset.
              logger.config(stCurrentType + " is " + stCurrentType);
              tempTypes.add(stCurrentType);
            } else if (currentType.getSuperType().getName().equals(stType)) {
              // If the selected type is the supertype of one type
              // whom the property can belong to : the selected type
              // is added to the temporary types hashset.
              logger.config(stType + " is supertype of " + stCurrentType);
              tempTypes.add(stType);
              logger.config("so supertype " + stType + " is added");
            } else if (mytype.isSubTypeOf(stCurrentType)) {
              // If the selected type is the subtype of one type
              // whom the property can belong to : the type whom the
              // property can belong to is added to the temporary
              // types hashset.
              logger.config(stType + " is  subtype of " + stCurrentType);
              tempTypes.add(stCurrentType);
              logger.config(" so supertype " + stCurrentType + " is added");
            } else if (stType.equals(stCurrentType)) {
              // If the selected type is one of the types whom the
              // property can belong to : the type whom the property
              // can belong to is added to the temporary types
              // hashset.
              logger.config(stType + " is " + stCurrentType);
              tempTypes.add(stCurrentType);
              logger.config(" so type " + stCurrentType + " is added");
            } else {
              // If the selected type and one of the types whom the
              // property can belong to don't have any hierarchical
              // link : the type whom the property can belong to and
              // the selected type are added to the temporary types
              // hashset.
              logger.config("type " + stCurrentType
                  + " is just another type with the attribute " + data);
              tempTypes.add(stType);
              tempTypes.add(stCurrentType);
              logger.config("so type " + stType + " is added and type "
                  + stCurrentType + " is also added");
            }
          }
        }

        logger.fine("adding tempTypes to metasByTypes hashMap");
        metasByTypes.put(data, tempTypes);
      }
    }
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
