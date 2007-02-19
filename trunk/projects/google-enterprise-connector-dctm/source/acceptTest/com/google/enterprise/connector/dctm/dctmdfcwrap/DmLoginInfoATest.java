package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.DebugFinalData;
import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.LoginException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmLoginInfoATest extends TestCase {

	IClientX dctmClientX;

	IClient localClient;

	ISessionManager sessionManager;

	ISession session;

	ILoginInfo loginInfo;

	public void setUp() throws Exception {
		super.setUp();
		dctmClientX = new DmClientX();
		localClient = dctmClientX.getLocalClient();
		sessionManager = localClient.newSessionManager();
		loginInfo = dctmClientX.getLoginInfo();

	}

	public void testGetSetUser() throws LoginException {
		String user = DmInitialize.DM_LOGIN_OK1;
		String docbase = DmInitialize.DM_DOCBASE;
		if (DebugFinalData.debugInEclipse) {
			System.out.println("docbase vaut " + docbase);
		}	
		loginInfo.setUser(user);
		sessionManager.setDocbaseName(docbase);
		sessionManager.setIdentity(docbase, loginInfo);
		dctmClientX.setSessionManager(sessionManager);
		String myUser = loginInfo.getUser();
		Assert.assertEquals(myUser, DmInitialize.DM_LOGIN_OK1);
	}

	public void testGetSetPassword() throws LoginException {
		String password = DmInitialize.DM_PWD_OK1;
		String docbase = DmInitialize.DM_DOCBASE;
		if (DebugFinalData.debugInEclipse) {
			System.out.println("docbase vaut " + docbase);
		}	
		loginInfo.setPassword(password);
		sessionManager.setDocbaseName(docbase);
		sessionManager.setIdentity(docbase, loginInfo);
		dctmClientX.setSessionManager(sessionManager);
		String myPwd = loginInfo.getPassword();
		Assert.assertEquals(myPwd, DmInitialize.DM_PWD_OK1);
	}

}
