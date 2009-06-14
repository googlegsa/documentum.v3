package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmFormatTest extends TestCase {

	IFormat dctmForm;

	public void setUp() throws Exception {
		super.setUp();
		IClientX dctmClientX = new DmClientX();

		IClient localClient = dctmClientX.getLocalClient();

		ISessionManager sessionManager = localClient.newSessionManager();

		ISession session = null;

		ILoginInfo loginInfo = dctmClientX.getLoginInfo();
		loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
		loginInfo.setPassword(DmInitialize.DM_PWD_OK1);
		sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);
		try {
			session = sessionManager.newSession(DmInitialize.DM_DOCBASE);
			IId id = dctmClientX.getId(DmInitialize.DM_ID1);
			ISysObject object = session.getObject(id);
			dctmForm = (DmFormat) object.getFormat();
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}

	}

	public void testCanIndex() throws DfException, RepositoryException {

		Assert.assertNotNull(dctmForm);

		boolean rep = dctmForm.canIndex();

		Assert.assertTrue(rep);
	}

	public void testGetMIMEType() throws DfException, RepositoryException {
		String mimetype = dctmForm.getMIMEType();
		Assert.assertEquals(mimetype, "application/msword");
	}

}