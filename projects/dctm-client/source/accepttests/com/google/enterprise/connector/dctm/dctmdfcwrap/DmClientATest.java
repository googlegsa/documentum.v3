package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmClientATest extends TestCase {

	IClientX dctmClientX;

	IClient localClient;

	public void setUp() throws Exception {
		super.setUp();
		dctmClientX = new DmClientX();
		localClient = dctmClientX.getLocalClient();
	}

	public void testNewSessionManager() throws RepositoryException {
		ISessionManager sessionManager = localClient.newSessionManager();
		Assert.assertNotNull(sessionManager);
		Assert.assertTrue(sessionManager instanceof DmSessionManager);
	}

}
