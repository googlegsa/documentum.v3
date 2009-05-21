// Copyright (C) 2006-2009 Google Inc.
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IDocbaseMap;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.spi.ConfigureResponse;
import com.google.enterprise.connector.spi.ConnectorFactory;
import com.google.enterprise.connector.spi.ConnectorType;
import com.google.enterprise.connector.spi.RepositoryException;

public class DctmConnectorType implements ConnectorType {
  private static final String HIDDEN = "hidden";

  private static final String VALUE = "value";

  private static final String NAME = "name";

  private static final String TEXT = "text";

  private static final String TYPE = "type";

  private static final String INPUT = "input";

  private static final String CLOSE_ELEMENT = "/>";

  private static final String OPEN_ELEMENT = "<";

  private static final String TR_END = "</tr>\r\n";

  private static final String TD_END = "</td>\r\n";

  private static final String TD_START = "<td>";

  private static final String TD_START_LABEL = "<td style='white-space: nowrap'>";

  private static final String TD_START_COLSPAN = "<td colspan='2'>";

  private static final String TR_START = "<tr>\r\n";

  private static final String TR_START_HIDDEN = "<tr style='display: none'>\r\n";

  private static final String SELECT_START = "<select";

  private static final String SELECT_END = "</select>\r\n";

  private static final String SELECTED = "selected='selected' ";

  private static final String TEXTAREA_START = "<textarea";

  private static final String TEXTAREA_END = "</textarea>\r\n";

  private static final String SCRIPT_START =
      "<script type=\"text/javascript\"><![CDATA[\n";

  private static final String SCRIPT_END = "]]></script>\n";

  private static final String DCTMCLASS = "clientX";

  private static final String AUTHENTICATIONTYPE = "authentication_type";

  private static final String ISPUBLIC = "is_public";

  private static final String WHERECLAUSE = "where_clause";

  private static final String ADVANCEDCONF = "advanced_configuration";

  private static final String ACTIONUPDATE = "action_update";

  private static final String DOCBASENAME = "docbase";

  private static final String DISPLAYURL = "webtop_display_url";

  private static final String INCLUDED_META = "included_meta";

  private static final String INCLUDED_OBJECT_TYPE = "included_object_type";

  private static final String ROOT_OBJECT_TYPE = "root_object_type";

  private static final String LOGIN = "login";

  private static final String PASSWORD = "password";

  private static final String PASSWORD_KEY = "Password";

  private static final String CHECKBOX = "checkbox";

  private static final String CHECKED = "checked='checked'";

  private static final String ID = "id";

  private IClient client;

  private IClientX cl = null;

  private ISession sess = null;

  private ISessionManager sessMag = null;

  private List<String> keys = null;

  private Set<String> keySet = null;

  private String initialConfigForm = null;

  private Set<String> included_object_type = null;

  private Set<String> included_meta = null;

  private String root_object_type = null;

  private String authentication_type = null;

  private String clientX = null;

  private static Logger logger = Logger.getLogger(DctmConnectorType.class
      .getName());

  private ResourceBundle resource;

  /**
   * Set the keys that are required for configuration. One of the overloadings
   * of this method must be called exactly once before the SPI methods are
   * used.
   *
   * @param keys a list of String keys
   */
  public void setConfigKeys(List<String> keys) {
    logger.fine("setConfigKeys List");
    if (this.keys != null) {
      throw new IllegalStateException();
    }
    this.keys = keys;
    this.keySet = new HashSet<String>(keys);
  }

  /**
   * Set the keys that are required for configuration. One of the overloadings
   * of this method must be called exactly once before the SPI methods are
   * used.
   *
   * @param keys an array of String keys
   */
  public void setConfigKeys(String[] keys) {
    logger.fine("setConfigKeys keys");
    setConfigKeys(Arrays.asList(keys));
  }

  public void setIncluded_meta(Set<String> included_meta) {
    this.included_meta = included_meta;
    logger.config("included_meta set to " + included_meta);
  }

  public void setIncluded_object_type(Set<String> included_object_type) {
    this.included_object_type = included_object_type;
    logger.config("included_object_type set to " + included_object_type);
  }

  public Set<String> getIncluded_meta() {
    return included_meta;
  }

  public Set<String> getIncluded_object_type() {
    return included_object_type;
  }

  public void setRoot_object_type(String root_object_type) {
    this.root_object_type = root_object_type;
    logger.config("root_object_type set to " + root_object_type);
  }

  public String getRoot_object_type() {
    return root_object_type;
  }

  public ConfigureResponse getConfigForm(Locale language) {
    try {
      resource = ResourceBundle.getBundle("DctmConnectorResources",
          language);
    } catch (MissingResourceException e) {
      resource = ResourceBundle.getBundle("DctmConnectorResources");
    }
    if (initialConfigForm != null) {
      return new ConfigureResponse("", initialConfigForm);
    }
    if (keys == null) {
      throw new IllegalStateException();
    }

    this.initialConfigForm = makeValidatedForm(null);

    return new ConfigureResponse("", initialConfigForm);
  }

