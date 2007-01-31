package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MockDmSessionManagerTest extends TestCase {

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.dctmdfcwrap.DmSessionManager.getSession(String)'
	 */
	public void testGetSession() throws RepositoryException {
		IClientX dctmClientX = new MockDmClient();
		IClient localClient = dctmClientX.getLocalClient();

		ISessionManager sessionManager = localClient.newSessionManager();

		String user = "queryUser";
		String password = "p@ssw0rd";
		String docbase = "gsadctm";

		ILoginInfo loginInfo = dctmClientX.getLoginInfo();
		loginInfo.setUser(user);
		loginInfo.setPassword(password);

		sessionManager.setIdentity(docbase, loginInfo);

		ISession session = null;
		try {
			session = sessionManager.getSession(docbase);
			Assert.assertNotNull(session);
			Assert.assertTrue(session instanceof MockDmSession);
		} finally {
			sessionManager.release(session);
		}
	}
}
