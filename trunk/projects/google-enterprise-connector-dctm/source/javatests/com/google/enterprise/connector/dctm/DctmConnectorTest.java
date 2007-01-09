package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

public class DctmConnectorTest extends TestCase {

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmConnector.login()'
	 */
	public void testLogin() {
		try {
			DctmConnector connector = new DctmConnector();
			assertNotNull(connector.login());
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
