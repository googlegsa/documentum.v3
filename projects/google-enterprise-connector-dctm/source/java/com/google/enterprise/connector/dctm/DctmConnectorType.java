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
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.enterprise.connector.dctm.FormSnippet.*;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.ConfigureResponse;
import com.google.enterprise.connector.spi.ConnectorFactory;
import com.google.enterprise.connector.spi.ConnectorType;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.util.UrlValidator;
import com.google.enterprise.connector.util.UrlValidatorException;

public class DctmConnectorType implements ConnectorType {
  private static final Logger logger =
      Logger.getLogger(DctmConnectorType.class.getName());

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
      Map<String, String> configMap, Locale language) {
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
    FormSnippetBuilder builder = new FormSnippetBuilder();
    builder.setConfigKeys(configKeys);
    builder.setRoot_object_type(rootObjectType);
    builder.setIncluded_object_type(includedObjectType);
    builder.setIncluded_meta(includedMeta);
    FormSnippet snippet = builder.build(configMap, resource, cl, sess);
    return snippet.render();
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
}
