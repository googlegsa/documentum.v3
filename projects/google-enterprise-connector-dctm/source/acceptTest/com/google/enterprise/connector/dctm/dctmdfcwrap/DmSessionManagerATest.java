package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmSessionManagerATest extends TestCase {

	IClientX dctmClientX;

	IClient localClient;

	ISessionManager sessionManager;

	ILoginInfo loginInfo;

	public void setUp() throws Exception {
		super.setUp();
		dctmClientX = new DmClientX();
		localClient = dctmClientX.getLocalClient();
		sessionManager = localClient.newSessionManager();
		loginInfo = dctmClientX.getLoginInfo();
	}

	public void testGetSession() throws RepositoryException {

		String user = DmInitialize.DM_LOGIN_OK4;
		String password = DmInitialize.DM_PWD_OK4;
		String docbase = DmInitialize.DM_DOCBASE;

		loginInfo.setUser(user);
		loginInfo.setPassword(password);

		sessionManager.setIdentity(docbase, loginInfo);

		ISession session = null;
		try {
			session = sessionManager.getSession(docbase);
			Assert.assertNotNull(session);
			Assert.assertTrue(session instanceof DmSession);
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}
	}

	public void testSetIdentity() throws LoginException {
		String user = DmInitialize.DM_LOGIN_OK4;
		String password = DmInitialize.DM_PWD_OK4;
		String docbase = DmInitialize.DM_DOCBASE;

		loginInfo.setUser(user);
		loginInfo.setPassword(password);
		sessionManager.setIdentity(docbase, loginInfo);
	}

	public void testGetIdentity() throws LoginException {
		String user = DmInitialize.DM_LOGIN_OK4;
		String password = DmInitialize.DM_PWD_OK4;
		String docbase = DmInitialize.DM_DOCBASE;
		loginInfo.setUser(user);
		loginInfo.setPassword(password);
		sessionManager.setIdentity(docbase, loginInfo);
		ILoginInfo logInfo = sessionManager.getIdentity(docbase);
		Assert.assertEquals(logInfo.getUser(), user);
		Assert.assertEquals(logInfo.getPassword(), password);

	}

	public void testNewSession() throws LoginException, RepositoryException {
		ISession session =null;
		try {
			String user = DmInitialize.DM_LOGIN_OK4;
			String password = DmInitialize.DM_PWD_OK4;
			String docbase = DmInitialize.DM_DOCBASE;
			loginInfo.setUser(user);
			loginInfo.setPassword(password);
			sessionManager.setIdentity(docbase, loginInfo);
			session = sessionManager.newSession(docbase);
			Assert.assertNotNull(session);
			Assert.assertTrue(session instanceof DmSession);
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}
	}

	public void testAuthenticateOK() throws LoginException {
		String user = DmInitialize.DM_LOGIN_OK4;
		String password = DmInitialize.DM_PWD_OK4;
		String docbase = DmInitialize.DM_DOCBASE;
		loginInfo.setUser(user);
		loginInfo.setPassword(password);
		sessionManager.setIdentity(docbase, loginInfo);
		boolean rep = sessionManager.authenticate(docbase);
		Assert.assertTrue(rep);
	}

	public void testAuthenticateK0() throws LoginException {
		String user = DmInitialize.DM_LOGIN_KO;
		String password = DmInitialize.DM_PWD_KO;
		String docbase = DmInitialize.DM_DOCBASE;
		loginInfo.setUser(user);
		loginInfo.setPassword(password);
		sessionManager.setIdentity(docbase, loginInfo);
		boolean rep = sessionManager.authenticate(docbase);
		Assert.assertFalse(rep);
	}

	public void testClearIdentity() throws LoginException {
		String user = DmInitialize.DM_LOGIN_OK4;
		String password = DmInitialize.DM_PWD_OK4;
		String docbase = DmInitialize.DM_DOCBASE;
		loginInfo.setUser(user);
		loginInfo.setPassword(password);
		sessionManager.setIdentity(docbase, loginInfo);
		sessionManager.clearIdentity(docbase);
		ILoginInfo logInfo = sessionManager.getIdentity(docbase);
		Assert.assertNull(((DmLoginInfo) logInfo).getIdfLoginInfo());
	}

}
