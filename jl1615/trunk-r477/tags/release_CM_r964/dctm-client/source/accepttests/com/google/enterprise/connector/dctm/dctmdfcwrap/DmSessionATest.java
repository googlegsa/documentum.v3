package com.google.enterprise.connector.dctm.dctmdfcwrap;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.documentum.fc.common.DfException;
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
		loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
		loginInfo.setPassword(DmInitialize.DM_PWD_OK1);
		sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);
		session = sessionManager.getSession(DmInitialize.DM_DOCBASE);

	}

	public void testGetObject() throws RepositoryException, DfException {
		try {
			IId id = dctmClientX.getId(DmInitialize.DM_ID1);
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

			String ticket = session
					.getLoginTicketForUser(DmInitialize.DM_LOGIN_OK5);

			session = sessionManager.getSession(DmInitialize.DM_DOCBASE);

			ISessionManager sessionManagerUser = dctmClientX.getLocalClient()
					.newSessionManager();
			loginInfo.setUser(DmInitialize.DM_LOGIN_OK5);
			loginInfo.setPassword(DmInitialize.DM_PWD_OK5);
			sessionManagerUser.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);

			Assert.assertNotNull(ticket);
			ILoginInfo loginUser = sessionManagerUser
					.getIdentity(DmInitialize.DM_DOCBASE);
			Assert.assertEquals(DmInitialize.DM_LOGIN_OK5, loginUser.getUser());
			Assert.assertEquals(DmInitialize.DM_PWD_OK5, loginUser
					.getPassword());
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}
	}

}
