package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MockDmClientXTest extends TestCase {

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX.getId(String)'
	 */
	/*public void testGetId() {
		IClientX dctmClientX = new MockDmClient();
		IId id = dctmClientX.getId("xxxxxxxxxxxxxxxx");
		Assert.assertTrue(id instanceof MockDmId);
	}*/

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX.getLocalClient()'
	 */
	public void testGetLocalClient() throws RepositoryException {
		IClientX dctmClientX = new MockDmClient();
		IClient localClient = dctmClientX.getLocalClient();
		Assert.assertTrue(localClient instanceof MockDmClient);
	}

}