  public ConfigureResponse validateConfig(Map<String, String> configData,
      Locale language, ConnectorFactory connectorFactory) {
    sess = null;
    sessMag = null;

    try {
      resource = ResourceBundle.getBundle("DctmConnectorResources",
          language);
    } catch (MissingResourceException e) {
      resource = ResourceBundle.getBundle("DctmConnectorResources");
    }
    String form = null;

    String validation = validateConfigMap(configData);
    String additionalWhereClause = null;
    boolean where_clause_config = false;
    if (validation.equals("")) {
      try {
        logger.config("CONFIG DATA is " + getMaskedMap(configData));

        setSessionManager(configData);

        ILoginInfo myinfo = sessMag.getIdentity(configData.get(DOCBASENAME));
        String pass = myinfo.getPassword();
        String user = myinfo.getUser();
        logger.config("login user: " + user);

        setSession(configData);

        logger.fine("test connection to the repository");

        String isPublic = configData.get(ISPUBLIC);
        if (isPublic == null) {
          configData.put(isPublic, "false");
        }

        if (logger.isLoggable(Level.FINE)) {
          logger.fine("sess before testwebtop: " + sess);
        }
        testWebtopUrl(configData.get(DISPLAYURL));
        if (logger.isLoggable(Level.FINE)) {
          logger.fine("sess after testwebtop: " + sess);
        }

        // display the form again when the advanced conf checkbox is checked and before that the user had saved the configuration
        if ((configData.get(ADVANCEDCONF).equals("on")
                && (configData.get(ACTIONUPDATE).equals("checkadvconf")
                    || configData.get(ACTIONUPDATE).equals("addmeta")))
            || configData.get(ACTIONUPDATE).equals("uncheckadvconf")) {
          logger.config("CASE ADVANCEDCONF SET TO on or ACTIONUPDATE set to uncheckadvconf");

          form = makeValidatedForm(configData);
          return new ConfigureResponse("", form);
        }

        if (configData.get(WHERECLAUSE) != null
            && !configData.get(WHERECLAUSE).equals("")) {
          where_clause_config = true;
          additionalWhereClause =
              checkAdditionalWhereClause(configData.get("where_clause"));

          configData.put("where_clause", additionalWhereClause);
          configData.put(ACTIONUPDATE, "checkadvconf");
          logger.config("where_clause is now " + additionalWhereClause);
          logger.config("after set properties: where_clause of configData is now " + configData.get("where_clause"));
        }
      } catch (RepositoryException e) {
        if (where_clause_config == false || (where_clause_config == true && additionalWhereClause != null)) {
          // If we get an exception and we haven't started checking
          // the where clause yet, then there's a problem with the core
          // configuration. We will turn the advanced configuration off
          // until the user fixes the problem.
          configData.put(ADVANCEDCONF, "off");
          logger.config("ADVANCEDCONF set to " + configData.get(ADVANCEDCONF));
        }

        logger.log(Level.SEVERE, "RepositoryException thrown in validateConfig: ", e);
        // return the config form with an error message (written in red)
        return createErrorMessage(configData, e);
      } finally {
        if (sess != null) {
          sessMag.releaseSessionConfig();
          logger.fine("Release sessionConfig");
        }
      }

      if (logger.isLoggable(Level.FINE)) {
        logger.fine("sess before return null: " + sess);
      }
      return null;
    }

    // return the config form with an error message (written in red) indicating the name of the missing parameter
    logger.config("if validation returns smthg");
    logger.config("sess: " + sess);
    configData.put(ADVANCEDCONF, "off");

    form = makeValidatedForm(configData);
    return new ConfigureResponse(resource.getString(validation + "_error"),
        form);
  }

  public ConfigureResponse getPopulatedConfigForm(
      Map<String, String> configMap, Locale language)  {
    logger.fine("getPopulatedConfigForm");
    ConfigureResponse result = null;

    try {
      setSessionManager(configMap);
      setSession(configMap);

      for (String key : keys) {
        String val = configMap.get(key);
        if (key.equals("advanced_configuration") && val == null) {
          logger.config("advanced_configuration null");
          ///val = getAdvanced_configuration();
          val = "off";
          logger.config("val advanced_configuration is now " + val);
          configMap.put(key, val);
          logger.fine("added in configMap");
        } else if (key.equals("action_update") && val == null) {
          logger.config("action_update null");
          ///val = getAction_update();
          val = "save";
          logger.config("val action_update is now " + val);
          configMap.put(key, val);
          logger.fine("added in configMap");
        } else if (key.equals("clientX") && (val == null || val.equals(""))) {
          logger.config("clientX null or empty");
          val = getClientX();
          logger.config("val clientX is now " + val);
          configMap.put(key, val);
          logger.fine("added in configMap");
        } else if (key.equals("authentication_type") && (val == null || val.equals(""))) {
          logger.config("authentication_type null or empty");
          val = getAuthentication_type();
          logger.config("val authentication_type is now " + val);
          configMap.put(key, val);
          logger.fine("added in configMap");
        } else if (key.equals("root_object_type") && val == null) {
          logger.config("root_object_type null or empty");
          val = getRoot_object_type();
          logger.config("val root_object_type is now " + val);
          configMap.put(key, val);
          logger.fine("added in configMap");
        }
      }

      if (logger.isLoggable(Level.CONFIG)) {
        logger.config("Before spring process: " + getMaskedMap(configMap));
      }

      try {
        resource = ResourceBundle.getBundle("DctmConnectorResources",
            language);
      } catch (MissingResourceException e) {
        resource = ResourceBundle.getBundle("DctmConnectorResources");
      }

      result = new ConfigureResponse("", makeValidatedForm(configMap));
    } catch (RepositoryException e1) {
      logger.log(Level.WARNING, "Error building the configuration form", e1);
    } finally {
      if (sess != null) {
        sessMag.releaseSessionConfig();
        logger.fine("Release sessionConfig");
      }
    }

    return result;
  }

  /**
   * @param key key name to test
   * @return true if the given key name describes a sensitive field
   */
  /* Copied from com.google.enterprise.connector.common.SecurityUtils. */
  private boolean isKeySensitive(String key) {
    return key.toLowerCase().indexOf("password") != -1;
  }

  /**
   * Gets a copy of the map with password property values masked.
   *
   * @param original a property map
   * @return a copy of the map with password property values
   * replaced by the string "[...]"
   */
  /* Copied from com.google.enterprise.connector.otex.LivelinkConnectorType. */
  private Map<String, String> getMaskedMap(Map<String, String> original) {
    HashMap<String, String> copy = new HashMap<String, String>();
    for (Map.Entry<String, String> entry : original.entrySet()) {
      String key = entry.getKey();
      if (isKeySensitive(key))
        copy.put(key, "[...]");
      else
        copy.put(key, entry.getValue());
    }
    return copy;
  }

  private ConfigureResponse createErrorMessage(Map<String, String> configData,
      RepositoryException e) {
    String form;
    String message = e.getMessage();
    String extractErrorMessage = null;
    String bundleMessage = null;
    if (message.indexOf("[") != -1) {
      extractErrorMessage = message.substring(message.indexOf("[") + 1,
          message.indexOf("]"));
    } else {
      extractErrorMessage = e.getCause().getClass().getName();
    }
    try {
      bundleMessage = resource.getString(extractErrorMessage);
    } catch (MissingResourceException mre) {
      bundleMessage = resource.getString("DEFAULT_ERROR_MESSAGE") + " "
          + e.getMessage();
    }
    logger.warning(bundleMessage);

    form = makeValidatedForm(configData);
    return new ConfigureResponse(bundleMessage, form);
  }

