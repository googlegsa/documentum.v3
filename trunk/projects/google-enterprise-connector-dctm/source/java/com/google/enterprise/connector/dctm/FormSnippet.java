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

import static com.google.enterprise.connector.dctm.HtmlUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

class FormSnippet {
  private static final Logger logger =
      Logger.getLogger(FormSnippet.class.getName());

  public static final String LOGIN = "login";

  public static final String PASSWORD_KEY = "Password";

  public static final String DOCBASENAME = "docbase";

  public static final String DISPLAYURL = "webtop_display_url";

  public static final String ISPUBLIC = "is_public";

  public static final String ADVANCEDCONF = "advanced_configuration";

  public static final String ACTIONUPDATE = "action_update";

  public static final String WHERECLAUSE = "where_clause";

  public static final String ROOT_OBJECT_TYPE = "root_object_type";

  public static final String AVAILABLE_OBJECT_TYPE = "available_object_type";

  public static final String INCLUDED_OBJECT_TYPE = "included_object_type";

  public static final String AVAILABLE_META = "available_meta";

  public static final String INCLUDED_META = "included_meta";

  /**
   * Checks whether the given key is a core field needed to open the
   * advanced configuration.
   *
   * @param key the property name
   */
  public static boolean isCoreConfig(String key) {
    return key.equals(LOGIN) || key.equals(PASSWORD_KEY)
        || key.equals(DOCBASENAME);
  }

  /**
   * Checks whether the given key is a required field.
   *
   * @param key the property name
   */
  public static boolean isRequired(String key) {
    return key.equals(LOGIN) || key.equals(PASSWORD_KEY)
        || key.equals(DOCBASENAME) || key.equals(DISPLAYURL);
  }

  private final Map<String, String> configMap;
  private final ResourceBundle resource;
  private final String rootType;
  private final List<String> docbases;
  private boolean isAdvancedOn;
  private final SortedSet<String> allTypes;
  private final Map<String, Set<String>> propertiesMap;
  private final Set<String> includedProperties;
  private final SortedSet<String> documentTypes;

  private List<String> configKeys;

  private String includedObjectType;

  private String includedMeta;

  public FormSnippet(Map<String, String> configMap,
      ResourceBundle resource, String rootType, List<String> docbases,
      boolean isAdvancedOn, SortedSet<String> documentTypes,
      SortedSet<String> allTypes, Map<String, Set<String>> propertiesMap,
      Set<String> includedProperties) {
    if (isAdvancedOn) {
      assert allTypes != null;
      assert propertiesMap != null;
      assert includedProperties != null;
      assert documentTypes != null;
    }

    this.configMap = configMap;
    this.resource = resource;
    this.rootType = rootType;
    this.docbases = docbases;
    this.isAdvancedOn = isAdvancedOn;
    this.allTypes = allTypes;
    this.propertiesMap = propertiesMap;
    this.includedProperties = includedProperties;
    this.documentTypes = documentTypes;
  }

  /** Constructor for the tests. */
  FormSnippet(ResourceBundle resource, List<String> docbases,
      boolean isAdvancedOn) {
    this.configMap = null;
    this.resource = resource;
    this.rootType = null;
    this.docbases = docbases;
    this.isAdvancedOn = isAdvancedOn;
    this.allTypes = null;
    this.propertiesMap = null;
    this.includedProperties = null;
    this.documentTypes = null;
  }

