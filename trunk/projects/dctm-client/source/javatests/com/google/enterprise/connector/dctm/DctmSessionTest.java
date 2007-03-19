package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

public class DctmSessionTest extends TestCase {
	
	DctmSession dctmSession = null;

	DctmConnector connector = null;

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

	public void testGetQueryTraversalManager() throws RepositoryException {
		QueryTraversalManager dctmQm = dctmSession.getQueryTraversalManager();
		String serverUrl = ((DctmQueryTraversalManager) dctmQm).getServerUrl();
		assertNotNull(dctmQm);

		assertEquals(serverUrl, DmInitialize.DM_WEBTOP_SERVER_URL);
	}

	public void testGetAuthenticationManager() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		AuthenticationManager dctmAm = dctmSession.getAuthenticationManager();
		assertNotNull(dctmAm);

	}

	public void testGetAuthorizationManager() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		AuthorizationManager dctmAz = dctmSession.getAuthorizationManager();
		assertNotNull(dctmAz);
	}

}
