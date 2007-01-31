package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

import junit.framework.TestCase;

public class DctmAuthenticationManagerTest extends TestCase {

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmAuthenticationManager.authenticate(String, String)'
	 */
	
	
	public void testAuthenticate() throws RepositoryException{
		Connector connector = new DctmConnector();
		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector).setQueryStringUnboundedDefault(DmInitialize.DM_QUERY_STRING_UNBOUNDED_DEFAULT);
		((DctmConnector) connector).setWebtopServerUrl(DmInitialize.DM_WEBTOP_SERVER_URL);
		((DctmConnector) connector).setQueryStringBoundedDefault(DmInitialize.DM_QUERY_STRING_BOUNDED_DEFAULT);
		((DctmConnector) connector).setAttributeName(DmInitialize.DM_ATTRIBUTE_NAME);
		((DctmConnector) connector).setQueryStringAuthoriseDefault(DmInitialize.DM_QUERY_STRING_AUTHORISE_DEFAULT);
		
	
		Session sess = (DctmSession) connector.login();
		DctmAuthenticationManager authentManager = (DctmAuthenticationManager) sess.getAuthenticationManager();
		
		
		assertTrue(authentManager.authenticate(DmInitialize.DM_LOGIN_OK1,DmInitialize.DM_PWD_OK1));
		assertFalse(authentManager.authenticate(DmInitialize.DM_LOGIN_OK2,DmInitialize.DM_PWD_KO));
		assertTrue(authentManager.authenticate(DmInitialize.DM_LOGIN_OK2,DmInitialize.DM_PWD_OK2));
		assertFalse(authentManager.authenticate(DmInitialize.DM_LOGIN_OK2,DmInitialize.DM_PWD_KO));
		assertFalse(authentManager.authenticate(DmInitialize.DM_LOGIN_OK2,null));
		assertFalse(authentManager.authenticate(null,DmInitialize.DM_PWD_OK1));
		assertFalse(authentManager.authenticate(null,null));
		
		assertTrue(authentManager.authenticate(DmInitialize.DM_LOGIN_OK3,DmInitialize.DM_PWD_OK3));
		assertTrue(authentManager.authenticate(DmInitialize.DM_LOGIN_OK1,DmInitialize.DM_PWD_OK1));
		assertTrue(authentManager.authenticate(DmInitialize.DM_LOGIN_OK5,DmInitialize.DM_PWD_OK5));
		
	}
	
	
}
