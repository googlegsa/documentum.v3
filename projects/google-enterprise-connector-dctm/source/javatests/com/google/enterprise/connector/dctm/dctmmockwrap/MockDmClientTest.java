package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MockDmClientTest extends TestCase {

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.dctmdfcwrap.DmClient.newSession(String, ILoginInfo)'
	 */
	public void testNewSession() throws RepositoryException {
		IClientX dctmClientX = new MockDmClient();
		IClient localClient = dctmClientX.getLocalClient();

		String user = "queryUser";
		String password = "p@ssw0rd";
		String docbase = "gsadctm";

		ILoginInfo loginInfo = dctmClientX.getLoginInfo();
		loginInfo.setUser(user);
		loginInfo.setPassword(password);

		ISession session = localClient.newSession(docbase, loginInfo);

		Assert.assertNotNull(session);
		Assert.assertTrue(session instanceof MockDmSession);
	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.dctmdfcwrap.DmClient.getLoginInfo()'
	 */
	public void testGetLoginInfo() throws RepositoryException {
		IClientX dctmClientX = new MockDmClient();
		IClient localClient = dctmClientX.getLocalClient();

		ILoginInfo loginInfo = dctmClientX.getLoginInfo();
		Assert.assertTrue(loginInfo instanceof MockDmLoginInfo);

		loginInfo.setUser("max");
		loginInfo.setPassword("foo");

		Assert.assertEquals("max", loginInfo.getUser());
		Assert.assertEquals("foo", loginInfo.getPassword());

	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.dctmdfcwrap.DmClient.newSessionManager()'
	 */
	public void testNewSessionManager() throws RepositoryException {
		IClientX dctmClientX = new MockDmClient();
		IClient localClient = dctmClientX.getLocalClient();

		ISessionManager sessionManager = localClient.newSessionManager();
		Assert.assertNotNull(sessionManager);
		Assert.assertTrue(sessionManager instanceof MockDmClient);

	}

}
