package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryLoginException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmLoginInfoTest extends TestCase {

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

	public void testGetSetUser() throws RepositoryLoginException {
		String user = DmInitialize.DM_LOGIN_OK1;
		String docbase = DmInitialize.DM_DOCBASE;
		loginInfo.setUser(user);
		sessionManager.setDocbaseName(docbase);
		sessionManager.setIdentity(docbase, loginInfo);
		dctmClientX.setSessionManager(sessionManager);
		Assert.assertEquals(DmInitialize.DM_LOGIN_OK1, loginInfo.getUser());
	}

	public void testGetSetPassword() throws RepositoryLoginException {
		String password = DmInitialize.DM_PWD_OK1;
		String docbase = DmInitialize.DM_DOCBASE;
		loginInfo.setPassword(password);
		sessionManager.setDocbaseName(docbase);
		sessionManager.setIdentity(docbase, loginInfo);
		dctmClientX.setSessionManager(sessionManager);
		Assert.assertEquals(DmInitialize.DM_PWD_OK1, loginInfo.getPassword());
	}

}
