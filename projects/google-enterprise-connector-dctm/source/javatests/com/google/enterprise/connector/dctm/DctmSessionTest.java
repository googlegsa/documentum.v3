package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

public class DctmSessionTest extends TestCase {
	DctmSession dctmSession = null;

	DctmConnector connector = null;

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmSession.DctmSession(String,
	 * String, String, String, String, String, String, String, String)'
	 */
	public void setUp() throws Exception {
		super.setUp();
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector)
				.setWebtopServerUrl(DmInitialize.DM_WEBTOP_SERVER_URL);
		dctmSession = (DctmSession) connector.login();
	}

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmSession.getQueryTraversalManager()'
	 */
	public void testGetQueryTraversalManager() throws RepositoryException {
		QueryTraversalManager DctmQm = dctmSession.getQueryTraversalManager();
		String serverUrl = ((DctmQueryTraversalManager) DctmQm).getServerUrl();
		assertNotNull(DctmQm);

		assertEquals(serverUrl, DmInitialize.DM_WEBTOP_SERVER_URL);
	}

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmSession.getAuthenticationManager()'
	 */
	public void testGetAuthenticationManager() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		AuthenticationManager DctmAm = dctmSession.getAuthenticationManager();
		assertNotNull(DctmAm);

	}

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmSession.getAuthorizationManager()'
	 */
	public void testGetAuthorizationManager() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		AuthorizationManager DctmAz = dctmSession.getAuthorizationManager();
		assertNotNull(DctmAz);
	}

}
