package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmQueryATest extends TestCase {

	IClientX dctmClientX;

	public void setUp() throws Exception {
		super.setUp();
		dctmClientX = new DmClientX();

	}

	public void testSetDQL() {
		IQuery query = dctmClientX.getQuery();
		Assert.assertNotNull(query);
		Assert.assertTrue(query instanceof DmQuery);
		query.setDQL(DmInitialize.DM_QUERY_STRING_ENABLE);
	}

	public void testExecute() throws RepositoryException {
		IClient localClient = dctmClientX.getLocalClient();

		ILoginInfo loginInfo = dctmClientX.getLoginInfo();
		ISessionManager sessionManager = localClient.newSessionManager();
		ISession session = null;
		try {

			loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
			loginInfo.setPassword(DmInitialize.DM_PWD_OK1);

			sessionManager.setDocbaseName(DmInitialize.DM_DOCBASE);
			sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);

			dctmClientX.setSessionManager(sessionManager);
			IQuery query = dctmClientX.getQuery();
			Assert.assertNotNull(query);
			Assert.assertTrue(query instanceof DmQuery);
			query.setDQL(DmInitialize.DM_QUERY_STRING_ENABLE);
			ICollection collec = query.execute(sessionManager,
					IQuery.READ_QUERY);
			Assert.assertNotNull(collec);
			session = sessionManager.getSession(DmInitialize.DM_DOCBASE);
		} finally {
			if (session != null)
				sessionManager.release(session);
		}
	}

}
