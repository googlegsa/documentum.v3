package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MockDmClientTest extends TestCase {

	/**
	 * Useless test
	 * 
	 */
	public void testGetLoginInfo() {
		IClientX dctmClientX = new MockDmClient();

		ILoginInfo loginInfo = dctmClientX.getLoginInfo();

		Assert.assertTrue(loginInfo instanceof MockDmLoginInfo);

		loginInfo.setUser("max");
		loginInfo.setPassword("foo");

		Assert.assertEquals("max", loginInfo.getUser());
		Assert.assertEquals("foo", loginInfo.getPassword());

	}

	public void testNewSessionManager() {

		IClientX dctmClientX = new MockDmClient();
		try {
			IClient localClient = dctmClientX.getLocalClient();
			ISessionManager sessionManager = localClient.newSessionManager();
			Assert.assertNotNull(sessionManager);
			Assert.assertTrue(sessionManager instanceof MockDmClient);
		} catch (RepositoryException e) {
			assertEquals(true, false);
		}

	}

	public void testSetAndClearIdentity() {
		IClientX dctmClientX = new MockDmClient();
		try {
			IClient localClient = dctmClientX.getLocalClient();
			ISessionManager sessionManager = localClient.newSessionManager();
			ILoginInfo ili = new MockDmLoginInfo();
			ili.setUser("mark");
			ili.setPassword("mark");
			sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);

			// No way to check identity as correctly been set but to try to
			// authenticate on the docbase
			assertTrue(sessionManager
					.authenticate("MockRepositoryEventLog7.txt"));

			sessionManager.clearIdentity("MockRepositoryEventLog7.txt");
			assertFalse(sessionManager
					.authenticate("MockRepositoryEventLog7.txt"));

		} catch (RepositoryException e) {
			assertEquals(true, false);
		}
	}

	public void testNewSession() {
		try {
			IClientX dctmClientX = new MockDmClient();
			IClient localClient = dctmClientX.getLocalClient();
			ISessionManager sessionManager = localClient.newSessionManager();
			try {
				sessionManager.newSession("MockRepositoryEventLog7.txt");
				assertTrue(false);
			} catch (LoginException e) {
				assertEquals(
						e.getMessage(),
						"newSession(MockRepositoryEventLog7.txt) called for docbase "
								+ "MockRepositoryEventLog7.txt without setting any "
								+ "credentials prior to this call");
			}
			ILoginInfo ili = new MockDmLoginInfo();
			ili.setUser("mark");
			ili.setPassword("mark");
			sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);
			ISession sess = sessionManager
					.newSession("MockRepositoryEventLog7.txt");
			assertTrue(sess instanceof MockDmSession);
			assertNotNull(sess);

		} catch (RepositoryException e) {
			assertEquals(true, false);
		}
	}

	public void testGetSessionAndGetAndSetDocbaseName() {
		try {
			IClientX dctmClientX = new MockDmClient();
			IClient localClient = null;
			localClient = dctmClientX.getLocalClient();
			ISessionManager sessionManager = localClient.newSessionManager();
			ILoginInfo ili = new MockDmLoginInfo();
			ili.setUser("mark");
			ili.setPassword("mark");
			sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);
			ISession sess7 = sessionManager
					.getSession("MockRepositoryEventLog7.txt");
			assertNotNull(sess7);
			assertTrue(sess7 instanceof MockDmSession);
			String checkName = sessionManager.getDocbaseName();
			assertEquals(checkName, "MockRepositoryEventLog7.txt");

			sessionManager.setIdentity("MockRepositoryEventLog2.txt", ili);
			ISession sess2 = sessionManager
					.getSession("MockRepositoryEventLog2.txt");
			assertNotNull(sess2);
			assertTrue(sess2 instanceof MockDmSession);
			checkName = sessionManager.getDocbaseName();
			assertEquals(checkName, "MockRepositoryEventLog2.txt");

			// Change the current docbase name
			sessionManager.setDocbaseName("MockRepositoryEventLog7.txt");
			checkName = sessionManager.getDocbaseName();
			assertEquals(checkName, "MockRepositoryEventLog7.txt");

		} catch (RepositoryException e) {
			assertTrue(false);
		}
	}
}
