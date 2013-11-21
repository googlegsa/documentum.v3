// Copyright 2009 Google Inc.
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

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClient;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClientX;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmQuery;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IDocbaseMap;
import com.google.enterprise.connector.spi.ConfigureResponse;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.ConnectorFactory;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SimpleConnectorFactory;
import com.google.enterprise.connector.util.UrlValidator;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class DctmMockConnectorTypeTest extends TestCase {
  private static final String CLIENT_X_CLASS_NAME =
      "com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClientX";
  private static final UrlValidator URL_VALIDATOR = new MockUrlValidator();
  private static final String[] CONFIG_KEYS = {
    "login", "Password", "docbase", "webtop_display_url", "is_public",
    "advanced_configuration", "action_update", "root_object_type",
    "where_clause", "included_object_type", "included_meta", };
  private static final String ROOT_OBJECT_TYPE = "dm_sysobject";
  private static final String INCLUDED_OBJECT_TYPE = "dm_document";
  private static final String INCLUDED_META = "object_name,r_object_type,"
      + "title,subject,keywords,authors,r_creation_date,r_modify_date,"
      + "r_content_size,a_content_type";

  /** The {@code ConnectorType} instance under test. */
  private DctmConnectorType type;

  /** The Locale-specific resources. */
  private ResourceBundle resources;

  /**
   * The default values for the configuration properties, as returned
   * by getConfigForm.
   */
  private Map<String, String> emptyMap;

  /**
   * Valid values for the configuration properties.
   */
  private Map<String, String> validMap;

  @Override
  protected void setUp() {
    // TODO: We could Spring instantiate the DctmConnectorType instead.
    type = new DctmConnectorType();
    type.setClientX(CLIENT_X_CLASS_NAME);
    type.setUrlValidator(URL_VALIDATOR);
    type.setConfigKeys(CONFIG_KEYS);
    type.setRoot_object_type(ROOT_OBJECT_TYPE);
    type.setIncluded_object_type(INCLUDED_OBJECT_TYPE);
    type.setIncluded_meta(INCLUDED_META);

    resources = type.getResources(Locale.US);

    emptyMap = new HashMap<String, String>();
    emptyMap.put("Password", "");
    emptyMap.put("root_object_type", "dm_sysobject");
    emptyMap.put("where_clause", "");
    emptyMap.put("included_meta", "object_name,r_object_type,title,subject,"
        + "keywords,authors,r_creation_date,r_modify_date,r_content_size,"
        + "a_content_type");
    emptyMap.put("login", "");
    emptyMap.put("action_update", "");
    emptyMap.put("included_object_type", "dm_document");
    emptyMap.put("webtop_display_url", "");
    emptyMap.put("docbase", "");

    validMap = new HashMap<String, String>(emptyMap);
    validMap.put("login", DmInitialize.DM_LOGIN_OK1);
    validMap.put("Password", DmInitialize.DM_PWD_OK1);
    validMap.put("docbase", DmInitialize.DM_DOCBASE);
    validMap.put("webtop_display_url", "http://www.google.com/");
  }

  private void assertContains(String source, String target) {
    assertTrue(getFragment(source), source.indexOf(target) != -1);
  }

  private void assertNotContains(String source, String target) {
    assertFalse(getFragment(source), source.indexOf(target) != -1);
  }

  private String getFragment(String source) {
    return (source.length() > 20) ? source.substring(0, 20) + "..." : source;
  }

  /**
   * Asserts that the given response is not an error. There should be an
   * empty message, no red text in the form, and table cells in the form.
   */
  private void assertIsNotError(ConfigureResponse response) {
    assertNotNull(response);
    assertEquals("", response.getMessage());
    String form = response.getFormSnippet();
    assertNotNull(form);
    assertContains(form, "<td>");
    assertNotContains(form, "color='red'");
  }

  /**
   * Asserts that the given response is an error with no form snippet.
   * Until GSA 6.2 is required, the returned message is empty, and the
   * error message is in the form snippet. Once GSA 6.2 is required,
   * this method will have to be changed to simply call
   * {@code assertIsError}.
   */
  private void assertIsFormError(ConfigureResponse response, String message,
      String restOfForm) {
    assertNotNull(response);
    assertEquals("", response.getMessage());
    String form = response.getFormSnippet();
    assertNotNull(form);
    assertContains(form, "color='red'");
    assertContains(form, message);
    assertEquals(restOfForm, form.replaceFirst(".*\n", ""));
  }

  /**
   * Asserts that the given response is an error with a message and a
   * form snippet.
   */
  private void assertIsError(ConfigureResponse response, String message,
      String form) {
    assertNotNull(response);
    assertContains(response.getMessage(), message);
    assertEquals(form, response.getFormSnippet());
  }

  public void testGcf() {
    ConfigureResponse response = type.getConfigForm(Locale.US);
    assertIsNotError(response);
  }

  public void testGcfClassNotFound() {
    type.setClientX("invalid.Class");

    ConfigureResponse response = type.getConfigForm(Locale.US);
    assertIsFormError(response, "invalid.Class", "");
  }

  /**
   * Base class for a ClientX implementation that throws exceptions
   * for testing the error handling.
   */
  private static abstract class MockDmClientXThrows extends MockDmClientX {
    protected abstract String getMessage();
  }

  static class GetLocalClientThrows extends MockDmClientXThrows {
    @Override
    protected String getMessage() {
      return "mimicking getLocalClient failure";
    }

    @Override
    public IClient getLocalClient() throws RepositoryException {
      throw new RepositoryException(
          new RuntimeException(getMessage()));
    }
  }

  static class GetDocbaseMapThrows extends MockDmClientXThrows {
    @Override
    protected String getMessage() {
      return "mimicking getDocbaseMap failure";
    }

    @Override
    public IClient getLocalClient() {
      return new MockDmClient() {
        @Override
        public IDocbaseMap getDocbaseMap() throws RepositoryException {
          throw new RepositoryException(
              new RuntimeException(getMessage()));
        }
      };
    }
  }

  private void testGcfThrows(MockDmClientXThrows client) {
    type.setClientX(client.getClass().getName());

    ConfigureResponse response = type.getConfigForm(Locale.US);
    assertIsFormError(response, client.getMessage(), "");
  }

  public void testGcfGetLocalClientThrows() {
    testGcfThrows(new GetLocalClientThrows());
  }

  public void testGcfGetDocbaseMapThrows() {
    testGcfThrows(new GetDocbaseMapThrows());
  }

  public void testVc() {
    ConfigureResponse response = type.validateConfig(validMap, Locale.US,
        new SimpleConnectorFactory(new DctmConnector()));
    assertNull(response);
  }

  public void testVcMissingUsername() {
    ConfigureResponse response =
        type.validateConfig(emptyMap, Locale.US, null);
    assertEquals(resources.getString("login_error"), response.getMessage());
    assertEquals(type.getConfigForm(Locale.US).getFormSnippet(),
        response.getFormSnippet());
  }

  public void testVcMissingDocbase() {
    // Without a docbase, getPopulatedConfigForm, which is our
    // expected baseline here, returns an error, and right now it
    // returns an error in the form. This will have to be changed when
    // we require GSA 6.2 and the error message is returned separately.
    // N.B., validMap is now invalid
    validMap.put("docbase", "");
    String originalForm =
        type.getPopulatedConfigForm(validMap, Locale.US).getFormSnippet();
    String modifiedForm = originalForm.replaceFirst(".*\n", "");

    ConfigureResponse response =
        type.validateConfig(validMap, Locale.US, null);
    assertEquals(resources.getString("docbase_error"), response.getMessage());
    assertEquals(modifiedForm, response.getFormSnippet());
  }

  public void testVcInvalidUrl() {
    // N.B., validMap is now invalid.
    validMap.put("webtop_display_url", "invalid URL");

    ConfigureResponse response =
        type.validateConfig(validMap, Locale.US, null);
    assertEquals(resources.getString("HttpException"), response.getMessage());
    assertEquals(type.getPopulatedConfigForm(validMap,
            Locale.US).getFormSnippet(), response.getFormSnippet());
  }

  public void testVcClassNotFound() {
    type.setClientX("invalid.Class");

    ConfigureResponse response =
        type.validateConfig(validMap, Locale.US, null);
    assertIsError(response, "invalid.Class", "");
  }

  private void testVcThrows(MockDmClientXThrows client,
      Map<String, String> configMap) {
    testVcThrows(client, configMap, client.getMessage());
  }

  private void testVcThrows(MockDmClientXThrows client,
      Map<String, String> configMap, String message) {
    type.setClientX(client.getClass().getName());

    ConfigureResponse response =
        type.validateConfig(configMap, Locale.US, null);
    assertIsError(response, message, "");
  }

  public void testVcGetLocalClientThrows() {
    testVcThrows(new GetLocalClientThrows(), validMap);
  }

  public void testVcMissingGetLocalClientThrows() {
    testVcThrows(new GetLocalClientThrows(), emptyMap,
        resources.getString("login_error"));
  }

  /*
   * Even though we may implement everything in ConnectorFactory here,
   * by extending SimpleConnectorFactory we are safe against future
   * extensions of the interface.
   */
  private static class WhereClauseFactory extends SimpleConnectorFactory {
    private final boolean ignoreMap;
    private final String[] xmlWhere;

    WhereClauseFactory(boolean ignoreMap, String... whereClause) {
      this.ignoreMap = ignoreMap;
      this.xmlWhere = whereClause;
    }

    @Override
    public Connector makeConnector(Map<String, String> configMap) {
      List<String> whereClause = new ArrayList<String>();
      String mapWhere = configMap.get("where_clause");
      if (mapWhere != null && mapWhere.length() > 0 && !ignoreMap)
        whereClause.add(mapWhere);
      Collections.addAll(whereClause, xmlWhere);
      DctmConnector connector = new DctmConnector();
      connector.setWhere_clause(whereClause);
      return connector;
    }
  }

  /** Tests a basic where clause that returns results. */
  public void testVcWhereClause_results() {
    validMap.put("where_clause", MockDmQuery.TRUE_WHERE_CLAUSE);
    ConnectorFactory connectorFactory = new WhereClauseFactory(false);

    ConfigureResponse response = type.validateConfig(validMap, Locale.US,
        connectorFactory);
    if (response != null)
      fail(response.getMessage());
  }

  /** Tests a where clause that returns no results. */
  public void testVcWhereClause_noresults() {
    validMap.put("where_clause", MockDmQuery.FALSE_WHERE_CLAUSE);
    ConnectorFactory connectorFactory = new WhereClauseFactory(false);
    String expected = resources.getString("additionalTooRestrictive");

    ConfigureResponse response = type.validateConfig(validMap, Locale.US,
        connectorFactory);
    assertNotNull(response);
    assertEquals(expected, response.getMessage());
  }

  /** Tests that multiple entries are valid. */
  public void testVcWhereClause_list() {
    validMap.put("where_clause", MockDmQuery.TRUE_WHERE_CLAUSE);
    ConnectorFactory connectorFactory =
        new WhereClauseFactory(false, MockDmQuery.TRUE_WHERE_CLAUSE);

    ConfigureResponse response = type.validateConfig(validMap, Locale.US,
        connectorFactory);
    if (response != null)
      fail(response.getMessage());
  }

  /** Tests that an XML entry that returns no results leads to an error. */
  public void testVcWhereClause_listnoresults() {
    validMap.put("where_clause", MockDmQuery.TRUE_WHERE_CLAUSE);
    ConnectorFactory connectorFactory =
        new WhereClauseFactory(false, MockDmQuery.FALSE_WHERE_CLAUSE);
    String expected = resources.getString("additionalTooRestrictive");

    ConfigureResponse response = type.validateConfig(validMap, Locale.US,
        connectorFactory);
    assertNotNull(response);
    assertEquals(expected, response.getMessage());
  }

  /** Tests that ignoring a non-empty map entry leads to an error. */
  public void testVcWhereClause_ignoremap() {
    validMap.put("where_clause", MockDmQuery.TRUE_WHERE_CLAUSE);
    ConnectorFactory connectorFactory =
        new WhereClauseFactory(true, MockDmQuery.ALT_TRUE_WHERE_CLAUSE);
    String expected = resources.getString("whereClauseNotUsed");

    ConfigureResponse response = type.validateConfig(validMap, Locale.US,
        connectorFactory);
    assertNotNull(response);
    assertEquals(expected, response.getMessage());
  }

  /**
   * Tests that an ignored map entry and an XML entry that returns no
   * results leads to an error. It's not important which error.
   */
  public void testVcWhereClause_ignoremapnoresults() {
    validMap.put("where_clause", MockDmQuery.TRUE_WHERE_CLAUSE);
    ConnectorFactory connectorFactory =
        new WhereClauseFactory(true, MockDmQuery.FALSE_WHERE_CLAUSE);
    List<String> expected = new ArrayList<String>();
    Collections.addAll(expected, resources.getString("whereClauseNotUsed"),
        resources.getString("additionalTooRestrictive"));

    ConfigureResponse response = type.validateConfig(validMap, Locale.US,
        connectorFactory);
    assertNotNull(response);
    assertTrue(response.getMessage(), expected.contains(response.getMessage()));
  }

  /** Tests that an empty map entry can be safely ignored. */
  public void testVcWhereClause_ignoremapempty() {
    ConnectorFactory connectorFactory =
        new WhereClauseFactory(true, MockDmQuery.TRUE_WHERE_CLAUSE);

    ConfigureResponse response = type.validateConfig(validMap, Locale.US,
        connectorFactory);
    if (response != null)
      fail(response.getMessage());
  }

  /**
   * Tests that an empty map entry with an XML entry that returns no
   * results leads to an error.
  */
  public void testVcWhereClause_emptynoresults() {
    ConnectorFactory connectorFactory =
        new WhereClauseFactory(true, MockDmQuery.FALSE_WHERE_CLAUSE);
    String expected = resources.getString("additionalTooRestrictive");

    ConfigureResponse response = type.validateConfig(validMap, Locale.US,
        connectorFactory);
    assertNotNull(response);
    assertEquals(expected, response.getMessage());
  }

  public void testGpcf() {
    // We need to select the docbase in order to get a mock session.
    // We don't strictly need a session, but DctmConnectorType is
    // requiring one.
    // N.B., emptyMap is no longer empty.
    emptyMap.put("docbase", DmInitialize.DM_DOCBASE);

    ConfigureResponse response =
        type.getPopulatedConfigForm(emptyMap, Locale.US);
    assertEquals("", response.getMessage());
    assertEquals(type.getConfigForm(Locale.US).getFormSnippet(),
        response.getFormSnippet());
  }

  public void testGpcfClassNotFound() {
    type.setClientX("invalid.Class");

    ConfigureResponse response =
        type.getPopulatedConfigForm(validMap, Locale.US);
    assertIsFormError(response, "invalid.Class", "");
  }

  private void testGpcfThrows(MockDmClientXThrows client,
      Map<String, String> configMap) {
    type.setClientX(client.getClass().getName());

    ConfigureResponse response =
        type.getPopulatedConfigForm(configMap, Locale.US);
    assertIsFormError(response, client.getMessage(), "");
  }

  public void testGpcfGetLocalClientThrows() {
    testGpcfThrows(new GetLocalClientThrows(), validMap);
  }

  public void testGpcfGetDocbaseMapThrows() {
    testGpcfThrows(new GetDocbaseMapThrows(), validMap);
  }

  public void testGpcfGetSessionThrows() {
    ConfigureResponse response =
        type.getPopulatedConfigForm(emptyMap, Locale.US);
    assertIsFormError(response, resources.getString("DEFAULT_ERROR_MESSAGE"),
        type.getConfigForm(Locale.US).getFormSnippet());
  }

  /**
   * Tests the {@code appendDropDownListAttribute} method using
   * {@code getPopulatedConfigForm}. Compared to the normal baseline,
   * if we instead use an invalid current docbase, then we get an
   * error back (from the attempt to get a session) and a form that
   * includes the invalid docbase, selected, along with the valid
   * docbase.
   */
  public void testGpcfInvalidDocbase() {
    // If the current docbase is not returned in the list, then it is
    // added and selected. We need to modify the default form
    // accordingly. First, we get the error-free version of the
    // populated form to modify and compare to.
    String originalForm = type.getPopulatedConfigForm(validMap,
        Locale.US).getFormSnippet();
    String modifiedForm = originalForm.replaceFirst(DmInitialize.DM_DOCBASE
        + "\" selected='selected'",
        "other\" selected='selected'>other ("
        + resources.getString("docbase_error")
        + ")</option>\n<option value=\"" + DmInitialize.DM_DOCBASE + '"');

    // N.B., validMap is now invalid
    validMap.put("docbase", "other");

    ConfigureResponse response =
        type.getPopulatedConfigForm(validMap, Locale.US);
    assertIsFormError(response, resources.getString("DEFAULT_ERROR_MESSAGE"),
        modifiedForm);
  }
}
