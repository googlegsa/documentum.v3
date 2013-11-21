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

import com.google.enterprise.connector.spi.ConfigureResponse;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Locale;

public class DctmConnectorTypeTest extends TestCase {
  private HashMap<String, String> map;

  @Override
  protected void setUp() {
    map = new HashMap<String, String>();
  }

  /*
   * Test method for
   * 'com.google.enterprise.connector.dctm.DctmConnectorType.getConfigForm(String)'
   */
  public void testGetConfigForm() {
    DctmConnectorType test = new DctmConnectorType();
    String[] fiels = { "login", "Password", "docbase", "clientX",
        "authentication_type", "webtop_display_url", "where_clause",
        "is_public" };
    test.setConfigKeys(fiels);
    String expectedForm = "<tr>\r\n"
        + "<td>Username</td>\r\n<td><input type=\"text\" size=\"50\" value=\"\" name=\"login\"/></td>\r\n</tr>"
        + "\r\n<tr>\r\n<td>Password</td>\r\n<td><input type=\"Password\" value=\"\" name=\"Password\"/></td>"
        + "\r\n</tr>\r\n<tr>\r\n<td>Repository</td>\r\n<td><select name=\"docbase\">\n\t<option value=\"gdoc\">gdoc</option>\n</select>\r\n<tr>"
        + "\r\n<td><input type=\"hidden\" value=\"\" name=\"clientX\"/></td>\r\n</tr>\r\n<tr>\r\n<td><input type=\"hidden\" value=\"\" name=\"authentication_type\"/></td>"
        + "\r\n</tr>\r\n<tr>\r\n<td>Webtop URL</td>\r\n<td><input type=\"text\" size=\"50\" value=\"\" name=\"webtop_display_url\"/></td>"
        + "\r\n</tr>\r\n<tr>\r\n<td><input type=\"hidden\" value=\"\" name=\"where_clause\"/></td>\r\n</tr>\r\n<tr>\r\n<td><input type=CHECKBOX name=\"is_public\" />Make public</td>\r\n</tr>\r\n"
        + "<tr>\r\n<td><input type=\"hidden\" value=\"false\" name=\"is_public\"/></td>\r\n</tr>\r\n";
    assertEquals(expectedForm, test.getConfigForm(Locale.ENGLISH)
        .getFormSnippet());
  }

  /*
   * Test method for
   * 'com.google.enterprise.connector.dctm.DctmConnectorType.validateConfig(Map,
   * String)'
   */
  public void testValidateConfig() {
    map.put("login", "queryUser");
    map.put("Password", "p@ssw0rd");
    map.put("docbase", "gsadctm");
    map.put("clientX", "");
    map.put("authentication_type", "");
    map.put("webtop_display_url", "http://swp-vm-wt:8080/webtop/");
    map.put("where_clause", "");
    DctmConnectorType test = new DctmConnectorType();
    String[] fiels = { "login", "Password", "docbase", "clientX",
        "authentication_type", "webtop_display_url", "where_clause",
        "is_public" };
    test.setConfigKeys(fiels);
    ConfigureResponse resp = test.validateConfig(map, Locale.US, null);
    assertNull(resp);
  }