  private String checkAdditionalWhereClause(String additionalWhereClause) throws RepositoryException {
    ICollection collec = null;
    int counter = 0;

    try {
      logger.config("check additional where clause: " + additionalWhereClause);

      IQuery query = cl.getQuery();
      String dql = "select r_object_id from dm_sysobject where r_object_type='dm_document' ";
      if (!additionalWhereClause.toLowerCase().startsWith("and ")) {
        query.setDQL(dql + "and "
            + additionalWhereClause);
      } else {
        query.setDQL(dql + additionalWhereClause);
      }

      collec = query.execute(sessMag, IQuery.EXECUTE_READ_QUERY);
      while (collec.next()) {
        counter++;
        break;
      }
    } catch (RepositoryException re) {
      // TODO(jl1615): Log and throw anti-pattern.
      logger.info("throw " + re);
      throw re;
    } finally {
      try {
        if (collec != null) {
          if (collec.getState() != ICollection.DF_CLOSED_STATE) {
            collec.close();
            logger.fine("after closing the collection");
          }
        }
      } catch (RepositoryException re) {
        // TODO(jl1615): Why bother with this? Should we log and not throw?
        throw re;
      }
    }

    if (counter == 0) {
      throw new RepositoryException("[additionalTooRestrictive]");
    }

    return additionalWhereClause;
  }

  private void testWebtopUrl(String webtopServerUrl)
      throws RepositoryException {
    logger.config("test connection to the webtop server: "
        + webtopServerUrl);
    try {
      new UrlValidator().validate(webtopServerUrl);
    } catch (UrlValidatorException e) {
      throw new RepositoryException(
          "[status] Http request returned a " + e.getStatusCode()
          + " status");
    } catch (GeneralSecurityException e) {
      throw new RepositoryException("[HttpException]", e);
    } catch (IllegalArgumentException e) {
      // TODO(jl1615): There's no resource bundle entry for this one,
      // unlike HttpException and IOException.
      throw new RepositoryException("[IllegalArgumentException]", e);
    } catch (IOException e) {
      throw new RepositoryException("[IOException]", e);
    }
  }

  private String validateConfigMap(Map<String, String> configData) {
    for (String key : keys) {
      String val = configData.get(key);
      if (!key.equals(DCTMCLASS) && !key.equals(AUTHENTICATIONTYPE)
          && !key.equals(WHERECLAUSE) && !key.equals(ISPUBLIC) && !key.equals(INCLUDED_OBJECT_TYPE)
          && !key.equals(INCLUDED_META) && !key.equals(ROOT_OBJECT_TYPE) && !key.equals(WHERECLAUSE)
          && (val == null || val.length() == 0)) {
        return key;
      }
    }
    return "";
  }

  private String makeValidatedForm(Map<String, String> configMap) {
    StringBuilder buf = new StringBuilder();

    String value = "";
    Set<String> hashIncludedMeta;
    Set<String> hashIncludedType;
    String rootType = "";
    String actionUpdate = "";
    String returnMessage = "";

    int cpt = 0;
    ICollection collecTypes = null;
    String advConf = "";

    logger.fine("in makeValidatedForm");

    //javascript functions used to
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

    try {
      //if configmap is not null : it is not the first time the form is displayed
      //parameters are loaded from the .properties file
      if ((configMap != null)) {
        rootType = configMap.get(ROOT_OBJECT_TYPE);
        logger.config("rootType from configmap: " + rootType);
        advConf = configMap.get(ADVANCEDCONF);
        logger.config("advConf from configmap: " + advConf);
        actionUpdate = configMap.get(ACTIONUPDATE);
        logger.config("actionUpdate from configmap: " + actionUpdate);
      } else {
        //if configmap is null : it is the first time the form is displayed.
        //parameters are loaded from the connectorType.xml file
        logger.config("value de rootObjectType null: get from connectorType");
        rootType = getRoot_object_type();
        logger.config("rootType from getRoot_object_type(): " + rootType);
        logger.config("rootObjectType: " + rootType);
      }

      //loop of the config parameters
      for (String key : keys) {
        ///logger.config("key vaut " + key);
        if (configMap != null) {
          value = configMap.get(key);
          ///logger.config("key " + key + " vaut " + value);
        }
        if (key.equals(ISPUBLIC)) {
          appendCheckBox(buf, key, resource.getString(key), value);
          appendHiddenInput(buf, key, "false");
          value = "";
        } else {
          if(!key.equals(ADVANCEDCONF)) {
            if (!key.equals(DCTMCLASS) && !key.equals(AUTHENTICATIONTYPE)) {
              appendStartRow(buf, resource.getString(key));
            } else {
              //creation of a hidden fields for the checkboxes
              appendStartHiddenRow(buf);
            }
          }
          if (key.equals(DOCBASENAME)) {
            logger.fine("docbase droplist");
            appendDropDownListAttribute(buf, TYPE, value);
            appendEndRow(buf);
          } else if (key.equals(INCLUDED_META)) {
            //if the form is not displayed for the first time (modification) and the advanced conf checkbox is checked
            if ((sess != null) && (!actionUpdate.equals("uncheckadvconf") && (advConf.equals("on")))) {
            ///if ((sess != null) && (!actionUpdate.equals("uncheckadvconf"))) {
              logger.config("cas actionUpdate not uncheckadvconf");

              //properties are displayed according to the values stored in the .properties file
              appendSelectMultipleIncludeMetadatas(buf, INCLUDED_META, configMap);

              logger.fine("after closing the collection");

              buf.append("</tbody>");
            } else {
              logger.config("cas actionUpdate uncheckadvconf or no sess");

              //properties from connectorType.xml file are loaded in a hashset
              hashIncludedMeta = getSetfromXML(INCLUDED_META);

              //properties are displayed according to the default values stored in the connectorType.xml file
              appendSelectMultipleIncludeMetadatas(buf, INCLUDED_META, hashIncludedMeta);

              buf.append("</tbody>");
            }
          ///} else if (key.equals(ACTIONUPDATE)) {
          } else if (key.equals(ADVANCEDCONF)) {
            logger.fine("advanced config");
            appendCheckBox(buf, ADVANCEDCONF, resource. getString(key), value);
          } else if (key.equals(WHERECLAUSE)) {
            logger.fine("where clause");
            appendTextarea(buf, WHERECLAUSE, value);
          } else if (key.equals(INCLUDED_OBJECT_TYPE)) {
            if ((sess != null) && (!actionUpdate.equals("uncheckadvconf") && (advConf.equals("on")))) {
            ///if ((sess != null) && (!actionUpdate.equals("uncheckadvconf"))) {
              logger.config("cas actionUpdate = " + actionUpdate);

              //creation of a collection of all Dctm types whose super_name attribute is not empty
              collecTypes = getListOfTypes(rootType);

              //properties are displayed according to the values stored in the .properties file
              appendSelectMultipleIncludeTypes(buf, INCLUDED_OBJECT_TYPE, collecTypes, configMap);
            } else {
              logger.config("cas actionUpdate uncheckadvconf or no sess");
              logger.config("cas actionUpdate " + actionUpdate);
              logger.config("cas advConfe " + advConf);

              //properties from connectorType.xml file are loaded in a hashset
              hashIncludedType = getSetfromXML(INCLUDED_OBJECT_TYPE);
              logger.config("after getSetfromXML de " + INCLUDED_OBJECT_TYPE);

              //properties are displayed according to the default values stored in the connectorType.xml file
              appendSelectMultipleIncludeTypes(buf, INCLUDED_OBJECT_TYPE, hashIncludedType, rootType);

              logger.fine("after appendSelectMultiple");
            }
          } else if (key.equals(ROOT_OBJECT_TYPE)) {
            logger.fine("makeValidatedForm - rootObjectType");

            buf.append(rootType);
            appendEndRow(buf);

            appendHiddenInput(buf, key, rootType);
          } else {
            logger.fine("other stuff");
            buf.append(OPEN_ELEMENT);
            buf.append(INPUT);
            if (key.equals(PASSWORD_KEY)) {
              appendAttribute(buf, TYPE, PASSWORD);
              if (configMap != null && configMap.get("password") != null) {
                value = configMap.get("password");
                configMap.remove("password");
              }
            } else if (key.equals(DCTMCLASS)) {
              appendAttribute(buf, TYPE, HIDDEN);
              value = getClientX();
            } else if (key.equals(AUTHENTICATIONTYPE)) {
              appendAttribute(buf, TYPE, HIDDEN);
              value = getAuthentication_type();
            } else if (key.equals(ACTIONUPDATE)) {
              appendAttribute(buf, TYPE, HIDDEN);
              value = "save";
            } else {
              appendAttribute(buf, TYPE, TEXT);
            }
            appendAttribute(buf, VALUE, value);
            appendAttribute(buf, NAME, key);
            appendAttribute(buf, ID, key);
            buf.append(CLOSE_ELEMENT);
            appendEndRow(buf);
            value = "";
          }
        }
      }

      /*
      if (configMap != null) {
        for (String key : new TreeSet<String>(configMap.keySet())) {
          if (!keySet.contains(key)) {
            // add another hidden field to preserve this data
            String val = configMap.get(key);
            buf.append("<input type=\"hidden\" value=\"");
            buf.append(val);
            buf.append("\" name=\"");
            buf.append(key);
            buf.append("\" />\r\n");
          }
        }
      }
      */
    } catch (RepositoryException e1) {
      logger.log(Level.WARNING, "Error building the configuration form", e1);
    }

    return buf.toString();
  }

