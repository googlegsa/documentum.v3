package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DctmMockClientXTest extends TestCase {

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.dctmdfcwrap.DctmClientX.getId(String)'
	 */
	public void testGetId() {
		IClientX dctmClientX = new DctmMockClient();
		IId id = dctmClientX.getId("xxxxxxxxxxxxxxxx");
		Assert.assertTrue(id instanceof DctmMockId);
	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.dctmdfcwrap.DctmClientX.getLocalClient()'
	 */
	public void testGetLocalClient() throws RepositoryException {
		IClientX dctmClientX = new DctmMockClient();
		IClient localClient = dctmClientX.getLocalClient();
		Assert.assertTrue(localClient instanceof DctmMockClient);		
	}

}
