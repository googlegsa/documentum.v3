package com.google.enterprise.connector.dctm.dctmdfcwrap;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.DebugFinalData;
import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmSessionATest extends TestCase {

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

	public void testGetObject() throws RepositoryException, DfException {
		try {
			String user = DmInitialize.DM_LOGIN_OK1;
			String password = DmInitialize.DM_PWD_OK1;
			String docbase = DmInitialize.DM_DOCBASE;
			loginInfo.setUser(user);
			loginInfo.setPassword(password);
			sessionManager.setIdentity(docbase, loginInfo);
			session = sessionManager.getSession(docbase);
			Assert.assertNotNull(session);
			Assert.assertTrue(session instanceof DmSession);
			String idString = DmInitialize.DM_ID1;
			if (DebugFinalData.debugInEclipse) {
				System.out.println("idString " + idString);
			}	
			IId id = dctmClientX.getId(idString);
			ISysObject object = session.getObject(id);
			Assert.assertNotNull(object);
			Assert.assertTrue(object instanceof DmSysObject);
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}
	}

	public void testGetLoginTicketForUser() throws RepositoryException {
		try {
			String userAdmin = DmInitialize.DM_LOGIN_OK1;
			String passwordAdmin = DmInitialize.DM_PWD_OK1;
			String docbase = DmInitialize.DM_DOCBASE;
			loginInfo.setUser(userAdmin);
			loginInfo.setPassword(passwordAdmin);
			sessionManager.setIdentity(docbase, loginInfo);
			session = sessionManager.getSession(docbase);
			String ticket = session
					.getLoginTicketForUser(DmInitialize.DM_LOGIN_OK5);
	
			session = sessionManager.getSession(docbase);
	
			ISessionManager sessionManagerUser = dctmClientX.getLocalClient()
					.newSessionManager();
			loginInfo.setUser(DmInitialize.DM_LOGIN_OK5);
			loginInfo.setPassword(DmInitialize.DM_PWD_OK5);
			sessionManagerUser.setIdentity(docbase, loginInfo);
	
			Assert.assertNotNull(session);
			Assert.assertTrue(session instanceof DmSession);
			if (DebugFinalData.debugInEclipse) {
				System.out.println("ticket vaut " + ticket);
			}	
			Assert.assertNotNull(ticket);
			ILoginInfo loginUser = sessionManagerUser.getIdentity(docbase);
			String myUser = loginUser.getUser();
			Assert.assertEquals(myUser, DmInitialize.DM_LOGIN_OK5);
			String myPassword = loginUser.getPassword();
			Assert.assertEquals(myPassword, DmInitialize.DM_PWD_OK5);
		}finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}	
	}

}
