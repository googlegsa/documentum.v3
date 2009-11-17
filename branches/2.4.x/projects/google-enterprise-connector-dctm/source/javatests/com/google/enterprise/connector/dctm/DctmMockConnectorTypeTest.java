// Copyright (C) 2009 Google Inc.
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
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmDocbaseMap;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IDocbaseMap;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.spi.ConfigureResponse;
import com.google.enterprise.connector.spi.ConnectorType;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DctmMockConnectorTypeTest extends TestCase {
  private static final String CLIENT_X_CLASS_NAME =
      "com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClient";
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
    validMap.put("login", "dummy");
    validMap.put("Password", "dummy");
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
  private static abstract class MockDmClientThrows extends MockDmClient {
    protected abstract String getMessage();
  }

  static class GetLocalClientThrows extends MockDmClientThrows {
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

  static class GetDocbaseMapThrows extends MockDmClientThrows {
    @Override
    protected String getMessage() {
      return "mimicking getDocbaseMap failure";
    }

    @Override
    public IDocbaseMap getDocbaseMap() throws RepositoryException {
      throw new RepositoryException(
          new RuntimeException(getMessage()));
    }
  }

  private void testGcfThrows(MockDmClientThrows client) {
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
    ConfigureResponse response =
        type.validateConfig(validMap, Locale.US, null);
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

  private void testVcThrows(MockDmClientThrows client,
      Map<String, String> configMap) {
    testVcThrows(client, configMap, client.getMessage());
  }

  private void testVcThrows(MockDmClientThrows client,
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

  private void testGpcfThrows(MockDmClientThrows client,
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

  static class MultipleDocbases extends MockDmClient {
    final int count;

    MultipleDocbases(int count) {
      this.count = count;
    }

    @Override
    public IDocbaseMap getDocbaseMap() throws RepositoryException {
      return new MockDmDocbaseMap(count);
    }
  }

  /**
   * Tests the {@code appendDropDownListAttribute} method.
   *
   * @param count the number of docbases to return
   * @param value the current docbase value
   * @param options the expected number of options in the select list
   * @param selected the expected value of the selected option
   */
  private void testAddla(int count, String value, int options,
      String selected) {
    StringBuilder buf = new StringBuilder();
    MockDmClient clientX = new MultipleDocbases(count);
    try {
      type.appendDropDownListAttribute(buf, value, resources, clientX);
    } catch (RepositoryException e) {
      fail(e.toString());
    }
    String selectList = buf.toString();
    assertEquals(options, options(selectList));
    assertEquals(selected, selected(selectList));
  }

  /**
   * Tests the {@code appendDropDownListAttribute} method, and expects
   * the current docbase value to be selected
   *
   * @param count the number of docbases to return
   * @param value the current docbase value
   * @param options the expected number of options in the select list
   */
  private void testAddla(int count, String value, int options) {
    testAddla(count, value, options, value);
  }

  private int options(String selectList) {
    return occurrences("<option ", selectList);
  }

  private String selected(String selectList) {
    Matcher m = Pattern.compile("value=\"(.*)\" selected='selected'")
        .matcher(selectList);
    if (m.find()) {
      assertEquals(1, m.groupCount());
      return m.group(1);
    } else {
      return null;
    }
  }

  private int occurrences(String pattern, String target) {
    Matcher m = Pattern.compile(pattern).matcher(target);
    int count = 0;
    while (m.find()) {
      count++;
    }
    return count;
  }

  /**
   * When there are zero docbases, the empty string is added to the
   * list and selected.
   */
  public void testGcfAddlaZero() {
    testAddla(0, "", 1);
  }

  /** When there is only one docbase, it is the only option, and selected. */
  public void testGcfAddlaOne() {
    testAddla(1, "", 1, DmInitialize.DM_DOCBASE);
  }

  /**
   * When there are multple docbases, the empty string is added to the
   * list and selected.
   */
  public void testGcfAddlaMultiple() {
    testAddla(2, "", 3);
  }

  public void testGpcfAddlaOne() {
    testAddla(1, DmInitialize.DM_DOCBASE, 1);
  }

  public void testGpcfAddlaMultiple() {
    testAddla(2, DmInitialize.DM_DOCBASE, 2);
  }

  /**
   * When the current docbase is not in the list, it is added to the
   * list and selected.
   */
  public void testGpcfAddlaInvalidZero() {
    testAddla(0, "other", 1);
  }

  public void testGpcfAddlaInvalidOne() {
    testAddla(1, "other", 2);
  }

  public void testGpcfAddlaInvalidMultiple() {
    testAddla(2, "other", 3);
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
