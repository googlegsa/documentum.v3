package com.google.enterprise.connector.dctm;

import java.util.Iterator;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

public class DctmMockSysobjectPropertyMapTest extends TestCase {

	IClientX dctmClientX = null;

	IClient localClient = null;

	ISessionManager sessionManager = null;

	public void setUp() throws Exception {
		super.setUp();

		dctmClientX = new MockDmClient();

		localClient = dctmClientX.getLocalClient();

		sessionManager = localClient.newSessionManager();

		ISession session = null;

		ILoginInfo loginInfo = dctmClientX.getLoginInfo();
		loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
		loginInfo.setPassword(DmInitialize.DM_PWD_OK1);
		sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);
		sessionManager.setDocbaseName(DmInitialize.DM_DOCBASE);
		try {
			session = sessionManager.newSession(DmInitialize.DM_DOCBASE);
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}

	}

	public void testGetProperties() throws RepositoryException {

		DctmSysobjectPropertyMap dctmSpm = new DctmSysobjectPropertyMap(
				DmInitialize.DM_ID1, sessionManager, dctmClientX, "false");

		Iterator iterator = dctmSpm.getProperties();
		int counter = 0;
		while (iterator.hasNext()) {
			iterator.next();
			counter++;
		}
		assertEquals(3, counter);
	}

	public void testGetProperty() throws RepositoryException {
		DctmSysobjectPropertyMap dctmSpm = new DctmSysobjectPropertyMap(
				DmInitialize.DM_ID1, sessionManager, dctmClientX,"false");
		Property property = dctmSpm.getProperty("google:docid");
		assertTrue(property instanceof DctmSysobjectProperty);
		assertEquals("google:docid", property.getName());
		assertEquals(DmInitialize.DM_ID1, property.getValue().getString());
	}

}
