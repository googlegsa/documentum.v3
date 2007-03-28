package com.google.enterprise.connector.dctm;

import java.util.HashMap;

import com.google.enterprise.connector.spi.ConfigureResponse;

import junit.framework.TestCase;

public class DctmConnectorTypeTest extends TestCase {

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmConnectorType.setConfigKeys(List)'
	 */
	public void testSetConfigKeysList() {

	}

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmConnectorType.getConfigForm(String)'
	 */
	public void testGetConfigForm() {
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authenticationType", "webtopServerUrl" };
		test.setConfigKeys(fiels);
		test.getConfigForm("en").getFormSnippet();

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
		map.put("clientX",
				"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		map.put("authenticationType", "api");
		map.put("webtopServerUrl", "http://swp-vm-wt:8080/webtop/");
		map.put("additionalWhereClause", "and owner_name != 'Administrator'");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authenticationType", "webtopServerUrl",
				"additionalWhereClause" };
		test.setConfigKeys(fiels);
		ConfigureResponse resp = test.validateConfig(map, "en");
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
		map.put("password", "p@ssw0r");
		map.put("docbase", "gsadctm");
		map.put("clientX",
				"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		map.put("authenticationType", "api");
		map.put("webtopServerUrl", "http://swp-vm-wt:8080/webtop/");
		map.put("additionalWhereClause", "and owner_name != 'Administrator'");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authenticationType", "webtopServerUrl",
				"additionalWhereClause" };
		test.setConfigKeys(fiels);
		ConfigureResponse resp = test.validateConfig(map, "en");
		assertTrue(resp.getMessage().startsWith("Some required configuration is missing: Please check the credentials."));
	}
	
	public void testValidateConfigWithDocbaseError() {
		HashMap map = new HashMap();
		map.put("login", "queryUser");
		map.put("password", "p@ssw0rd");
		map.put("docbase", "gsadct");
		map.put("clientX",
				"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		map.put("authenticationType", "api");
		map.put("webtopServerUrl", "http://swp-vm-wt:8080/webtop/");
		map.put("additionalWhereClause", "and owner_name != 'Administrator'");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authenticationType", "webtopServerUrl",
				"additionalWhereClause" };
		test.setConfigKeys(fiels);
		test.validateConfig(map, "en").getFormSnippet();

		ConfigureResponse resp = test.validateConfig(map, "en");
		assertTrue(resp.getMessage().startsWith("Some required configuration is missing: Docbase name is incorrect."));
	}
	
	public void testValidateConfigWithServerWebtopError() {
		HashMap map = new HashMap();
		map.put("login", "queryUser");
		map.put("password", "p@ssw0rd");
		map.put("docbase", "gsadctm");
		map.put("clientX",
				"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		map.put("authenticationType", "api");
		map.put("webtopServerUrl", "http://swp-vm-w:8080/webtop/");
		map.put("additionalWhereClause", "and owner_name != 'Administrator'");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authenticationType", "webtopServerUrl",
				"additionalWhereClause" };
		test.setConfigKeys(fiels);
		test.validateConfig(map, "en").getFormSnippet();
		ConfigureResponse resp = test.validateConfig(map, "en");
		assertTrue(resp.getMessage().startsWith("Some required configuration is missing: Please check the webtop server url."));
		

	}
	
	public void testValidateConfigWithWebtopError() {
		HashMap map = new HashMap();
		map.put("login", "queryUser");
		map.put("password", "p@ssw0rd");
		map.put("docbase", "gsadctm");
		map.put("clientX",
				"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		map.put("authenticationType", "api");
		map.put("webtopServerUrl", "http://swp-vm-wt:8080/webto/");
		map.put("additionalWhereClause", "and owner_name != 'Administrator'");
		DctmConnectorType test = new DctmConnectorType();
		String[] fiels = { "login", "password", "docbase", "clientX",
				"authenticationType", "webtopServerUrl",
				"additionalWhereClause" };
		test.setConfigKeys(fiels);
		ConfigureResponse resp = test.validateConfig(map, "en");
		assertTrue(resp.getMessage().startsWith("Some required configuration is missing: Please check the webtop server url and that the server is up and running. "));
		
		

	}

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmConnectorType.getPopulatedConfigForm(Map,
	 * String)'
	 */
	public void testGetPopulatedConfigForm() {

	}

}
