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
import com.google.enterprise.connector.spi.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmQueryATest extends TestCase {

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
		String user = DmInitialize.DM_LOGIN_OK1;
		String password = DmInitialize.DM_PWD_OK1;
		String docbase = DmInitialize.DM_DOCBASE;
		System.out.println("docbase vaut " + docbase);
		loginInfo.setUser(user);
		loginInfo.setPassword(password);
		sessionManager.setDocbaseName(docbase);
		sessionManager.setIdentity(docbase, loginInfo);
		dctmClientX.setSessionManager(sessionManager);
	}

	public void testSetDQL() {
		IQuery query = dctmClientX.getQuery();
		Assert.assertNotNull(query);
		Assert.assertTrue(query instanceof DmQuery);
		query.setDQL(DmInitialize.DM_QUERY_STRING_ENABLE);
	}

	public void testExecute() throws RepositoryException {
		IQuery query = dctmClientX.getQuery();
		Assert.assertNotNull(query);
		Assert.assertTrue(query instanceof DmQuery);
		query.setDQL(DmInitialize.DM_QUERY_STRING_ENABLE);
		ResultSet resu = query
		.execute(sessionManager, IQuery.DF_READ_QUERY, dctmClientX);
		Assert.assertNotNull(resu);
	}

}
