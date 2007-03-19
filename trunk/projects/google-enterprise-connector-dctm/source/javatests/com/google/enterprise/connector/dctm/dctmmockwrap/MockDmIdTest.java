package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

import junit.framework.TestCase;

public class MockDmIdTest extends TestCase {

	IClientX dctmClientX;

	IClient localClient;

	ISessionManager sessionManager;

	ISession sess7;

	public void setUp() throws Exception {
		super.setUp();
		dctmClientX = new MockDmClient();
		localClient = null;
		localClient = dctmClientX.getLocalClient();
		sessionManager = localClient.newSessionManager();
		ILoginInfo ili = new MockDmLoginInfo();
		ili.setUser(DmInitialize.DM_LOGIN_OK4);
		ili.setPassword(DmInitialize.DM_PWD_OK4);
		sessionManager.setIdentity(DmInitialize.DM_DOCBASE, ili);
		sess7 = sessionManager.getSession(DmInitialize.DM_DOCBASE);
	}

	public void testToString() {
		IId id = dctmClientX.getId(DmInitialize.DM_ID2);
		String idString = id.toString();
		assertEquals(idString, DmInitialize.DM_ID2);
	}

}
