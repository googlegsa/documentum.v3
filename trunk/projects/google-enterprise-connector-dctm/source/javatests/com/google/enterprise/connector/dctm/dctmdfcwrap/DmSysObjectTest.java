package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;

import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmSysObjectTest extends TestCase {

	ISysObject object;

	ISession session = null;

	ISessionManager sessionManager;

	public void setUp() throws Exception {
		super.setUp();
		IClientX dctmClientX;
		IClient localClient;

		ILoginInfo loginInfo;
		dctmClientX = new DmClientX();
		localClient = dctmClientX.getLocalClient();
		sessionManager = localClient.newSessionManager();
		loginInfo = dctmClientX.getLoginInfo();
		loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
		loginInfo.setPassword(DmInitialize.DM_PWD_OK1);
		sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);

		session = sessionManager.getSession(DmInitialize.DM_DOCBASE);
		object = session.getObject(dctmClientX.getId(DmInitialize.DM_ID1));

	}

	public void testGetFormat() throws RepositoryException {
		try {
			IFormat format = object.getFormat();
			Assert.assertNotNull(format);
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}
	}

	public void testGetContentSize() throws RepositoryException {
		try {
			long size = object.getContentSize();
			assertTrue(size > 0);
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}

	}

}