  /** Gets {@code isAdvancedOn} field for the tests. */
  boolean isAdvancedOn() {
    return isAdvancedOn;
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

  public String render() {
    StringBuilder buf = new StringBuilder();

    // JavaScript functions for Advanced Configuration.
    appendJavaScript(buf);

    for (String key : configKeys) {
      ///logger.config("key is " + key);
      String value = "";
      if (configMap != null) {
        value = configMap.get(key);
        ///logger.config("key " + key + " is " + value);
      }

      // LOGIN, PASSWORD_KEY, and DISPLAYURL are handled in the else block.
      if (key.equals(DOCBASENAME)) {
        logger.fine("docbase droplist");
        appendStartRow(buf, resource.getString(key), isRequired(key));
        appendDropDownListAttribute(buf, value);
        appendEndRow(buf);
      } else if (key.equals(ISPUBLIC)) {
        if (value != null && value.equals("on")) {
          appendCheckboxRow(buf, key, resource.getString(key), value, resource);
        }
      } else if (key.equals(ADVANCEDCONF)) {
        appendCheckboxRow(buf, ADVANCEDCONF,
            "<b>" + resource.getString(key) + "</b>", value, resource);
      } else if (key.equals(ACTIONUPDATE)) {
        appendHiddenInput(buf, key, key, "");
      } else if (key.equals(ROOT_OBJECT_TYPE)) {
        logger.fine("makeValidatedForm - rootObjectType");
        if (isAdvancedOn) {
          appendStartTbody(buf, "more", null);

          appendStartRow(buf, resource.getString(key), isRequired(key));
          buf.append(SELECT_START);
          appendAttribute(buf, NAME, ROOT_OBJECT_TYPE);
          buf.append(" onchange=\"");
          buf.append("redisplayFormSnippet();");
          buf.append("\">\n");
          String[] baseTypes = { "dm_sysobject", "dm_document", };
          for (String type : baseTypes) {
            logger.config("Available object type: " + type);
            appendOption(buf, type, type, type.equals(rootType));
          }
          for (String type : documentTypes) {
            logger.config("Available root object type: " + type);
            appendOption(buf, type, type, type.equals(rootType));
          }
          buf.append(SELECT_END);
          appendEndRow(buf);
        } else {
          appendStartTbody(buf, "more", "none");

          appendHiddenInput(buf, key, rootType);
        }
      } else if (key.equals(WHERECLAUSE)) {
        logger.fine("where clause");
        appendStartTextareaRow(buf, resource.getString(key));
        appendTextarea(buf, WHERECLAUSE, value);
      } else if (key.equals(INCLUDED_OBJECT_TYPE)) {
        if (isAdvancedOn) {
          appendStartTable(buf);
          appendStartTableRow(buf, resource.getString(AVAILABLE_OBJECT_TYPE),
              resource.getString(key));

          appendSelectMultipleIncludeTypes(buf, rootType, allTypes,
              configMap);
        } else {
          appendHiddenIncludeTypes(buf, configMap);
        }
      } else if (key.equals(INCLUDED_META)) {
        // If the form is not displayed for the first time
        // (modification) and the advanced conf checkbox is checked.
        if (isAdvancedOn) {
          appendStartTableRow(buf, resource.getString(AVAILABLE_META),
              resource.getString(key));

          appendSelectMultipleIncludeMetadatas(buf, propertiesMap,
              includedProperties);

          appendEndTable(buf);
        } else {
          appendHiddenIncludeMetadatas(buf, configMap);
        }

        appendEndTbody(buf);
      } else {
        logger.fine("makeValidatedForm - input - " + key);
        appendStartRow(buf, resource.getString(key), isRequired(key));
        appendInput(buf, key, value, key.equals(PASSWORD_KEY));
        appendEndRow(buf);
      }
    }

    return buf.toString();
  }

  /** Writes the JavaScript functions. */
  /* @VisibleForTesting */
  void appendJavaScript(StringBuilder buf) {
    appendStartHiddenRow(buf);
    buf.append(SCRIPT_START);
    String[] messages = {
      "advanced_configuration_error",
      "included_object_type_error",
      "included_meta_error"
    };
    for (String message : messages) {
      buf.append("var ")
          .append(message)
          .append(" = '")
          .append(resource.getString(message))
          .append("';\n");
    }

    // TODO: Cache the JavaScript file, perhaps in SoftReference.
    // TODO: We should throw RepositoryException here and let
    // DctmConnectorType handle that generically.
    boolean isError = false;
    InputStream in = getJavaScript();
    if (in == null) {
      isError = true;
    } else {
      Reader reader = new InputStreamReader(in);
      int count;
      char[] buffer = new char[4096];
      try {
        try {
          while ((count = reader.read(buffer)) != -1) {
            buf.append(buffer, 0, count);
          }
        } finally {
          reader.close();
        }
      } catch (IOException e) {
        isError = true;
      }
    }
    buf.append(SCRIPT_END);
    appendEndRow(buf);
    if (isError) {
      buf.append(resource.getString("javascript_error"));
      isAdvancedOn = false;
    }
  }

  /* @VisibleForTesting */
  protected InputStream getJavaScript() {
    return FormSnippet.class.getResourceAsStream("FormSnippet.js");
  }

  /* @VisibleForTesting */
  void appendDropDownListAttribute(StringBuilder buf, String value) {
    buf.append(SELECT_START);
    appendAttribute(buf, NAME, DOCBASENAME);
    buf.append(">\n");

    // If there are zero or more one docbase available and none
    // selected, then add an empty to the top of the list as the
    // selected option so that users must make an explicit choice.
    // Otherwise, if there is a configured docbase but it does not
    // appear in the list, add the configured docbase along with a
    // message to the top of the list as the selected option.
    int count = docbases.size();
    if ("".equals(value) && count != 1) {
      appendOption(buf, "", "", true);
    } else if (!"".equals(value) && !docbases.contains(value)) {
      String label = value + " (" + resource.getString("docbase_error") + ")";
      appendOption(buf, value, label, true);
      count++; // Hack to avoid selecting the wrong docbase below.
    }

    for (String docbase : docbases) {
      appendOption(buf, docbase, docbase, count == 1 || docbase.equals(value));
    }

    buf.append(SELECT_END);
  }

  private void appendCheckboxRow(StringBuilder buf, String key, String label,
      String value, ResourceBundle resource) {
    boolean isAdvanced = key.equals(ADVANCEDCONF);
    boolean isOn = value != null && value.equals("on");

    buf.append(TR_START);
    if (isAdvanced) {
      buf.append("<td colspan='2' style='padding-top: 3ex'>");
    } else {
      buf.append(TD_START_COLSPAN);
    }

    String onClick;
    if (isAdvanced) {
      logger.config("advanced conf set to " + value);
      onClick = "clickAdvancedConfiguration(this, " + isOn + ");";
    } else {
      onClick = null;
    }

    appendCheckbox(buf, key, isOn, onClick);
    appendLabel(buf, key, label);

    appendEndRow(buf);
  }

  private void appendSelectMultipleIncludeTypes(StringBuilder buf,
      String rootType, SortedSet<String> allTypes,
      Map<String, String> configMap) {
    logger.fine("in SelectMultipleIncludeTypes with collection parameter");
    String stTypes = configMap.get(INCLUDED_OBJECT_TYPE);
    String[] typeList = stTypes.split(",");

    Set<String> includedTypes = new TreeSet<String>();
    for (String stType : typeList) {
      includedTypes.add(stType.trim());
    }

    // TODO: Allow an empty included_object_type value, implying all
    // subtypes of the root_object_type. This fails in retrieving the
    // available properties, so that needs to be patched up.
    // Also, we could allow an empty included_meta value, but in that
    // case I think it intuitively implies that no properties will be
    // indexed, whereas it logically implies that all properties will
    // be indexed. I think the best thing is to insert an optgroup or
    // something indicating that that an empty included object type
    // list implies all subtypes of the root object type.

    // Remove any included types not in the full list. If the complete
    // list is removed, add back the root object type.
    StringBuilder buffer = new StringBuilder();
    for (Iterator<String> i = includedTypes.iterator(); i.hasNext(); ) {
      String type = i.next();
      if (!allTypes.contains(type)) {
        i.remove();
      } else {
        buffer.append(type).append(',');
      }
    }
    if (includedTypes.isEmpty()) {
      includedTypes.add(rootType);
      stTypes = rootType;
    } else {
      stTypes = buffer.substring(0, buffer.length() - 1);
    }
    configMap.put(INCLUDED_OBJECT_TYPE, stTypes);

    // Print the available types.
    appendSelectStart(buf, "included_object_type_toinclude");
    for (String type : allTypes) {
      if (!includedTypes.contains(type)) {
        logger.config("Available object type: " + type);
        appendOption(buf, type, type);
      }
    }
    buf.append(SELECT_END);

    buf.append(TD_END);
    buf.append(TD_START_CENTER);

    appendStartOnclickButton(buf, " > ");
    buf.append("swap('CM_included_object_type_toinclude', ")
        .append("'CM_included_object_type_bis', null, ")
        .append("'CM_included_object_type_bis', 'CM_included_object_type', ")
        .append("true);");
    appendEndOnclickButton(buf);
    appendStartOnclickButton(buf, " < ");
    buf.append("swap('CM_included_object_type_bis', ")
        .append("'CM_included_object_type_toinclude', ")
        .append("included_object_type_error")
        .append(", 'CM_included_object_type_bis', 'CM_included_object_type', ")
        .append("true);");
    appendEndOnclickButton(buf);

    buf.append(TD_END);
    buf.append(TD_START);

    // Creation of the select list of the types previously selected.
    appendSelectStart(buf, "CM_included_object_type_bis",
        "included_object_type_bis");
    for (String type : includedTypes) {
      ///logger.config("appendSelectMultipleIncludeTypes type is " + type);
      appendOption(buf, type, type);
    }
    buf.append(SELECT_END);

    appendEndRow(buf);

    if (logger.isLoggable(Level.FINE)) {
      logger.fine("String included_object_type: " + stTypes);
    }
    appendHiddenInput(buf, "CM_included_object_type", "included_object_type",
        stTypes);
  }

  private void appendHiddenIncludeTypes(StringBuilder buf,
      Map<String, String> configMap) {
    String stTypes = (configMap != null) ?
        configMap.get(INCLUDED_OBJECT_TYPE) : includedObjectType;
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("included_object_type: " + stTypes);
    }
    appendHiddenInput(buf, "CM_included_object_type", "included_object_type",
        stTypes);
  }

