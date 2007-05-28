package com.google.enterprise.connector.dctm;

import java.util.HashMap;
import java.util.Locale;

import com.google.enterprise.connector.spi.ConfigureResponse;

import junit.framework.TestCase;

public class DctmConnectorTypeTest extends TestCase {

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmConnectorType.getConfigForm(String)'
	 */
	public void testGetConfigForm() {
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authentication_type", "webtop_display_url", "where_clause",
				"is_public" };
		test.setConfigKeys(fiels);
		String expectedForm = "<tr>\r\n"
				+ "<td>User name</td>\r\n<td><input type=\"text\" value=\"\" name=\"login\"/></td>\r\n</tr>"
				+ "\r\n<tr>\r\n<td>Password</td>\r\n<td><input type=\"password\" value=\"\" name=\"password\"/></td>"
				+ "\r\n</tr>\r\n<tr>\r\n<td>Repository</td>\r\n<td><select name=\"docbase\">\n\t<option value=\"gsadctm\">gsadctm</option>\n\t<option value=\"gdoc\">gdoc</option>\n</select>\r\n<tr>"
				+ "\r\n<td><input type=\"hidden\" value=\"\" name=\"clientX\"/></td>\r\n</tr>\r\n<tr>\r\n<td><input type=\"hidden\" value=\"\" name=\"authentication_type\"/></td>"
				+ "\r\n</tr>\r\n<tr>\r\n<td>Display URL</td>\r\n<td><input type=\"text\" value=\"\" name=\"webtop_display_url\"/></td>"
				+ "\r\n</tr>\r\n<tr>\r\n<td><input type=\"hidden\" value=\"\" name=\"where_clause\"/></td>\r\n</tr>\r\n<tr>\r\n<td><input type=CHECKBOX name=\"is_public\" />Make public</td>\r\n</tr>\r\n";
		System.out.println(test.getConfigForm(Locale.US).getFormSnippet());

