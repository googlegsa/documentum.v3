package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.TraversalManager;
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
				.setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
		((DctmConnector) connector).setIs_public("false");
		dctmSession = (DctmSession) connector.login();
	}

	public void testGetQueryTraversalManager() throws RepositoryException {
		TraversalManager dctmQm = dctmSession.getTraversalManager();
		String serverUrl = ((DctmTraversalManager) dctmQm).getServerUrl();
		assertNotNull(dctmQm);

		assertEquals(DmInitialize.DM_WEBTOP_SERVER_URL,serverUrl );
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
