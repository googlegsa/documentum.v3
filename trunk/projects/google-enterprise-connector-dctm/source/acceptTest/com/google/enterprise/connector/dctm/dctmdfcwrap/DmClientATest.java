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

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.dctmdfcwrap.DmClient.newSession(String,
	 * ILoginInfo)'
	 */
	public void testNewSession() throws RepositoryException {
		ILoginInfo loginInfo = dctmClientX.getLoginInfo();
		loginInfo.setUser(DmInitialize.DM_LOGIN_OK4);
		loginInfo.setPassword(DmInitialize.DM_PWD_OK1);

		ISession session = localClient.newSession(DmInitialize.DM_DOCBASE,
				loginInfo);

		Assert.assertNotNull(session);
		Assert.assertTrue(session instanceof DmSession);
	}

	public void testNewSessionManager() throws RepositoryException {
		ISessionManager sessionManager = localClient.newSessionManager();
		Assert.assertNotNull(sessionManager);
		Assert.assertTrue(sessionManager instanceof DmSessionManager);
	}

	public void authenticateOK() throws RepositoryException {
		ILoginInfo loginInfo = dctmClientX.getLoginInfo();
		loginInfo.setUser(DmInitialize.DM_LOGIN_OK4);
		loginInfo.setPassword(DmInitialize.DM_PWD_OK1);
		boolean rep = localClient.authenticate(DmInitialize.DM_DOCBASE,
				loginInfo);
		Assert.assertTrue(rep);

	}

	public void authenticateK0() throws RepositoryException {
		ILoginInfo loginInfo = dctmClientX.getLoginInfo();
		loginInfo.setUser(DmInitialize.DM_LOGIN_KO);
		loginInfo.setPassword(DmInitialize.DM_PWD_KO);
		boolean rep = localClient.authenticate(DmInitialize.DM_DOCBASE,
				loginInfo);
		Assert.assertTrue(rep);

	}
}