  private void appendSelectMultipleIncludeMetadatas(StringBuilder buf,
      Map<String, Set<String>> propertiesMap, Set<String> includedProperties) {
    logger.fine("in appendSelectMultipleIncludeMetadatas properties");

    // Creation of the select list of the available properties
    // (properties of the selected types) with the names of the
    // types it belongs to.
    appendSelectStart(buf, "included_meta_toinclude");
    Set<String> propertyNames = propertiesMap.keySet();
    for (String propertyName : propertyNames) {
      if (!includedProperties.contains(propertyName)) {
        buf.append("<option value=\"");
        buf.append(propertyName);
        buf.append("\" title=\"");
        Set<String> types = propertiesMap.get(propertyName);
        for (String typeName : types) {
          buf.append(typeName);
          buf.append(", ");
        }
        buf.delete(buf.length() - 2, buf.length());
        buf.append("\">");
        buf.append(propertyName);
        buf.append("</option>\n");
      }
    }
    buf.append(SELECT_END);

    buf.append(TD_END);
    buf.append(TD_START_CENTER);

    appendStartOnclickButton(buf, " > ");
    buf.append("swap('CM_included_meta_toinclude','CM_included_meta_bis', ")
        .append("null, 'CM_included_meta_bis', 'CM_included_meta');");
    appendEndOnclickButton(buf);
    appendStartOnclickButton(buf, " < ");
    buf.append("swap('CM_included_meta_bis','CM_included_meta_toinclude', ")
        .append("included_meta_error, 'CM_included_meta_bis', ")
        .append("'CM_included_meta');");
    appendEndOnclickButton(buf);

    buf.append(TD_END);
    buf.append(TD_START);

    // Creation of the select list of the selected properties.
    // stMeta may contain properties that are no longer accessible,
    // so rebuild the string.
    appendSelectStart(buf, "CM_included_meta_bis", "included_meta_bis");
    StringBuilder buffer = new StringBuilder();
    for (String propertyName : includedProperties) {
      if (propertiesMap.containsKey(propertyName)) {
        buffer.append(propertyName).append(',');
        appendOption(buf, propertyName, propertyName);
      }
    }
    String stMeta = buffer.substring(0, buffer.length() - 1);
    buf.append(SELECT_END);

    appendEndRow(buf);

    appendHiddenInput(buf, "CM_included_meta", "included_meta", stMeta);
  }

  private void appendHiddenIncludeMetadatas(StringBuilder buf,
      Map<String, String> configMap) {
    String stMeta = (configMap != null) ?
        configMap.get(INCLUDED_META) : includedMeta;
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("included_meta: " + stMeta);
    }
    appendHiddenInput(buf, "CM_included_meta", "included_meta", stMeta);
  }
}
