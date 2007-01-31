package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmSysObjectTest extends TestCase {

	IClientX dctmClientX;
	IClient localClient;
	ISessionManager sessionManager; 
	ISession session;
	ILoginInfo loginInfo;
	
	public void setUp() throws Exception{
		super.setUp();
		dctmClientX = new DmClientX();
		localClient = dctmClientX.getLocalClient();
		sessionManager = localClient.newSessionManager();
		loginInfo = dctmClientX.getLoginInfo();
		String user=DmInitialize.DM_LOGIN_OK1;
		String password=DmInitialize.DM_PWD_OK1;
		String docbase=DmInitialize.DM_DOCBASE;
		loginInfo.setUser(user);
		loginInfo.setPassword(password);
		sessionManager.setIdentity(docbase, loginInfo);
		session = sessionManager.getSession(docbase);
		Assert.assertNotNull(session);
		Assert.assertTrue(session instanceof DmSession);	
	}
	
	public void testGetFormat() {

	}

	
	public void testGetContentSize() {

	}

	public void testGetContent() {

	}

	
	public void testEnumAttrs() {

	}

	
	public void testGetACLDomain() {

	}

	
	public void testGetACLName() {

	}

	public void testGetString() {

	}

	
	public void testGetBoolean() {

	}

	
	public void testGetDouble() {

	}

	public void testGetId() {

	}

	
	public void testGetInt() {

	}

	public void testGetTime() {

	}

}
