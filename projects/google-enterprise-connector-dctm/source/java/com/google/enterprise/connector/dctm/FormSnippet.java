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

import java.util.Arrays;
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

  private List<String> configKeys;

  private String includedObjectType;

  private String includedMeta;

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

  public FormSnippet() {
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

  public String render(Map<String, String> configMap,
      ResourceBundle resource, String rootType, List<String> docbases,
      boolean isAdvancedOn, SortedSet<String> allTypes,
      Map<String, Set<String>> metasByTypes,
      Set<String> existingProperties, Set<String> includedProperties,
      SortedSet<String> documentTypes) {
    StringBuilder buf = new StringBuilder();

    // JavaScript functions used to sync the select lists with the
    // hidden fields.
    appendStartHiddenRow(buf);
    buf.append(SCRIPT_START);
    buf.append("function insertIncludeMetas() { \n");
    buf.append("var txtIncludeMetas = document.getElementById('CM_included_meta');\n");
    buf.append("var selectedArray = new Array();\n");
    buf.append("var selObj = document.getElementById('CM_included_meta_bis');\n");

    buf.append("var i;\n");
    buf.append("var count = 0;\n");
    buf.append("for (i=0; i<selObj.options.length; i++) {\n");
    buf.append("selectedArray[count] = selObj.options[i].value;\n");
    buf.append("count++;\n");
    buf.append("}\n");
    buf.append("txtIncludeMetas.value = selectedArray;\n");
    buf.append("}\n");

    buf.append("function insertIncludeTypes() { \n");
    ///buf.append("alert('includeTypes');");
    ///buf.append("alert(document.getElementById('CM_included_object_type'));\n");
    buf.append("var txtIncludeTypes = document.getElementById('CM_included_object_type');\n");
    buf.append("var selectedArray = new Array();\n");
    buf.append("var selObj = document.getElementById('CM_included_object_type_bis');\n");

    buf.append("var i;\n");
    buf.append("var count = 0;\n");
    buf.append("for (i=0; i<selObj.options.length; i++) {\n");

    buf.append("selectedArray[count] = selObj.options[i].value;\n");
    buf.append("count++;\n");

    buf.append("}\n");
    buf.append("txtIncludeTypes.value = selectedArray;\n");
    buf.append("}\n");
    buf.append(SCRIPT_END);
    appendEndRow(buf);

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
        appendDropDownListAttribute(buf, value, resource, docbases);
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
          appendStartRow(buf, resource.getString(key), isRequired(key));
          buf.append(SELECT_START);
          appendAttribute(buf, NAME, ROOT_OBJECT_TYPE);
          buf.append(" onchange=\"");
          buf.append("document.getElementById('action_update').value='redisplay';");
          buf.append("document.body.style.cursor='wait';");
          buf.append("document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();");
          buf.append("\">\n");
          String[] baseTypes = { "dm_sysobject", "dm_document", };
          for (String type : baseTypes) {
            logger.config("Available object type: " + type);
            appendOption(buf, type, type, type.equals(rootType));
          }
          for (String type : documentTypes) {
            logger.config("Available object type: " + type);
            appendOption(buf, type, type, type.equals(rootType));
          }
          buf.append(SELECT_END);
          appendEndRow(buf);
        } else {
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

          // Properties are displayed according to the values stored
          // in the .properties file.
          appendSelectMultipleIncludeTypes(buf, rootType, allTypes,
              configMap);
        } else {
          // Properties are displayed according to the default
          // values stored in the connectorType.xml file.
          appendSelectMultipleIncludeTypes(buf, configMap);
        }
      } else if (key.equals(INCLUDED_META)) {
        // If the form is not displayed for the first time
        // (modification) and the advanced conf checkbox is checked.
        if (isAdvancedOn) {
          appendStartTableRow(buf, resource.getString(AVAILABLE_META),
              resource.getString(key));

          // Properties are displayed according to the values stored
          // in the .properties file.
          appendSelectMultipleIncludeMetadatas(buf, metasByTypes,
              existingProperties, includedProperties);

          appendEndTable(buf);
        } else {
          // Properties are displayed according to the default
          // values stored in the connectorType.xml file.
          appendSelectMultipleIncludeMetadatas(buf, configMap);
        }
        buf.append("</tbody>");
      } else {
        logger.fine("makeValidatedForm - input - " + key);
        appendStartRow(buf, resource.getString(key), isRequired(key));
        appendInput(buf, key, value, key.equals(PASSWORD_KEY));
        appendEndRow(buf);
      }
    }

    return buf.toString();
  }

  /* This method has package access for unit testing. */
  void appendDropDownListAttribute(StringBuilder buf, String value,
      ResourceBundle resource, List<String> docbases) {
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
      StringBuilder jsBuf = new StringBuilder();
      jsBuf.append("if(document.getElementById('more').style.display == 'none'){");
      jsBuf.append("if((document.getElementById('login').value != '')")
          .append("&&(document.getElementById('Password').value != '')){");
      if (isOn) {
        jsBuf.append("document.getElementById('more').style.display='';");
      } else {
        jsBuf.append("document.getElementById('action_update').value='redisplay';");
        jsBuf.append("document.body.style.cursor='wait';");
        jsBuf.append("document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();");
      }
      jsBuf.append("}else{");
      jsBuf.append("alert('")
          .append(resource.getString("advanced_configuration_error"))
          .append("');this.checked=false;");
      jsBuf.append("}");
      jsBuf.append("}else{");
      jsBuf.append("document.getElementById('more').style.display='none';");
      jsBuf.append("}");
      onClick = jsBuf.toString();
    } else {
      onClick = null;
    }

    appendCheckbox(buf, key, isOn, onClick);
    appendLabel(buf, key, label);

    appendEndRow(buf);

    if (isAdvanced) {
      if (isOn) {
        buf.append("<tbody id=\"more\">");
      } else {
        buf.append("<tbody id=\"more\" style=\"display: none\">");
      }
    }
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

    // JavaScript functions used to pass an item from a select list to
    // another one.
    // TODO: Two ways to improve this code, if it needs to run faster,
    // would be to make sure the selected items are processed in order,
    // and search the toList from the previous insertion point, and to
    // use a binary search.
    buf.append(SCRIPT_START);
    buf.append("var selOptions = new Array();\n");
    buf.append("function swap(listFrom, listTo) {");
    buf.append("fromList=document.getElementsByName(listFrom)[0];");
    buf.append("toList = document.getElementsByName(listTo)[0];");
    buf.append("var i;");
    buf.append("var count = 0;");
    ///buf.append("alert(fromList.options.length);");
    buf.append("for(i=0;i<fromList.options.length;i++){");
    buf.append("if (fromList.options[i].selected) {");
    buf.append("count++;");
    buf.append("}");
    buf.append("}");
    // TODO: Allow an empty included_object_type value, implying all
    // subtypes of the root_object_type. This fails in retrieving the
    // available properties, so that needs to be patched up.
    buf.append("if ((count == fromList.options.length)")
        .append("&&(listFrom=='CM_included_object_type_bis')){");
    buf.append("alert('You need to select at least one object type');");
    buf.append("return false;");
    buf.append("}");
    // END TODO
    buf.append("if ((count == fromList.options.length)")
        .append("&&(listFrom=='CM_included_meta_bis')){");
    buf.append("alert('You need to select at least one property');");
    buf.append("return false;");
    buf.append("}");
    buf.append("while (fromList.selectedIndex != -1) {");
    buf.append("addOption(toList,fromList.options[fromList.selectedIndex]); ");
    buf.append("fromList.options[fromList.selectedIndex] = null;");
    buf.append("}");
    buf.append("}\n");

    // Insert the moved option in alphabetic order.
    buf.append("function addOption(list, option){");
    buf.append(  "var beforeOption = null;");
    buf.append(  "for (var i = 0; i < list.length; i++) {");
    buf.append(    "if (option.value < list.options[i].value) {");
    buf.append(      "beforeOption = list.options[i];");
    buf.append(      "break;");
    buf.append(    "}");
    buf.append(  "}");
    buf.append(  "var newOption = document.createElement('option'); ");
    buf.append(  "newOption.value = option.value;");
    buf.append(  "newOption.appendChild(document.createTextNode(option.value));");
    buf.append(  "if (beforeOption == null) {");
    buf.append(    "list.appendChild(newOption);");
    buf.append(  "} else {");
    buf.append(    "list.insertBefore(newOption, beforeOption);");
    buf.append(  "}");
    // Workaround IE7 bug where the width shrinks on each insert.
    buf.append(  "list.style.width='';");
    buf.append(  "list.style.width='100%';");
    buf.append("}\n");

    buf.append("function selectAll(select){");
    buf.append("for (var i = 0; i < document.getElementById(select).length; i++)");
    buf.append("{");
    buf.append("document.getElementById(select).options[i].selected = true;");
    buf.append("}");
    buf.append("}\n");
    buf.append(SCRIPT_END);

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
    buf.append("swap('CM_included_object_type_toinclude','CM_included_object_type_bis');");
    buf.append("insertIncludeTypes();insertIncludeMetas();");
    buf.append("document.getElementById('action_update').value='redisplay';");
    buf.append("document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();");
    buf.append("document.body.style.cursor='wait';");
    appendEndOnclickButton(buf);
    appendStartOnclickButton(buf, " < ");
    buf.append("swap('CM_included_object_type_bis','CM_included_object_type_toinclude');");
    buf.append("insertIncludeTypes();insertIncludeMetas();");
    buf.append("document.getElementById('action_update').value='redisplay';");
    buf.append("document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();");
    buf.append("document.body.style.cursor='wait';");
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

  private void appendSelectMultipleIncludeTypes(StringBuilder buf,
      Map<String, String> configMap) {
    logger.fine("in appendSelectMultipleIncludeTypes");

    String stTypes = (configMap != null) ?
        configMap.get(INCLUDED_OBJECT_TYPE) : includedObjectType;
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("String included_object_type: " + stTypes);
    }
    appendHiddenInput(buf, "CM_included_object_type", "included_object_type",
        stTypes);
  }

  private void appendSelectMultipleIncludeMetadatas(StringBuilder buf,
      Map<String, Set<String>> metasByTypes,
      Set<String> existingProperties, Set<String> includedProperties) {
    logger.fine("in appendSelectMultipleIncludeMetadatas properties");

    // Creation of the select list of the available properties
    // (properties of the selected types) with the names of the
    // types it belongs to.
    appendSelectStart(buf, "included_meta_toinclude");
    Set<String> dataSet = metasByTypes.keySet();
    for (String data : dataSet) {
      ///logger.config("appendSelectMultipleIncludeMetadatas type is " + type);
      Set<String> types = metasByTypes.get(data);
      if (!includedProperties.contains(data)) {
        buf.append("<option value=\"");
        buf.append(data);
        buf.append("\" title=\"");
        for (String stType : types) {
          buf.append(stType);
          buf.append(", ");
        }
        buf.delete(buf.length() - 2, buf.length());
        buf.append("\">");
        buf.append(data);
        buf.append("</option>\n");
      }
    }
    buf.append(SELECT_END);

    buf.append(TD_END);
    buf.append(TD_START_CENTER);

    appendStartOnclickButton(buf, " > ");
    buf.append("swap('CM_included_meta_toinclude','CM_included_meta_bis');");
    buf.append("insertIncludeMetas();insertIncludeTypes();");
    appendEndOnclickButton(buf);
    appendStartOnclickButton(buf, " < ");
    buf.append("swap('CM_included_meta_bis','CM_included_meta_toinclude');");
    buf.append("insertIncludeMetas();insertIncludeTypes();");
    appendEndOnclickButton(buf);

    buf.append(TD_END);
    buf.append(TD_START);

    // Creation of the select list of the selected properties.
    // stMeta may contain properties that are no longer accessible,
    // so rebuild the string.
    appendSelectStart(buf, "CM_included_meta_bis", "included_meta_bis");
    StringBuilder buffer = new StringBuilder();
    for (String data : includedProperties) {
      if (existingProperties.contains(data)) {
        buffer.append(data).append(',');
        appendOption(buf, data, data);
      }
    }
    String stMeta = buffer.substring(0, buffer.length() - 1);
    buf.append(SELECT_END);

    appendEndRow(buf);

    appendHiddenInput(buf, "CM_included_meta", "included_meta", stMeta);
  }

  private void appendSelectMultipleIncludeMetadatas(StringBuilder buf,
      Map<String, String> configMap) {
    logger.fine("in appendSelectMultipleIncludeMetadatas defaults");

    String stMeta = (configMap != null) ?
        configMap.get(INCLUDED_META) : includedMeta;

    appendHiddenInput(buf, "CM_included_meta", "included_meta", stMeta);
  }
}