		assertEquals(expectedForm, test.getConfigForm(Locale.US)
				.getFormSnippet());

	}

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmConnectorType.validateConfig(Map,
	 * String)'
	 */
	public void testValidateConfig() {
		HashMap map = new HashMap();
		map.put("login", "queryUser");
		map.put("password", "p@ssw0rd");
		map.put("docbase", "gsadctm");
		map.put("clientX", "");
		map.put("authentication_type", "");
		map.put("webtop_display_url", "http://swp-vm-wt:8080/webtop/");
		map.put("where_clause", "");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authentication_type", "webtop_display_url", "where_clause",
				"is_public" };
		test.setConfigKeys(fiels);
		ConfigureResponse resp = test.validateConfig(map, Locale.US);
		assertNull(resp);

	}

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmConnectorType.validateConfig(Map,
	 * String)'
	 */
	public void testValidateConfigWithConnectionError() {
		HashMap map = new HashMap();
		map.put("login", "queryUser");
		map.put("password", "p@ssw@r");
		map.put("docbase", "gsadctm");
		map.put("clientX",
				"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		map.put("authentication_type", "api");
		map.put("webtop_display_url", "http://swp-vm-wt:8080/webtop/");
		map.put("where_clause", "and owner_name != 'Administrator'");
		map.put("is_public", "false");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authentication_type", "webtop_display_url", "where_clause",
				"is_public" };
		test.setConfigKeys(fiels);
		ConfigureResponse resp = test.validateConfig(map, Locale.US);
		assertTrue(resp
				.getMessage()
				.startsWith(
						"<p><font color=\"#FF0000\">Some required configuration is missing: Please check the credentials."));
	}

	public void testValidateConfigWithDocbaseError() {
		HashMap map = new HashMap();
		map.put("login", "queryUser");
		map.put("password", "p@ssw0rd");
		map.put("docbase", "gsadct");
		map.put("clientX",
				"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		map.put("authentication_type", "api");
		map.put("webtop_display_url", "http://swp-vm-wt:8080/webtop/");
		map.put("where_clause", "and owner_name != 'Administrator'");
		map.put("is_public", "false");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authentication_type", "webtop_display_url", "where_clause",
				"is_public" };
		test.setConfigKeys(fiels);

		ConfigureResponse resp = test.validateConfig(map, Locale.US);
		assertTrue(resp
				.getMessage()
				.startsWith(
						"<p><font color=\"#FF0000\">Some required configuration is missing: Docbase name is incorrect."));
	}

	public void testValidateConfigWithServerWebtopError() {
		HashMap map = new HashMap();
		map.put("login", "queryUser");
		map.put("password", "p@ssw0rd");
		map.put("docbase", "gsadctm");
		map.put("clientX",
				"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		map.put("authentication_type", "api");
		map.put("webtop_display_url", "http://swp-vm-w:8080/webtop/");
		map.put("where_clause", "and owner_name != 'Administrator'");
		map.put("is_public", "false");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authentication_type", "webtop_display_url", "where_clause",
				"is_public" };
		test.setConfigKeys(fiels);
		ConfigureResponse resp = test.validateConfig(map, Locale.US);
		assertTrue(resp
				.getMessage()
				.startsWith(
						"<p><font color=\"#FF0000\">Some required configuration is missing: Please check the webtop server url."));

	}

	public void testValidateConfigWithWebtopError() {
		HashMap map = new HashMap();
		map.put("login", "queryUser");
		map.put("password", "p@ssw0rd");
		map.put("docbase", "gsadctm");
		map.put("clientX",
				"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		map.put("authentication_type", "api");
		map.put("webtop_display_url", "http://swp-vm-wt:8080/webto/");
		map.put("where_clause", "and owner_name != 'Administrator'");
		map.put("is_public", "false");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authentication_type", "webtop_display_url", "where_clause",
				"is_public" };
		test.setConfigKeys(fiels);
		ConfigureResponse resp = test.validateConfig(map, Locale.US);
		assertTrue(resp
				.getMessage()
				.startsWith(
						"<p><font color=\"#FF0000\">Some required configuration is missing: Please check the webtop server url and that the server is up and running."));

	}

	public void testValidateConfigWithQueryError() {
		HashMap map = new HashMap();
		map.put("login", "queryUser");
		map.put("password", "p@ssw0rd");
		map.put("docbase", "gsadctm");
		map.put("clientX",
				"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		map.put("authentication_type", "api");
		map.put("webtop_display_url", "http://swp-vm-wt:8080/webtop/");
		map.put("where_clause", "an owner_name != 'Administrator'");
		map.put("is_public", "false");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authentication_type", "webtop_display_url", "where_clause",
				"is_public" };
		test.setConfigKeys(fiels);
		ConfigureResponse resp = test.validateConfig(map, Locale.US);
		assertTrue(resp
				.getMessage()
				.startsWith(
						"<p><font color=\"#FF0000\">Some required configuration is missing: The additional where clause is not starting with the keyword 'AND'. Please check the additional where clause."));

	}

	public void testValidateConfigWithAnotherQueryError() {
		HashMap map = new HashMap();
		map.put("login", "queryUser");
		map.put("password", "p@ssw0rd");
		map.put("docbase", "gsadctm");
		map.put("clientX",
				"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		map.put("authentication_type", "api");
		map.put("webtop_display_url", "http://swp-vm-wt:8080/webtop/");
		map.put("where_clause", "and owne_name != 'Administrator'");
		map.put("is_public", "false");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authentication_type", "webtop_display_url", "where_clause",
				"is_public" };
		test.setConfigKeys(fiels);
		ConfigureResponse resp = test.validateConfig(map, Locale.US);
		assertTrue(resp
				.getMessage()
				.startsWith(
						"<p><font color=\"#FF0000\">Some required configuration is missing: Syntax error in DQL filter. You have specified an invalid attribute name."));

	}

	public void testValidateConfigWithAnotherSecondQueryError() {
		HashMap map = new HashMap();
		map.put("login", "queryUser");
		map.put("password", "p@ssw0rd");
		map.put("docbase", "gsadctm");
		map.put("clientX",
				"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		map.put("authentication_type", "api");
		map.put("webtop_display_url", "http://swp-vm-wt:8080/webtop/");
		map.put("where_clause", "and folde('/test_docs',descend)");
		map.put("is_public", "false");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authentication_type", "webtop_display_url", "where_clause",
				"is_public" };
		test.setConfigKeys(fiels);
		ConfigureResponse resp = test.validateConfig(map, Locale.US);
		assertTrue(resp
				.getMessage()
				.startsWith(
						"<p><font color=\"#FF0000\">Some required configuration is missing: Syntax error in DQL filter. A Parser Error (syntax error) has occurred."));

	}

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmConnectorType.getPopulatedConfigForm(Map,
	 * String)'
	 */
	public void testGetPopulatedConfigForm() {
		HashMap map = new HashMap();
		map.put("login", "queryUser");
		map.put("password", "p@ssw0rd");
		map.put("docbase", "gsadctm");
		map.put("clientX",
				"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		map.put("authentication_type", "api");
		map.put("webtop_display_url", "http://swp-vm-wt:8080/webtop/");
		map.put("where_clause", "an owner_name != 'Administrator'");
		map.put("is_public", "on");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authentication_type", "webtop_display_url", "where_clause",
				"is_public" };
		test.setConfigKeys(fiels);

		String expectedForm = "<tr>\r\n"
				+ "<td>User name</td>\r\n<td><input type=\"text\" value=\"queryUser\" name=\"login\"/></td>\r\n</tr>"
				+ "\r\n<tr>\r\n<td>Password</td>\r\n<td><input type=\"password\" value=\"p@ssw0rd\" name=\"password\"/></td>"
				+ "\r\n</tr>\r\n<tr>\r\n<td>Repository</td>\r\n<td><select name=\"docbase\">\n\t<option selected value=\"gsadctm\">gsadctm</option>\n\t<option value=\"gdoc\">gdoc</option>\n</select>\r\n<tr>"
				+ "\r\n<td><input type=\"hidden\" value=\"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX\" name=\"clientX\"/></td>\r\n</tr>\r\n<tr>\r\n<td><input type=\"hidden\" value=\"api\" name=\"authentication_type\"/></td>"
				+ "\r\n</tr>\r\n<tr>\r\n<td>Display URL</td>\r\n<td><input type=\"text\" value=\"http://swp-vm-wt:8080/webtop/\" name=\"webtop_display_url\"/></td>"
				+ "\r\n</tr>\r\n<tr>\r\n<td><input type=\"hidden\" value=\"an owner_name != 'Administrator'\" name=\"where_clause\"/></td>\r\n</tr>\r\n<tr>\r\n<td><input type=CHECKBOX name=\"is_public\" CHECKED/>Make public</td>\r\n</tr>\r\n";
		assertEquals(expectedForm, test.getPopulatedConfigForm(map, Locale.US)
				.getFormSnippet());

	}

}
