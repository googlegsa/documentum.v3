package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.DebugFinalData;
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

public class DmSessionManagerTest extends TestCase {

	ISessionManager sessionManager;

	ILoginInfo loginInfo;

	String user = DmInitialize.DM_LOGIN_OK4;

	String password = DmInitialize.DM_PWD_OK4;

	String docbase = DmInitialize.DM_DOCBASE;

	private String userKO = DmInitialize.DM_LOGIN_KO;

	private String pwdKO = DmInitialize.DM_PWD_KO;

	//public void setUp() throws Exception {
	public void setUp(){
		try {
			super.setUp();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		IClientX dctmClientX;

		IClient localClient=null;

		dctmClientX = new DmClientX();
		try {
			localClient = dctmClientX.getLocalClient();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		sessionManager = localClient.newSessionManager();
		loginInfo = dctmClientX.getLoginInfo();
		loginInfo.setUser(user);
		if (DebugFinalData.debugInEclipse) {
			System.out.println("setUser = "+user);
		}
		
		loginInfo.setPassword(password);
		
		if (DebugFinalData.debugInEclipse) {
			System.out.println("setPassword = "+password);
		}
		try {
			sessionManager.setIdentity(docbase, loginInfo);
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	public void testNewSession() throws LoginException, RepositoryException {

		ISession session = null;
		try {
			session = sessionManager.newSession(docbase);
			Assert.assertNotNull(session);
			Assert.assertTrue(session instanceof DmSession);
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}
	}

//	public void testAuthenticate() throws LoginException {
	public void testAuthenticateOK(){ 
		boolean rep = false;
		
		rep = sessionManager.authenticate(docbase);
		
		Assert.assertTrue(rep);

		sessionManager.clearIdentity(docbase);
		loginInfo.setUser(DmInitialize.DM_LOGIN_OK2);
		loginInfo.setPassword(DmInitialize.DM_PWD_OK2);
		try {
			sessionManager.setIdentity(docbase, loginInfo);
			rep = sessionManager.authenticate(docbase);
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		if (DebugFinalData.debugInEclipse) {
			System.out.println("rep de testAuthenticateOK vaut "+rep);
		}
		Assert.assertTrue(rep);
	}
	
	//public void testAuthenticate() throws LoginException {
	
	public void testAuthenticateKO(){ 
		boolean rep = false;
		
		rep = sessionManager.authenticate(docbase);
		
		Assert.assertTrue(rep);

		sessionManager.clearIdentity(docbase);
		if (DebugFinalData.debugInEclipse) {
			System.out.println("après clearIdentity");
		}	
		loginInfo.setUser(userKO);
		if (DebugFinalData.debugInEclipse) {
			System.out.println("après setUser");
		}	
		loginInfo.setPassword(pwdKO);
		if (DebugFinalData.debugInEclipse) {
			System.out.println("après setPassword");
		}	
		try {
			sessionManager.setIdentity(docbase, loginInfo);
			if (DebugFinalData.debugInEclipse) {
				System.out.println("après setIdentity");
			}	
		}catch (LoginException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("catch LoginException");
		}
		
		rep = sessionManager.authenticate(docbase);
		if (DebugFinalData.debugInEclipse) {
			System.out.println("après authenticate");
			System.out.println("rep de testAuthenticateKO vaut "+rep);
		}	
		Assert.assertFalse(rep);
	}
	
	
	//public void testClearIdentity() throws LoginException {
	public void testClearIdentity(){
		sessionManager.clearIdentity(docbase);
		ILoginInfo logInfo = sessionManager.getIdentity(docbase);
		Assert.assertNull(((DmLoginInfo) logInfo).getIdfLoginInfo());
	}

}