  /*
   * Test method for
   * 'com.google.enterprise.connector.dctm.DctmConnectorType.validateConfig(Map,
   * String)'
   */
  public void testValidateConfigWithConnectionError() {
    map.put("login", "queryUser");
    map.put("Password", "p@ssw@r");
    map.put("docbase", "gsadctm");
    map.put("clientX",
        "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
    map.put("authentication_type", "api");
    map.put("webtop_display_url", "http://swp-vm-wt:8080/webtop/");
    map.put("where_clause", "and owner_name != 'Administrator'");
    map.put("is_public", "false");
    DctmConnectorType test = new DctmConnectorType();
    String[] fiels = { "login", "Password", "docbase", "clientX",
        "authentication_type", "webtop_display_url", "where_clause",
        "is_public" };
    test.setConfigKeys(fiels);
    ConfigureResponse resp = test.validateConfig(map, Locale.US, null);
    assertTrue(resp
        .getMessage()
        .startsWith(
            "<p><font color=\"#FF0000\">Some required configuration is missing: Please check the Superuser credentials."));
  }

  public void testValidateConfigWithServerWebtopError() {
    map.put("login", "queryUser");
    map.put("Password", "p@ssw0rd");
    map.put("docbase", "gsadctm");
    map.put("clientX",
        "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
    map.put("authentication_type", "api");
    map.put("webtop_display_url", "http://swp-vm-w:8080/webtop/");
    map.put("where_clause", "and owner_name != 'Administrator'");
    map.put("is_public", "false");
    DctmConnectorType test = new DctmConnectorType();
    String[] fiels = { "login", "Password", "docbase", "clientX",
        "authentication_type", "webtop_display_url", "where_clause",
        "is_public" };
    test.setConfigKeys(fiels);
    ConfigureResponse resp = test.validateConfig(map, Locale.US, null);
    assertTrue(resp
        .getMessage()
        .startsWith(
            "<p><font color=\"#FF0000\">Some required configuration is missing: Please check the Webtop URL."));
  }

  public void testValidateConfigWithWebtopError() {
    map.put("login", "queryUser");
    map.put("Password", "p@ssw0rd");
    map.put("docbase", "gsadctm");
    map.put("clientX",
        "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
    map.put("authentication_type", "api");
    map.put("webtop_display_url", "http://swp-vm-wt:8080/webto/");
    map.put("where_clause", "and owner_name != 'Administrator'");
    map.put("is_public", "false");
    DctmConnectorType test = new DctmConnectorType();
    String[] fiels = { "login", "Password", "docbase", "clientX",
        "authentication_type", "webtop_display_url", "where_clause",
        "is_public" };
    test.setConfigKeys(fiels);
    ConfigureResponse resp = test.validateConfig(map, Locale.US, null);
    assertTrue(resp
        .getMessage()
        .startsWith(
            "<p><font color=\"#FF0000\">Some required configuration is missing: Please make sure that the Webtop URL is correct and that the application server is up and running."));
  }

  public void testValidateConfigWithQueryError() {
    map.put("login", "queryUser");
    map.put("Password", "p@ssw0rd");
    map.put("docbase", "gsadctm");
    map.put("clientX",
        "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
    map.put("authentication_type", "api");
    map.put("webtop_display_url", "http://swp-vm-wt:8080/webtop/");
    map.put("where_clause", "an owner_name != 'Administrator'");
    map.put("is_public", "false");
    DctmConnectorType test = new DctmConnectorType();
    String[] fiels = { "login", "Password", "docbase", "clientX",
        "authentication_type", "webtop_display_url", "where_clause",
        "is_public" };
    test.setConfigKeys(fiels);
    ConfigureResponse resp = test.validateConfig(map, Locale.US, null);
    assertTrue(resp
        .getMessage()
        .startsWith(
            "<p><font color=\"#FF0000\">Some required configuration is missing: The additional WHERE clause must start with the keyword AND. Please check the additional WHERE clause."));
  }

  public void testValidateConfigWithAnotherQueryError() {
    map.put("login", "user1");
    map.put("Password", "p@ssw0rd");
    map.put("docbase", "gsadctm");
    map.put("clientX",
        "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
    map.put("authentication_type", "api");
    map.put("webtop_display_url",
        "http://swp-vm-wt:8080/webtop/drl/objectId/");
    map.put("where_clause", "and owne_name != 'Administrator'");
    map.put("is_public", "false");
    DctmConnectorType test = new DctmConnectorType();
    String[] fiels = { "login", "Password", "docbase", "clientX",
        "authentication_type", "webtop_display_url", "where_clause",
        "is_public" };
    test.setConfigKeys(fiels);
    ConfigureResponse resp = test.validateConfig(map, Locale.US, null);
    assertTrue(resp
        .getMessage()
        .startsWith(
            "<p><font color=\"#FF0000\">Syntax error in DQL filter. You have specified an invalid property name."));
  }

  public void testValidateConfigWithAnotherSecondQueryError() {
    map.put("login", "queryUser");
    map.put("Password", "p@ssw0rd");
    map.put("docbase", "gsadctm");
    map.put("clientX",
        "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
    map.put("authentication_type", "api");
    map.put("webtop_display_url", "http://swp-vm-wt:8080/webtop/");
    map.put("where_clause", "and folde('/test_docs',descend)");
    map.put("is_public", "false");
    DctmConnectorType test = new DctmConnectorType();
    String[] fiels = { "login", "Password", "docbase", "clientX",
        "authentication_type", "webtop_display_url", "where_clause",
        "is_public" };
    test.setConfigKeys(fiels);
    ConfigureResponse resp = test.validateConfig(map, Locale.US, null);
    assertTrue(resp
        .getMessage()
        .startsWith(
            "<p><font color=\"#FF0000\">Syntax error in DQL filter. A parser error (syntax error) has occurred."));
  }

  /*
   * Test method for
   * 'com.google.enterprise.connector.dctm.DctmConnectorType.getPopulatedConfigForm(Map,
   * String)'
   */
  public void testGetPopulatedConfigForm() {
    map.put("login", "queryUser");
    map.put("Password", "p@ssw0rd");
    map.put("docbase", "gsadctm");
    map.put("clientX",
        "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
    map.put("authentication_type", "api");
    map.put("webtop_display_url", "http://swp-vm-wt:8080/webtop/");
    map.put("where_clause", "an owner_name != 'Administrator'");
    map.put("is_public", "on");
    DctmConnectorType test = new DctmConnectorType();
    String[] fiels = { "login", "Password", "docbase", "clientX",
        "authentication_type", "webtop_display_url", "where_clause",
        "is_public" };
    test.setConfigKeys(fiels);

    String expectedForm = "<tr>\r\n"
        + "<td>Username</td>\r\n<td><input type=\"text\" value=\"queryUser\" name=\"login\"/></td>\r\n</tr>"
        + "\r\n<tr>\r\n<td>Password</td>\r\n<td><input type=\"Password\" value=\"p@ssw0rd\" name=\"Password\"/></td>"
        + "\r\n</tr>\r\n<tr>\r\n<td>Repository</td>\r\n<td><select name=\"docbase\">\n\t<option selected value=\"gsadctm\">gsadctm</option>\n\t<option value=\"gdoc\">gdoc</option>\n</select>\r\n<tr>"
        + "\r\n<td><input type=\"hidden\" value=\"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX\" name=\"clientX\"/></td>\r\n</tr>\r\n<tr>\r\n<td><input type=\"hidden\" value=\"api\" name=\"authentication_type\"/></td>"
        + "\r\n</tr>\r\n<tr>\r\n<td>Webtop URL</td>\r\n<td><input type=\"text\" value=\"http://swp-vm-wt:8080/webtop/\" name=\"webtop_display_url\"/></td>"
        + "\r\n</tr>\r\n<tr>\r\n<td><input type=\"hidden\" value=\"an owner_name != 'Administrator'\" name=\"where_clause\"/></td>\r\n</tr>\r\n"
        + "<tr>\r\n<td><input type=CHECKBOX name=\"is_public\" CHECKED/>Make public</td>\r\n</tr>\r\n"
        + "<tr>\r\n<td><input type=\"hidden\" value=\"false\" name=\"is_public\"/></td>\r\n</tr>\r\n";
    System.out.println(test.getPopulatedConfigForm(map, Locale.US)
        .getFormSnippet());
    assertEquals(expectedForm, test.getPopulatedConfigForm(map, Locale.US)
        .getFormSnippet());
  }
}