  private void setSessionManager(Map<String, String> logMap)
      throws RepositoryException {
    ILoginInfo loginInfo;
    try {
      cl = (IClientX) Class.forName(
          "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX")
          .newInstance();
    } catch (InstantiationException e) {
      logger.log(
          Level.SEVERE,
          "error while building the configuration form. The docbase will be added manually. ",
          e);
    } catch (IllegalAccessException e) {
      logger.log(
          Level.SEVERE,
          "error while building the configuration form. The docbase will be added manually. ",
          e);
    } catch (ClassNotFoundException e) {
      logger.log(
          Level.SEVERE,
          "error while building the configuration form. The docbase will be added manually. ",
          e);
    } catch (NoClassDefFoundError e) {
      logger.log(
          Level.SEVERE,
          "error while building the configuration form. The docbase will be added manually. ",
          e);
    }

    client = cl.getLocalClient();
    sessMag = client.newSessionManager();
    sessMag.clearIdentity(logMap.get(DOCBASENAME));
    logger.config("after clearIdentity for docbase: "
        + logMap.get(DOCBASENAME));
    loginInfo = cl.getLoginInfo();
    loginInfo.setUser(logMap.get(LOGIN));
    logger.config("after setIdentity for login: " + logMap.get(LOGIN));
    loginInfo.setPassword(logMap.get(PASSWORD_KEY));
    sessMag.setIdentity(logMap.get(DOCBASENAME), loginInfo);
    sessMag.setDocbaseName(logMap.get(DOCBASENAME));
    logger.config("after setIdentity for docbase: " + logMap.get(DOCBASENAME));
    logger.config("new sessionManager");
  }

  private void setSession(Map<String, String> logMap)
      throws RepositoryException {
    logger.config("login vaut " + logMap.get(LOGIN));
    logger.config("docbase vaut " + logMap.get(DOCBASENAME));

    sess = sessMag.getSession(logMap.get(DOCBASENAME));
    sessMag.setSessionConfig(sess);
  }

  private Set<String> getSetfromXML(String setName) {
    Set<String> hash = new HashSet<String>();
    logger.fine("in getSetfromXML");

    if (setName.equals(INCLUDED_META)) {
      logger.config("dans included_meta");
      hash = getIncluded_meta();
    } else if (setName.equals(INCLUDED_OBJECT_TYPE)) {
      logger.config("dans Included_object_type");
      hash = getIncluded_object_type();
    }
    return hash;
  }

  private ICollection getListOfTypes(String root_object_type) {
    IQuery que = null;
    String queryString = "";
    ICollection collec = null;
    try {
      logger.config("docbase of sessMag vaut " + sessMag.getDocbaseName());
      que = cl.getQuery();
      queryString = "select * from dm_type where super_name != '' order by r_object_id";
      logger.config("queryString: " + queryString);
      que.setDQL(queryString);

      boolean auth = sessMag.authenticate(sessMag.getDocbaseName());
      logger.config("AUTH VAUT " + auth);

      collec = que.execute(sessMag, IQuery.EXECUTE_READ_QUERY);
    } catch (RepositoryException e) {
      // TODO Auto-generated catch block
      logger.log(Level.WARNING, "Error getting the list of types", e);
    } catch (NoClassDefFoundError e) {
      logger.log(Level.SEVERE,
          "error while building the configuration form. The docbase will be added manually. ",
          e);
    }
    return collec;
  }

