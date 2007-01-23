package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DctmClientXTest extends TestCase {

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.dctmdfcwrap.DctmClientX.getId(String)'
	 */
	public void testGetId() {
		IClientX clientX = new DctmClientX();
		IId id = clientX.getId("xxxxxxxxxxxxxxxx");
		Assert.assertTrue(id instanceof IDctmId);
	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.dctmdfcwrap.DctmClientX.getLocalClient()'
	 */
	public void testGetLocalClient() throws RepositoryException {
		IClientX clientX = new DctmClientX();
		IClient localClient = clientX.getLocalClient();
		Assert.assertTrue(localClient instanceof IDctmClient);		
	}

}
