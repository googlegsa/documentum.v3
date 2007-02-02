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
				.setQueryStringUnboundedDefault(DmInitialize.DM_QUERY_STRING_UNBOUNDED_DEFAULT);
		((DctmConnector) connector)
				.setWebtopServerUrl(DmInitialize.DM_WEBTOP_SERVER_URL);
		((DctmConnector) connector)
				.setQueryStringBoundedDefault(DmInitialize.DM_QUERY_STRING_BOUNDED_DEFAULT);
		((DctmConnector) connector)
				.setAttributeName(DmInitialize.DM_ATTRIBUTE_NAME);
		((DctmConnector) connector)
				.setQueryStringAuthoriseDefault(DmInitialize.DM_QUERY_STRING_AUTHORISE_DEFAULT);
		dctmSession = (DctmSession) connector.login();
	}

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmSession.getQueryTraversalManager()'
	 */
	public void testGetQueryTraversalManager() throws RepositoryException {
		QueryTraversalManager DctmQm = dctmSession.getQueryTraversalManager();
		String boundedTraversalQuery = ((DctmQueryTraversalManager) DctmQm)
				.getBoundedTraversalQuery();
		String unboundedTraversalQuery = ((DctmQueryTraversalManager) DctmQm)
				.getUnboundedTraversalQuery();
		String serverUrl = ((DctmQueryTraversalManager) DctmQm).getServerUrl();
		assertNotNull(DctmQm);
		assertEquals(boundedTraversalQuery,
				DmInitialize.DM_QUERY_STRING_BOUNDED_DEFAULT);
		assertEquals(unboundedTraversalQuery,
				DmInitialize.DM_QUERY_STRING_UNBOUNDED_DEFAULT);
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
		String queryStringAuthoriseDefault = ((DctmAuthorizationManager) DctmAz)
				.getQueryStringAuthoriseDefault();
		String attributeName = ((DctmAuthorizationManager) DctmAz)
				.getAttributeName();
		assertEquals(queryStringAuthoriseDefault,
				DmInitialize.DM_QUERY_STRING_AUTHORISE_DEFAULT);
		assertEquals(attributeName, DmInitialize.DM_ATTRIBUTE_NAME);
		assertNotNull(DctmAz);
	}

}
