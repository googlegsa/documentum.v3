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
	public void testAuthenticate() throws LoginException, RepositoryException {
		Connector myconn = new DctmConnector();
		
		((DctmConnector)myconn).setLogin("emilie");
		((DctmConnector)myconn).setPassword("p@ssw0rd");
		Session sess = (DctmSession) myconn.login();
		DctmAuthenticationManager authentManager = (DctmAuthenticationManager) sess.getAuthenticationManager();
		
		assertFalse(authentManager.authenticate("user1","falsePassword"));
		assertFalse(authentManager.authenticate("user1",null));
		assertFalse(authentManager.authenticate(null,"p@ssw0rd"));
		assertFalse(authentManager.authenticate(null,null));
		
		assertTrue(authentManager.authenticate("emilie","p@ssw0rd"));
		assertTrue(authentManager.authenticate("user1","p@ssw0rd"));
		assertTrue(authentManager.authenticate("user2","p@ssw0rd"));
		assertTrue(authentManager.authenticate("queryUser","p@ssw0rd"));
		assertTrue(authentManager.authenticate("Fred","UnDeux34"));

	}

}