  private void appendSelectMultipleIncludeTypes(StringBuilder buf, String name,
      ICollection collecTypes, Map<String, String> configMap)
      throws RepositoryException {
    logger.fine("in SelectMultipleIncludeTypes with collection parameter");
    String super_type;
    String stTypes = configMap.get(INCLUDED_OBJECT_TYPE);
    ///stTypes = "dm_document,dm_folder";
    String[] typeList = stTypes.split(",");

    HashSet<String> hashTypes = new HashSet<String>();
    HashSet<String> hashDctmTypes = new HashSet<String>();
    for (int x = 0; x < typeList.length; x++) {
      hashTypes.add(typeList[x]);
    }

    //javascript functions used to pass an item from a select list to another one
    buf.append(SCRIPT_START +
      "var selOptions = new Array(); " +
      "function swap(listFrom, listTo){" +
        "fromList=document.getElementsByName(listFrom)[0];" +
        "toList = document.getElementsByName(listTo)[0];" +
        "var i;" +
        "var count = 0;" +
        ///"alert(fromList.options.length);" +
        "for(i=0;i<fromList.options.length;i++){" +
          "if (fromList.options[i].selected) {" +
            "count++;" +
            ///"alert('option '+i+' selected');" +
            ///"alert('count = '+count);" +
          "}" +
          "if ((count == fromList.options.length)&&(listFrom=='CM_included_object_type_bis')){" +
            "alert('You need to select at least one object type');" +
            "return false;" +
          "}" +
          "if ((count == fromList.options.length)&&(listFrom=='CM_included_meta_bis')){" +
            "alert('You need to select at least one property');" +
            "return false;" +
          "}" +
        "}" +
        ///"alert('selectedIndex = '+fromList.selectedIndex);" +
        "while (fromList.selectedIndex != -1)" +
        "{ " +
          "addOption(toList,fromList.options[fromList.selectedIndex]); " +
          "fromList.options[fromList.selectedIndex] = null;" +
        " }" +
      " } " +
      "function addOption(list, option){" +
        " list.options[list.options.length]=new Option(option.value,option.value);" +
      " } " +
      "function selectAll(select){" +
        "for (var i = 0; i < document.getElementById(select).length; i++)" +
        "{" +
          "document.getElementById(select).options[i].selected = true;" +
        "}" +
      "}\n" +
      SCRIPT_END);

    appendSelectStart(buf, "included_object_type_toinclude");

    int nbtypes = 0;
    if (configMap.get(ADVANCEDCONF).equals("on")) {
      //loop of the Dctm types whose super_name field is not empty
      while (collecTypes.next()) {
        String type = collecTypes.getString("name");
        logger.config("type: " + type);
        super_type = collecTypes.getString("super_name");
        logger.config("super type: " + super_type);
        ///if (!hashTypes.contains(type) && (hashDctmTypes.contains(super_type) || super_type.equals("dm_sysobject") || hashTypes.contains(super_type))) {
        //exclusion of the selected types (=in the second select list and stored in the .properties file) and of the types whose super_name attribute is emty (the where clause in the query is not enough since a type can have a super_type whose super_name is empty)
        if (!hashTypes.contains(type) && !super_type.equals("")) {
          hashDctmTypes.add(type);
          nbtypes++;
          logger.config("added type: " + type);
          //Creation of the select list of the types available for selection
          appendOption(buf, type, type);
        }
      }
    }

    logger.config("nbtypes is: " + nbtypes);
    buf.append(SELECT_END);
    buf.append("<input type=\"button\" value=\"&gt;\"  onclick=\"swap('CM_included_object_type_toinclude','CM_included_object_type_bis');insertIncludeTypes();insertIncludeMetas();document.getElementById('action_update').value='addmeta';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();\"></input>");
    buf.append("<input type=\"button\" value=\"&lt;\"  onclick=\"swap('CM_included_object_type_bis','CM_included_object_type_toinclude');insertIncludeTypes();insertIncludeMetas();document.getElementById('action_update').value='addmeta';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();\"></input>");

    appendSelectStart(buf, "CM_included_object_type_bis",
        "included_object_type_bis");

    //C
    if (!hashTypes.isEmpty()) {
      for (String type : hashTypes) {
        ///logger.config("appendSelectMultiple " + name + " type vaut " + type);
        //Creation of the select list of the types previously selected
        appendOption(buf, type, type);
      }
    }

    buf.append(SELECT_END);
    appendEndRow(buf);

    appendHiddenInput(buf, "CM_included_object_type", "included_object_type",
        stTypes);

    try {
      if (collecTypes.getState() != ICollection.DF_CLOSED_STATE) {
        collecTypes.close();
        logger.fine("collection closed");
      }
    } catch (RepositoryException re1) {
      logger.severe("Error while closing " + re1);
    }
  }

  private void appendSelectMultipleIncludeTypes(StringBuilder buf, String name,
      Set<String> hash, String superData) {
    logger.fine("in appendSelectMultipleIncludeTypes");

    //javascript functions used to pass an item from a select list to another one
    buf.append(SCRIPT_START +
        "var selOptions = new Array(); " +
        "function swap(listFrom, listTo){" +
          "fromList=document.getElementsByName(listFrom)[0]; " +
          "toList = document.getElementsByName(listTo)[0]; " +
          "while (fromList.selectedIndex != -1)" +
          "{ " +
            "addOption(toList,fromList.options[fromList.selectedIndex]); " +
            "fromList.options[fromList.selectedIndex] = null;" +
          " }" +
        " } " +
        "function addOption(list, option){" +
          ///" list.options[list.options.length]=new Option(option.innerHTML,option.value);" +
        " list.options[list.options.length]=new Option(option.value,option.value);" +
        " } " +
        "function selectAll(select){" +
          "for (var i = 0; i < document.getElementById(select).length; i++)" +
          "{" +
            "document.getElementById(select).options[i].selected = true;" +
          "}" +
        "}" +
        SCRIPT_END);

    appendSelectStart(buf, "included_object_type_toinclude");
    buf.append(SELECT_END);

    buf.append("<input type=\"button\" value=\"&gt;\"  onclick=\"swap('CM_included_object_type_toinclude','CM_included_object_type_bis');\"></input>");
    buf.append("<input type=\"button\" value=\"&lt;\"  onclick=\"swap('CM_included_object_type_bis','CM_included_object_type_toinclude');\"></input>");

    //Creation of the list of the types available for selection (empty)
    appendSelectStart(buf, "CM_included_object_type_bis",
        "included_object_type_bis");

    ///Creation of the select list of the default types (listed in the connectorType.xml file)
    String stTypes = "";
    if (!hash.isEmpty()) {
      for (String type : hash) {
        ///logger.config("appendSelectMultiple " + name + " type vaut " + type);
        stTypes = stTypes.concat(type + ",");
        appendOption(buf, type, type);
      }
    }

    if (logger.isLoggable(Level.FINE)) {
      logger.fine("stTypes before substring " + stTypes);
    }
    stTypes = stTypes.substring(0, stTypes.length() - 1);
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("stTypes after substring " + stTypes);
    }

