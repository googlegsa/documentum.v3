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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
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
  private static final Logger logger =
      Logger.getLogger(DctmConnectorType.class.getName());

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

  private static final String TD_START_LABEL =
      "<td style='white-space: nowrap'>";

  private static final String TD_START_TEXTAREA =
      "<td style='white-space: nowrap; vertical-align: top; padding-top: 2px'>";

  private static final String TD_START_PADDING =
      "<td style='white-space: nowrap; padding-top: 1ex'>\r\n";

  private static final String TD_START_COLSPAN = "<td colspan='2'>";

  private static final String TD_START_CENTER =
      "<td style='text-align: center'>";

  private static final String TR_START = "<tr>\r\n";

  private static final String TR_START_HIDDEN =
      "<tr style='display: none'>\r\n";

  private static final String SELECT_START = "<select";

  private static final String SELECT_END = "</select>\r\n";

  private static final String SELECTED = " selected='selected'";

  private static final String TEXTAREA_START = "<textarea";

  private static final String TEXTAREA_END = "</textarea>\r\n";

  private static final String SCRIPT_START =
      "<script type=\"text/javascript\"><![CDATA[\n";

  private static final String SCRIPT_END = "]]></script>\n";

  private static final String ISPUBLIC = "is_public";

  private static final String WHERECLAUSE = "where_clause";

  private static final String ADVANCEDCONF = "advanced_configuration";

  private static final String ACTIONUPDATE = "action_update";

  private static final String DOCBASENAME = "docbase";

  private static final String DISPLAYURL = "webtop_display_url";

  private static final String AVAILABLE_META = "available_meta";

  private static final String INCLUDED_META = "included_meta";

  private static final String AVAILABLE_OBJECT_TYPE = "available_object_type";

  private static final String INCLUDED_OBJECT_TYPE = "included_object_type";

  private static final String ROOT_OBJECT_TYPE = "root_object_type";

  private static final String LOGIN = "login";

  private static final String PASSWORD = "password";

  private static final String PASSWORD_KEY = "Password";

  private static final String CHECKBOX = "checkbox";

  private static final String CHECKED = " checked='checked'";

  private static final String ID = "id";

  private String clientXclassName;

  private UrlValidator urlValidator;

  private List<String> configKeys;

  private String includedObjectType;

  private String includedMeta;

  private String rootObjectType;

  public DctmConnectorType() {
  }

  public void setClientX(String className) {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("ClientX: " + className);
    }
    clientXclassName = className;
  }

  public void setUrlValidator(UrlValidator urlValidator) {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("URL validator: " + urlValidator.getClass().getName());
    }
    this.urlValidator = urlValidator;
  }

  /**
   * Set the keys that are required for configuration. One of the overloadings
   * of this method must be called exactly once before the SPI methods are
   * used.
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

  /**
   * Set the keys that are required for configuration. One of the overloadings
   * of this method must be called exactly once before the SPI methods are
   * used.
   *
   * @param configKeys an array of String keys
   */
  public void setConfigKeys(String[] configKeys) {
    logger.fine("setConfigKeys array");
    setConfigKeys(Arrays.asList(configKeys));
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

  public ConfigureResponse getConfigForm(Locale language) {
    ResourceBundle resource = getResources(language);
    try {
      IClientX cl = getClientX();
      return new ConfigureResponse("",
          makeValidatedForm(null, resource, cl, null));
    } catch (RepositoryException e) {
      // If we can't connect to DFC at all, return just an error message.
      logger.log(Level.SEVERE,
          "Error while building the configuration form", e);
      return createErrorResponse(e, resource, ErrorStyle.GET_CONFIG_FORM);
    }
  }

  public ConfigureResponse validateConfig(Map<String, String> configData,
      Locale language, ConnectorFactory connectorFactory) {
    logger.config("CONFIG DATA is " + getMaskedMap(configData));
    ResourceBundle resource = getResources(language);

    String missing = validateCoreConfig(configData);
    if (missing == null) {
      // Make sure advanced_configuration has a default value.
      if (!configData.containsKey(ADVANCEDCONF))
        configData.put(ADVANCEDCONF, "off");

      ISession sess = null;
      ISessionManager sessMag = null;
      boolean isCoreConfigValid = false;
      IClientX cl = null;
      try {
        cl = getClientX();
        sessMag = getSessionManager(cl, configData);

        ILoginInfo myinfo = sessMag.getIdentity(configData.get(DOCBASENAME));
        String user = myinfo.getUser();
        logger.config("login user: " + user);

        sess = getSession(configData, sessMag);

        logger.fine("test connection to the repository: " + sess);
        isCoreConfigValid = true;

        testWebtopUrl(configData.get(DISPLAYURL));

        // Display the form again when the advanced conf checkbox is
        // checked and a JavaScript action submitted the form.
        if (configData.get(ADVANCEDCONF).equals("on")
            && configData.get(ACTIONUPDATE).equals("redisplay")) {
          logger.config("Redisplay the configuation form");
          String form = makeValidatedForm(configData, resource, cl, sess);
          return new ConfigureResponse("", form);
        }

        if (configData.get(WHERECLAUSE) != null
            && !configData.get(WHERECLAUSE).equals("")) {
          String whereClause = checkWhereClause(configData, cl, sess);
          configData.put(WHERECLAUSE, whereClause);
          logger.config("where_clause is now " + whereClause);
        }
      } catch (RepositoryException e) {
        logger.log(Level.SEVERE,
            "RepositoryException thrown in validateConfig: ", e);

        if (!isCoreConfigValid) {
          // If there's a problem with the core configuration, we will
          // turn the advanced configuration off until the user fixes
          // the problem.
          configData.put(ADVANCEDCONF, "off");
          logger.config("ADVANCEDCONF reset to off");
        }

        if (cl == null) {
          // If we can't connect to DFC at all, return just an error message.
          return createErrorResponse(e, resource, ErrorStyle.VALIDATE_CONFIG);
        }

        // Return the config form with an error message.
        return createErrorResponse(configData, e, resource, cl, sess,
            ErrorStyle.VALIDATE_CONFIG);
      } finally {
        if (sess != null) {
          sessMag.release(sess);
          logger.fine("Release sessionConfig");
        }
      }

      // There's no need to persist action_update.
      configData.remove(ACTIONUPDATE);

      return null;
    } else {
      if (!missing.equals(DISPLAYURL)) {
        // If there's a problem with the core configuration, we will
        // turn the advanced configuration off until the user fixes
        // the problem.
        configData.put(ADVANCEDCONF, "off");
        logger.config("ADVANCEDCONF reset to off");
      }

      // Return the config form with an error message indicating the
      // name of the missing parameter.
      String form;
      try {
        IClientX cl = getClientX();
        form = makeValidatedForm(configData, resource, cl, null);
      } catch (RepositoryException e) {
        form = "";
      }
      return new ConfigureResponse(resource.getString(missing + "_error"),
          form);
    }
  }

  public ConfigureResponse getPopulatedConfigForm(
      Map<String, String> configMap, Locale language)  {
    logger.fine("getPopulatedConfigForm");
    ResourceBundle resource = getResources(language);
    ConfigureResponse result = null;

    // We use the session here to validate the username, password, and
    // docbase. We will also need it later in makeValidatedForm if the
    // advanced config is turned on.
    IClientX cl = null;
    ISessionManager sessMag = null;
    ISession sess = null;
    try {
      cl = getClientX();
      sessMag = getSessionManager(cl, configMap);
      sess = getSession(configMap, sessMag);

      for (String key : configKeys) {
        String val = configMap.get(key);
        if (val == null) {
          if (key.equals(ADVANCEDCONF)) {
            val = "off";
          } else if (key.equals(ACTIONUPDATE)) {
            val = "";
          } else if (key.equals(ROOT_OBJECT_TYPE) && val == null) {
            val = rootObjectType;
          } else if (key.equals(INCLUDED_OBJECT_TYPE)) {
            val = includedObjectType;
          } else if (key.equals(INCLUDED_META)) {
            val = includedMeta;
          } else {
            continue;
          }
          logger.config(key + " was null; set to " + val);
          configMap.put(key, val);
        }
      }

      if (logger.isLoggable(Level.CONFIG)) {
        logger.config("Before spring process: " + getMaskedMap(configMap));
      }

      result = new ConfigureResponse("",
          makeValidatedForm(configMap, resource, cl, sess));
    } catch (RepositoryException e) {
      logger.log(Level.WARNING, "Error building the configuration form", e);

      if (cl == null) {
        // If we can't connect to DFC at all, return just an error message.
        return createErrorResponse(e, resource, ErrorStyle.GET_CONFIG_FORM);
      }

      // TODO: Do we want to turn off the advanced config here, like
      // we do in validateConfig? The equivalent to !isCoreConfigValid
      // is sess == null, and if sess == null then advanced config is
      // effectively turned off, so we don't have to do anything here.

      // Return the config form with an error message.
      return createErrorResponse(configMap, e, resource, cl, sess,
          ErrorStyle.GET_CONFIG_FORM);
    } finally {
      if (sess != null) {
        sessMag.release(sess);
        logger.fine("Release sessionConfig");
      }
    }

    return result;
  }

  /**
   * Localizes the resource bundle name.
   *
   * @param language the locale to look up
   * @return the <code>ResourceBundle</code>
   * @throws MissingResourceException if the bundle can't be found
   */
  /* This method has package access for unit testing. */
  ResourceBundle getResources(Locale language)
      throws MissingResourceException {
    return ResourceBundle.getBundle("DctmConnectorResources", language);
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

  /**
   * Configuration options to workaround bug bug 1628781. Until GSA 6.2,
   * only validateConfig displayed the message from the ConfigureResponse,
   * so we need to push the message into the form when appropriate.
   */
  private enum ErrorStyle { GET_CONFIG_FORM, VALIDATE_CONFIG };

  private ConfigureResponse createErrorResponse(Map<String, String> configData,
      RepositoryException e, ResourceBundle resource, IClientX cl,
      ISession sess, ErrorStyle style) {
    String bundleMessage = getErrorMessage(e, resource);
    String form;
    try {
      form = makeValidatedForm(configData, resource, cl, sess);
    } catch (RepositoryException ignored) {
      logger.log(Level.SEVERE, "Error creating error response form", ignored);
      form = "";
    }
    return getConfigureResponse(bundleMessage, form, style);
  }

  private ConfigureResponse createErrorResponse(RepositoryException e,
      ResourceBundle resource, ErrorStyle style) {
    String bundleMessage = getErrorMessage(e, resource);
    String form = "";
    return getConfigureResponse(bundleMessage, form, style);
  }

  /**
   * Gets a {@code ConfigureResponse}. Bug 1628781 prevents the GSA
   * from the displaying the message returned by {@code getConfigForm}
   * or {@code getPopulatedConfigForm}, so we push the message into
   * the form snippet but outside of a table row, for roughly similar
   * rendering in the browser.
   */
  private ConfigureResponse getConfigureResponse(String bundleMessage,
      String form, ErrorStyle style) {
    if (style == ErrorStyle.GET_CONFIG_FORM) {
        form = "<font color='red'>" + bundleMessage + "</font><br />\n"
            + form;
        bundleMessage = "";
    }
    return new ConfigureResponse(bundleMessage, form);
  }

  private String getErrorMessage(RepositoryException e,
      ResourceBundle resource) {
    String message = e.getMessage();
    String extractErrorMessage;
    if (message.indexOf("[") != -1) {
      extractErrorMessage = message.substring(message.indexOf("[") + 1,
          message.indexOf("]"));
    } else {
      // XXX: How safe is assuming that we have cause here?
      extractErrorMessage = e.getCause().getClass().getName();
    }
    String bundleMessage;
    try {
      bundleMessage = resource.getString(extractErrorMessage);
    } catch (MissingResourceException mre) {
      bundleMessage = resource.getString("DEFAULT_ERROR_MESSAGE") + " "
          + e.getMessage();
    }
    logger.warning(bundleMessage);
    return bundleMessage;
  }

  private String checkWhereClause(Map<String, String> configData,
      IClientX cl, ISession sess) throws RepositoryException {
    String docbase = configData.get(DOCBASENAME);
    String whereClause = DqlUtils.stripLeadingAnd(configData.get(WHERECLAUSE));
    String rootType = configData.get(ROOT_OBJECT_TYPE);
    String includedTypes = configData.get(INCLUDED_OBJECT_TYPE);

    StringBuilder dql = new StringBuilder();
    dql.append("select r_object_id from ");
    dql.append(rootType);
    dql.append(" where ");
    DqlUtils.appendObjectTypes(dql,
        new HashSet<String>(Arrays.asList(includedTypes.split(","))));
    dql.append(" and (");
    dql.append(whereClause);
    dql.append(") ENABLE (return_top 1)");

    IQuery query = cl.getQuery();
    query.setDQL(dql.toString());
    ICollection collec = query.execute(sess, IQuery.EXECUTE_READ_QUERY);
    try {
      if (!collec.next()) {
        throw new RepositoryException("[additionalTooRestrictive]");
      }
    } finally {
      if (collec.getState() != ICollection.DF_CLOSED_STATE) {
        collec.close();
      }
    }

    return whereClause;
  }

  private void testWebtopUrl(String url) throws RepositoryException {
    if (url == null || url.length() == 0) {
      throw new RepositoryException("[webtop_display_url_error]");
    }
    logger.config("test connection to the webtop server: " + url);
    try {
      urlValidator.validate(url);
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

  /**
   * Checks whether the given key is a core field needed to open the
   * advanced configuration.
   *
   * @param key the property name
   */
  private boolean isCoreConfig(String key) {
    return key.equals(LOGIN) || key.equals(PASSWORD_KEY)
        || key.equals(DOCBASENAME);
  }

  /**
   * Checks whether the given key is a required field.
   *
   * @param key the property name
   */
  private boolean isRequired(String key) {
    return key.equals(LOGIN) || key.equals(PASSWORD_KEY)
        || key.equals(DOCBASENAME) || key.equals(DISPLAYURL);
  }

  private String validateCoreConfig(Map<String, String> configData) {
    for (String key : configKeys) {
      String val = configData.get(key);
      if ((val == null || val.length() == 0) && isCoreConfig(key)) {
        logger.warning("Missing key: " + key);
        return key;
      }
    }
    return null;
  }

  private String makeValidatedForm(Map<String, String> configMap,
      ResourceBundle resource, IClientX cl, ISession sess)
      throws RepositoryException {
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

    for (String key : configKeys) {
      ///logger.config("key is " + key);
      String value = "";
      if (configMap != null) {
        value = configMap.get(key);
        ///logger.config("key " + key + " is " + value);
      }

      if (key.equals(ISPUBLIC)) {
        if (value != null && value.equals("on")) {
          appendCheckBox(buf, key, resource.getString(key), value, resource);
        }
      } else if (key.equals(DOCBASENAME)) {
        logger.fine("docbase droplist");
        appendStartRow(buf, resource.getString(key), isRequired(key));
        appendDropDownListAttribute(buf, value, resource, cl);
        appendEndRow(buf);
      } else if (key.equals(INCLUDED_OBJECT_TYPE)) {
        if (sess != null && advConf.equals("on")) {
          appendStartTable(buf);
          appendStartTableRow(buf, resource.getString(AVAILABLE_OBJECT_TYPE),
              resource.getString(key));

          SortedSet<String> allTypes = getListOfTypes(rootType, cl, sess);

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
        if (sess != null && advConf.equals("on")) {
          appendStartTableRow(buf, resource.getString(AVAILABLE_META),
              resource.getString(key));

          // Properties are displayed according to the values stored
          // in the .properties file.
          appendSelectMultipleIncludeMetadatas(buf, configMap, sess);

          appendEndTable(buf);
        } else {
          // Properties are displayed according to the default
          // values stored in the connectorType.xml file.
          appendSelectMultipleIncludeMetadatas(buf, configMap);
        }
        buf.append("</tbody>");
      } else if (key.equals(ADVANCEDCONF)) {
        logger.fine("advanced config: " + advConf);
        appendCheckBox(buf, ADVANCEDCONF,
            "<b>" + resource.getString(key) + "</b>", value, resource);
      } else if (key.equals(ROOT_OBJECT_TYPE)) {
        logger.fine("makeValidatedForm - rootObjectType");
        if (sess != null && advConf.equals("on")) {
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
          SortedSet<String> documentTypes = getListOfTypes("dm_document",
              cl, sess);
          documentTypes.remove("dm_document");
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
      } else if (key.equals(ACTIONUPDATE)) {
        appendHiddenInput(buf, key, key, "");
      } else {
        logger.fine("makeValidatedForm - input - " + key);
        appendStartRow(buf, resource.getString(key), isRequired(key));
        buf.append(OPEN_ELEMENT);
        buf.append(INPUT);
        if (key.equals(PASSWORD_KEY)) {
          appendAttribute(buf, TYPE, PASSWORD);
        } else {
          appendAttribute(buf, TYPE, TEXT);
        }
        appendAttribute(buf, VALUE, value);
        appendAttribute(buf, NAME, key);
        appendAttribute(buf, ID, key);
        buf.append(CLOSE_ELEMENT);
        appendEndRow(buf);
      }
    }

    return buf.toString();
  }

  private IClientX getClientX() throws RepositoryException {
    IClientX cl;
    try {
      cl = (IClientX) Class.forName(clientXclassName).newInstance();
    } catch (InstantiationException e) {
      Throwable cause = e.getCause();
      if (cause instanceof RepositoryException)
        throw (RepositoryException) cause;
      else
        throw new RepositoryException(e);
    } catch (Throwable e) {
      throw new RepositoryException(e);
    }
    return cl;
  }

  private ISessionManager getSessionManager(IClientX cl,
      Map<String, String> logMap) throws RepositoryException {
    IClient client = cl.getLocalClient();
    ISessionManager sessMag = client.newSessionManager();
    sessMag.clearIdentity(logMap.get(DOCBASENAME));
    logger.config("after clearIdentity for docbase: "
        + logMap.get(DOCBASENAME));
    ILoginInfo loginInfo = cl.getLoginInfo();
    loginInfo.setUser(logMap.get(LOGIN));
    logger.config("after setIdentity for login: " + logMap.get(LOGIN));
    loginInfo.setPassword(logMap.get(PASSWORD_KEY));
    sessMag.setIdentity(logMap.get(DOCBASENAME), loginInfo);
    logger.config("after setIdentity for docbase: " + logMap.get(DOCBASENAME));
    return sessMag;
  }

  private ISession getSession(Map<String, String> logMap,
      ISessionManager sessMag) throws RepositoryException {
    logger.config("login is " + logMap.get(LOGIN));
    logger.config("docbase is " + logMap.get(DOCBASENAME));

    return sessMag.getSession(logMap.get(DOCBASENAME));
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
      Map<String, String> configMap, ISession sess)
      throws RepositoryException {
    logger.fine("in appendSelectMultipleIncludeMetadatas properties");

    logger.config("string type: " + configMap.get(INCLUDED_OBJECT_TYPE));
    String[] typeList = configMap.get(INCLUDED_OBJECT_TYPE).split(",");

    Map<String, Set<String>> metasByTypes =
        new TreeMap<String, Set<String>>();
    HashSet<String> existingProperties = new HashSet<String>();

    logger.config("string meta: " + configMap.get(INCLUDED_META));
    String stMeta = configMap.get(INCLUDED_META);
    String[] metaList = stMeta.split(",");

    Set<String> includedProperties = new TreeSet<String>();
    for (String property : metaList) {
      includedProperties.add(property.trim());
    }

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
    stMeta = buffer.substring(0, buffer.length() - 1);
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

  private void appendCheckBox(StringBuilder buf, String key, String label,
      String value, ResourceBundle resource) {
    boolean isAdvanced = key.equals(ADVANCEDCONF);
    boolean isOn = value != null && value.equals("on");

    buf.append(TR_START);
    if (isAdvanced) {
      buf.append("<td colspan='2' style='padding-top: 3ex'>");
    } else {
      buf.append(TD_START_COLSPAN);
    }
    buf.append(OPEN_ELEMENT);
    buf.append(INPUT);
    appendAttribute(buf, TYPE, CHECKBOX);
    appendAttribute(buf, VALUE, "on"); // Also the browsers default value.
    appendAttribute(buf, NAME, key);
    appendAttribute(buf, ID, key);

    if (isAdvanced) {
      logger.config("advanced conf set to " + value);
      buf.append(" onclick=\"");
      buf.append("if(document.getElementById('more').style.display == 'none'){");
      buf.append("if((document.getElementById('login').value != '')")
          .append("&amp;&amp;(document.getElementById('Password').value != '')){");
      if (isOn) {
        buf.append("document.getElementById('more').style.display='';");
      } else {
        buf.append("document.getElementById('action_update').value='redisplay';");
        buf.append("document.body.style.cursor='wait';");
        buf.append("document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();");
      }
      buf.append("}else{");
      buf.append("alert('")
          .append(resource.getString("advanced_configuration_error"))
          .append("');this.checked=false;");
      buf.append("}");
      buf.append("}else{");
      buf.append("document.getElementById('more').style.display='none';");
      buf.append("}\"");
    }

    if (isOn) {
      buf.append(CHECKED);
    }
    buf.append(CLOSE_ELEMENT);

    buf.append("<label");
    appendAttribute(buf, "for", key);
    buf.append(">");
    buf.append(label);
    buf.append("</label>");

    appendEndRow(buf);

    if (isAdvanced) {
      if (isOn) {
        buf.append("<tbody id=\"more\">");
      } else {
        buf.append("<tbody id=\"more\" style=\"display: none\">");
      }
    }
  }

  private void appendTextarea(StringBuilder buf, String name, String value) {
    buf.append(TEXTAREA_START);
    appendAttribute(buf, NAME, name);
    appendAttribute(buf, "rows", "10");
    appendAttribute(buf, "cols", "50");
    buf.append(">");
    escapeAndAppend(buf, value);
    buf.append(TEXTAREA_END);
    appendEndRow(buf);
  }

  private void appendOption(StringBuilder buf, String value, String text) {
    appendOption(buf, value, text, false);
  }

  private void appendOption(StringBuilder buf, String value, String text,
      boolean isSelected) {
    buf.append("<option");
    appendAttribute(buf, "value", value);
    if (isSelected) {
      buf.append(SELECTED);
    }
    buf.append(">");
    buf.append(text);
    buf.append("</option>\n");
  }

  /* This method has package access for unit testing. */
  void appendDropDownListAttribute(StringBuilder buf, String value,
      ResourceBundle resource, IClientX cl) throws RepositoryException {
    IClient client = cl.getLocalClient();
    IDocbaseMap docbaseMap = client.getDocbaseMap();
    int count = docbaseMap.getDocbaseCount();

    ArrayList<String> docbases = new ArrayList<String>(count);
    for (int i = 0; i < count; i++) {
      docbases.add(docbaseMap.getDocbaseName(i));
    }

    buf.append(SELECT_START);
    appendAttribute(buf, NAME, DOCBASENAME);
    buf.append(">\n");

    // If there are zero or more one docbase available and none
    // selected, then add an empty to the top of the list as the
    // selected option so that users must make an explicit choice.
    // Otherwise, if there is a configured docbase but it does not
    // appear in the list, add the configured docbase along with a
    // message to the top of the list as the selected option.
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

  private void appendStartRow(StringBuilder buf, String key,
      boolean isRequired) {
    buf.append(TR_START);
    buf.append(TD_START_LABEL);
    buf.append(key);
    if (isRequired) {
      buf.append("<span style='color: red; font-weight: bold; ")
          .append("margin: auto 0.3em;\'>*</span>");
    }
    buf.append(TD_END);
    buf.append(TD_START);
  }

  private void appendStartTextareaRow(StringBuilder buf, String key) {
    buf.append(TR_START);
    buf.append(TD_START_TEXTAREA);
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

  private void appendStartTable(StringBuilder buf) {
    buf.append(TR_START);
    buf.append(TD_START_COLSPAN);
    buf.append("<table style=\"width: 100%; border-collapse: collapse\">");
  }

  private void appendStartTableRow(StringBuilder buf, String leftKey,
      String rightKey) {
    buf.append(TR_START);
    buf.append(TD_START_PADDING);
    buf.append(leftKey);
    buf.append(TD_END);
    buf.append(TD_START);
    buf.append("&nbsp;");
    buf.append(TD_END);
    buf.append(TD_START_PADDING);
    buf.append(rightKey);
    buf.append(TD_END);
    buf.append(TR_END);
    buf.append(TR_START);
    buf.append(TD_START);
  }

  private void appendEndTable(StringBuilder buf) {
    buf.append("</table>");
    appendEndRow(buf);
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
    buf.append(" style=\"width:100%\" multiple='multiple' size=\"10\">\n");
  }

  private void appendAttribute(StringBuilder buf, String attrName,
      String attrValue) {
    buf.append(" ");
    buf.append(attrName);
    buf.append("=\"");
    escapeAndAppend(buf, attrValue);
    buf.append("\"");
    if (attrName == TYPE && (attrValue == TEXT || attrValue == PASSWORD)) {
      buf.append(" size=\"50\"");
    }
  }

  /* Copied from com.google.enterprise.connector.otex.LivelinkConnectorType. */
  private void escapeAndAppend(StringBuilder buffer, String data) {
    for (int i = 0; i < data.length(); i++) {
      switch (data.charAt(i)) {
        case '\'': buffer.append("&apos;"); break;
        case '"': buffer.append("&quot;"); break;
        case '&': buffer.append("&amp;"); break;
        case '<': buffer.append("&lt;"); break;
        case '>': buffer.append("&gt;"); break;
        default: buffer.append(data.charAt(i));
      }
    }
  }

  private void appendStartOnclickButton(StringBuilder buf, String label) {
    buf.append("<input");
    appendAttribute(buf, "type", "button");
    appendAttribute(buf, "value", label);
    buf.append(" onclick=\"");
  }

  private void appendEndOnclickButton(StringBuilder buf) {
    // Putting whitepace between the close input tag and the br tag
    // causes a misalignment in Chrome. The extra break doesn't seem
    // to cause vertical alignment issues in Chrome, Safari, Firefox,
    // or IE.
    buf.append("\"></input><br />\n");
  }
}
