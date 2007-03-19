package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

import junit.framework.TestCase;

public class MockDmLoginInfoTest extends TestCase {

	IClientX dctmClientX;

	IClient localClient;

	ISessionManager sessionManager;

	ISession sess7;

	ILoginInfo ili;

	public void setUp() throws Exception {
		super.setUp();
		dctmClientX = new MockDmClient();
		localClient = null;
		localClient = dctmClientX.getLocalClient();
		sessionManager = localClient.newSessionManager();

		ili = new MockDmLoginInfo();
	}

	public void testSetGetUser() {
		ili.setUser(DmInitialize.DM_LOGIN_OK4);
		String user = ili.getUser();
		assertEquals(user, DmInitialize.DM_LOGIN_OK4);
	}

	public void testSetGetPassword() {
		ili.setPassword(DmInitialize.DM_PWD_OK4);
		String pwd = ili.getPassword();
		assertEquals(pwd, DmInitialize.DM_PWD_OK4);
	}

}