    buf.append(SELECT_END);
    appendEndRow(buf);

    appendHiddenInput(buf, "CM_included_object_type", "included_object_type",
        stTypes);
  }

  private void appendSelectMultipleIncludeMetadatas(StringBuilder buf,
      String name, Map<String, String> configMap) throws RepositoryException {
    logger.fine("in appendSelectMultipleIncludeMetadatas collection");

    StringBuilder buf2 = new StringBuilder();

    logger.config("string type: " + configMap.get(INCLUDED_OBJECT_TYPE));
    String[] typeList = configMap.get(INCLUDED_OBJECT_TYPE).split(",");

    HashMap<String, Set<String>> metasByTypes =
        new HashMap<String, Set<String>>();
    Set<String> hashTypes = new HashSet<String>();
    HashSet<String> tempTypes = new HashSet<String>();
    HashSet<String> hashMetasOfSelectedTypes = new HashSet<String>();

    for (int x = 0; x < typeList.length; x++) {
      hashTypes.add(typeList[x]);
    }

    logger.config("string meta: " + configMap.get(INCLUDED_META));
    String stMeta = configMap.get(INCLUDED_META);
    String[] metaList = stMeta.split(",");

    HashSet<String> hashMetas = new HashSet<String>();
    for (int x = 0; x < metaList.length; x++) {
      hashMetas.add(metaList[x]);
    }

    appendSelectStart(buf2, "CM_included_meta_bis", "included_meta_bis");
    appendSelectStart(buf, "included_meta_toinclude");

    IType dmsysType = sess.getType("dm_sysobject");
    HashSet<String> hashDmSysMeta = new HashSet<String>();
    for (int j = 0; j < dmsysType.getTypeAttrCount(); j++) {
      IAttr dmsysattr = dmsysType.getTypeAttr(j);
      String dmsysattrname = dmsysattr.getName();
      logger.config("dmsysattrname " + dmsysattrname + " is metadata of dm_sysobject");
      hashDmSysMeta.add(dmsysattrname);
    }

    if (configMap.get(ADVANCEDCONF).equals("on")) {
      ///loop of the selected types list
      for (int x = 0; x < typeList.length; x++) {
        String stType = typeList[x];
        IType mytype = sess.getType(stType);
        ///mytype = sess.getType(stType);
        ///mytype = (getSession(configMap)).getType(stType);
        logger.config("stType is " + stType);
        ///loop of the properties of each selected type
        for (int i = 0; i < mytype.getTypeAttrCount(); i++) {
          logger.config("compteur: " + mytype.getTypeAttrCount());
          IAttr attr = mytype.getTypeAttr(i);
          ///logger.config("attr vaut " + attr.toString());
          String data = attr.getName();
          logger.config("attr is " + data + " - attr of the type " + stType);
          if (!hashMetasOfSelectedTypes.contains(data)) {
            hashMetasOfSelectedTypes.add(data);
          }
          ///if the property is a dm_sysobject one, dm_sysobject is added to the temporary types hashset
          if (hashDmSysMeta.contains(data)) {
            tempTypes.add("dm_sysobject");
            logger.config("attr " + data + " is a dm_sysobject attribute");
            ///if the property is not already present in the list of available properties : the type is added to the temporary types hashset
          } else if (!metasByTypes.containsKey(data)) {
            tempTypes.add(stType);
            logger.config("attr " + data + " is a new attribute for the metas list");
            ///if the property is not already present in the list of available properties
          } else {
            logger.config("attr " + data + " is not a new attribute for the metas list");
            hashTypes = metasByTypes.get(data);
            ///loop of the hashset of types whom the property can belong to (among the selected types)
            for (String stCurrentType : hashTypes) {
              logger.config("the type " + stCurrentType + " is already known to have the meta " + data);
              IType currentType = sess.getType(stCurrentType);
              ///is the selected type is dm_sysobject : dm_sysobject is added to the temporary types hashset
              if (stCurrentType.equals("dm_sysobject")) {
                logger.config(stCurrentType + " is " + stCurrentType);
                tempTypes.add(stCurrentType);
                ///if the selected type is the supertype of one type whom the property can belong to : the selected type is added to the temporary types hashset
              } else if (((currentType.getSuperType()).getName()).equals(stType)) {
                logger.config(stType + " is supertype of " + stCurrentType);
                tempTypes.add(stType);
                logger.config("so supertype " + stType + " is added");
                ///if the selected type is the subtype of one type whom the property can belong to : the type whom the property can belong to is added to the temporary types hashset
              } else if (mytype.isSubTypeOf(stCurrentType)) {
                logger.config(stType + " is  subtype of " + stCurrentType);
                tempTypes.add(stCurrentType);
                logger.config(" so supertype " + stCurrentType + " is added");
                ///if the selected type is one of the types whom the property can belong to  : the type whom the property can belong to is added to the temporary types hashset
              } else if (stType.equals(stCurrentType)) {
                logger.config(stType + " is " + stCurrentType);
                tempTypes.add(stCurrentType);
                logger.config(" so type " + stCurrentType + " is added");
                ///if the selected type and one of the types whom the property can belong to don't have any hierarchical link : the type whom the property can belong to and the selected type are added to the temporary types hashset
              } else {
                logger.config("type " + stCurrentType + " is just another type with the attribute " + data);
                tempTypes.add(stType);
                tempTypes.add(stCurrentType);
                logger.config(" so type " + stType + " is added and type " + stCurrentType + " is also added");
              }
            }
          }

          logger.fine("adding tempTypes to metasByTypes hashMap");
          metasByTypes.put(data, tempTypes);
          ///temporary hashset of types reinitiated
          tempTypes = new HashSet<String>();
        }
      }

      //Creation of the select list of the available properties (properties of the selected types) with the names of the types it belongs to
      if (!metasByTypes.isEmpty()) {
        logger.fine("writing the select");
        Set<String> dataSet = metasByTypes.keySet();
        for (String data : dataSet) {
          ///logger.config("appendSelectMultiple " + name + " type vaut " + type);
          hashTypes = metasByTypes.get(data);
          if (!hashMetas.contains(data)) {
            buf.append("<option value=\"");
            buf.append(data);
            buf.append("\">");
            buf.append(data);
            buf.append(" (");
            for (String stType : hashTypes) {
              buf.append(" ");
              buf.append(stType);
              buf.append(" ");
            }
            buf.append(") </option>\n");
          }
        }
      }
    }

    //Creation of the select list of the selected properties
    if (!hashMetas.isEmpty()) {
      for (String data : hashMetas) {
        if (hashMetasOfSelectedTypes.contains(data)) {
          appendOption(buf2, data, data);
        }
      }
    }

    ///logger.config("appendSelectMultipleIncludeMetadatas " + name);
    logger.fine("before select");
    buf.append(SELECT_END);
    logger.fine("after select");

    buf.append("<input type=\"button\" value=\"&gt;\"  onclick=\"swap('CM_included_meta_toinclude','CM_included_meta_bis');insertIncludeMetas();insertIncludeTypes();document.getElementById('action_update').value='addmeta';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();\"></input>");
    buf.append("<input type=\"button\" value=\"&lt;\"  onclick=\"swap('CM_included_meta_bis','CM_included_meta_toinclude');insertIncludeMetas();insertIncludeTypes();document.getElementById('action_update').value='addmeta';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();\"></input>");

    buf2.append(SELECT_END);
    buf.append(buf2);
    appendEndRow(buf);

    appendHiddenInput(buf, "CM_included_meta", "included_meta", stMeta);
  }

  private void appendSelectMultipleIncludeMetadatas(StringBuilder buf,
      String name, Set<String> hashMeta) {
    logger.fine("in appendSelectMultipleIncludeMetadatas hashMeta");

    String stMeta = "";
    String type;
    StringBuilder buf2 = new StringBuilder();

    appendSelectStart(buf2, "CM_included_meta_bis", "included_meta_bis");

    //Creation of the select list of the available properties (empty)
    appendSelectStart(buf, "included_meta_toinclude");

    //Creation of the select list of the selected properties
    if (name.equals(INCLUDED_META)) {
      if (!hashMeta.isEmpty()) {
        for (String meta : hashMeta) {
          stMeta = stMeta.concat(meta + ",");
          appendOption(buf2, meta, meta);
        }
      }
    }

    stMeta = stMeta.substring(0, stMeta.length() - 1);

    logger.fine("before select");
    buf.append(SELECT_END);
    logger.fine("after select");

    buf.append("<input type=\"button\" value=\"&gt;\"  onclick=\"swap('CM_included_meta_toinclude','CM_included_meta_bis');\"></input>");
    buf.append("<input type=\"button\" value=\"&lt;\"  onclick=\"swap('CM_included_meta_bis','CM_included_meta_toinclude');\"></input>");

    buf2.append(SELECT_END);
    buf.append(buf2);
    appendEndRow(buf);

    appendHiddenInput(buf, "CM_included_meta", "included_meta", stMeta);
  }

  private void appendCheckBox(StringBuilder buf, String key, String label,
      String value) {
    buf.append(TR_START);
    buf.append(TD_START_COLSPAN);
    buf.append(OPEN_ELEMENT);
    buf.append(INPUT);
    buf.append(" ");
    buf.append(TYPE);
    buf.append("=\"");
    buf.append(CHECKBOX);
    buf.append("\" ");
    buf.append(NAME);
    buf.append("=\"");
    buf.append(key);
    buf.append("\" ");

    if (key.equals(ADVANCEDCONF)) {
      buf.append("id=\"ADVC\" ");
      if (value != null && value.equals("on")) {
        logger.config("advanced conf not null");
        logger.config("advanced conf set to on");
        buf.append("onclick=\"if(document.getElementById('more').style.display == 'none'){if((document.getElementById('login').value != '')&amp;&amp;(document.getElementById('Password').value != '')&amp;&amp;(document.getElementById('webtop_display_url').value != '')){document.getElementById('more').style.display='';document.getElementById('action_update').value='checkadvconf';insertIncludeMetas();insertIncludeTypes();" +
            "document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();}else{alert('" + resource.getString("advanced_config_error") + "');this.checked=false;}}else{if(confirm('" + resource.getString("confirm_uncheck_advanced") + "')){document.getElementById('more').style.display='none';document.getElementById('action_update').value='uncheckadvconf';" +
            "document.getElementById('where_clause').value='';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();insertIncludeMetas();insertIncludeTypes();}}\" ");
        buf.append(CHECKED);
        buf.append(CLOSE_ELEMENT);
        buf.append(label);
        appendEndRow(buf);

        appendHiddenInput(buf, "advanced_configuration", "true");

        /*
        appendHiddenInput(buf, "action_update", "action_update", "save");
        */

        buf.append("<tbody id=\"more\">");
      } else {
        logger.config("advanced conf set to off");
        ///insertIncludeMetas();insertIncludeTypes();
        ///buf.append("onclick=\"if(document.getElementById('more').style.display == 'none'){document.getElementById('more').style.display='';document.getElementById('action_update').value='checkadvconf';insertIncludeMetas();insertIncludeTypes();document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();}else{document.getElementById('more').style.display='none';document.getElementById('action_update').value='uncheckadvconf';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();insertIncludeMetas();insertIncludeTypes();}\">");
        buf.append("onclick=\"if(document.getElementById('more').style.display == 'none'){if((document.getElementById('login').value != '')&amp;&amp;(document.getElementById('Password').value != '')&amp;&amp;(document.getElementById('webtop_display_url').value != '')){" +
            "document.getElementById('more').style.display='';document.getElementById('action_update').value='checkadvconf';insertIncludeMetas();insertIncludeTypes();document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();}" +
            "else{alert('" + resource.getString("advanced_config_error") + "');this.checked=false;}}else{if(confirm('" + resource.getString("confirm_uncheck_advanced") + "')){document.getElementById('more').style.display='none';document.getElementById('action_update').value='uncheckadvconf';" +
            "document.getElementById('where_clause').value='';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();insertIncludeMetas();insertIncludeTypes();}}\"");
        buf.append(CLOSE_ELEMENT);
        buf.append(label);
        appendEndRow(buf);

        appendHiddenInput(buf, "advanced_configuration", "false");

        /*
        appendHiddenInput(buf, "action_update", "action_update", "save");
        */

        buf.append("<tbody id=\"more\" style=\"display: none\">");
      }
    } else {
      if (value != null && value.equals("on")) {
        buf.append(CHECKED);
      }
      buf.append(CLOSE_ELEMENT);
      buf.append(label);
      appendEndRow(buf);
    }
  }

  private void appendTextarea(StringBuilder buf, String name, String value) {
    buf.append(TEXTAREA_START);
    appendAttribute(buf, NAME, name);
    appendAttribute(buf, "rows", "10");
    appendAttribute(buf, "cols", "50");
    buf.append(">");
    buf.append(value);
    buf.append(TEXTAREA_END);
    appendEndRow(buf);
  }

  private void appendOption(StringBuilder buf, String value, String contents) {
    buf.append("<option value=\"");
    buf.append(value);
    buf.append("\">");
    buf.append(contents);
    buf.append("</option>\n");
  }

  private void appendDropDownListAttribute(StringBuilder buf, String type2,
      String value) {
    IClientX cl = null;
    try {
      cl = (IClientX) Class.forName(
          "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX")
          .newInstance();
    } catch (InstantiationException e) {
      logger.log(Level.SEVERE,
          "error while building the configuration form. The docbase will be added manually. ",
          e);
    } catch (IllegalAccessException e) {
      logger.log(Level.SEVERE,
          "error while building the configuration form. The docbase will be added manually. ",
          e);
    } catch (ClassNotFoundException e) {
      logger.log(Level.SEVERE,
          "error while building the configuration form. The docbase will be added manually. ",
          e);
    } catch (NoClassDefFoundError e) {
      logger.log(Level.SEVERE,
          "error while building the configuration form. The docbase will be added manually. ",
          e);
    }
    IClient client;

    try {
      client = cl.getLocalClient();

      IDocbaseMap mapOfDocbasesName = client.getDocbaseMap();
      if (!(mapOfDocbasesName.getDocbaseCount() > 0)) {
        appendAttribute(buf, type2, value);
      } else {
        buf.append(SELECT_START);
        appendAttribute(buf, NAME, DOCBASENAME);
        buf.append(">\n");

        for (int i = 0; i < mapOfDocbasesName.getDocbaseCount(); i++) {
          buf.append("\t<option ");
          if (value != null
              && mapOfDocbasesName.getDocbaseName(i).equals(value)) {
            buf.append(SELECTED);
          }
          buf.append("value=\"");
          buf.append(mapOfDocbasesName.getDocbaseName(i));
          buf.append("\">");
          buf.append(mapOfDocbasesName.getDocbaseName(i));
          buf.append("</option>\n");
        }
        buf.append(SELECT_END);
      }
    } catch (RepositoryException e) {
      logger.log(Level.SEVERE,
          "error while building the configuration form. The docbase will be added manually. ",
          e);
    }
  }

  private void appendStartRow(StringBuilder buf, String key) {
    buf.append(TR_START);
    buf.append(TD_START_LABEL);
    buf.append(key);
    buf.append(TD_END);
    buf.append(TD_START);
  }

  private void appendStartHiddenRow(StringBuilder buf) {
    buf.append(TR_START_HIDDEN);
    buf.append(TD_START);
    buf.append(TD_END);
    buf.append(TD_START);
  }

  private void appendEndRow(StringBuilder buf) {
    buf.append(TD_END);
    buf.append(TR_END);
  }

  private void appendHiddenInput(StringBuilder buf, String name, String value) {
    appendHiddenInput(buf, null, name, value);
  }

  private void appendHiddenInput(StringBuilder buf, String id, String name,
      String value) {
    appendStartHiddenRow(buf);
    buf.append("<input type=\"hidden\"");
    if (id != null) {
      appendAttribute(buf, ID, id);
    }
    appendAttribute(buf, "name", name);
    appendAttribute(buf, "value", value);
    buf.append(" />");
    appendEndRow(buf);
  }

  private void appendSelectStart(StringBuilder buf, String name) {
    appendSelectStart(buf, null, name);
  }

  private void appendSelectStart(StringBuilder buf, String id, String name) {
    buf.append(SELECT_START);
    appendAttribute(buf, NAME, name);
    if (id != null) {
      appendAttribute(buf, ID, id);
    }
    buf.append(" style=\"width:270px\" multiple='multiple' size=\"10\">\n");
  }

  private void appendAttribute(StringBuilder buf, String attrName,
      String attrValue) {
    buf.append(" ");
    buf.append(attrName);
    buf.append("=\"");
    buf.append(attrValue);
    buf.append("\"");
    if (attrName == TYPE && attrValue == TEXT) {
      buf.append(" size=\"50\"");
    }
  }

  public String getAuthentication_type() {
    return authentication_type;
  }

  public void setAuthentication_type(String authentication_type) {
    this.authentication_type = authentication_type;
    logger.config("authentication_type set to " + authentication_type);
  }

  public String getClientX() {
    return clientX;
  }

  public void setClientX(String clientX) {
    this.clientX = clientX;
    logger.config("clientX set to " + clientX);
  }

  /*
  public String getAdvanced_configuration() {
    logger.config("get advanced_configuration: " + advanced_configuration);
    return advanced_configuration;
  }

  public void setAdvanced_configuration(String advanced_configuration) {
    this.advanced_configuration = advanced_configuration;
    logger.config("advanced_configuration set to " + advanced_configuration);
  }

  public String getAction_update() {
    logger.config("get action_update: " + action_update);
    return action_update;
  }

  public void setAction_update(String action_update) {
    this.action_update = action_update;
    logger.config("action_update set to " + action_update);
  }
  */
}
