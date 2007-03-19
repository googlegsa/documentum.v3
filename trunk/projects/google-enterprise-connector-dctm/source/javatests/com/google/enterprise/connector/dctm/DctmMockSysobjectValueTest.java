package com.google.enterprise.connector.dctm;

import java.io.InputStream;
import java.util.Calendar;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ValueType;

import junit.framework.TestCase;

public class DctmMockSysobjectValueTest extends TestCase {

	IClientX dctmClientX = null;

	IClient localClient = null;

	ISessionManager sessionManager = null;

	ISysObject object = null;

	public void setUp() throws Exception {
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
			session = sessionManager.getSession(DmInitialize.DM_DOCBASE);
			IId id = dctmClientX.getId(DmInitialize.DM_ID1);
			object = session.getObject(id);
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}

	}

	public void testGetString() throws IllegalArgumentException,
			RepositoryException {
		DctmSysobjectValue dspm = new DctmSysobjectValue(object,
				"google:docid", ValueType.STRING);
		assertEquals(DmInitialize.DM_ID1, dspm.getString());
	}

	public void testGetStream() throws IllegalArgumentException,
			IllegalStateException, RepositoryException {

		InputStream is = null;
		DctmSysobjectValue dspm = new DctmSysobjectValue(object, "",
				ValueType.BINARY);

		is = dspm.getStream();
		assertNotNull(is);
		assertTrue(is instanceof InputStream);

	}

	public void testCalendarToIso8601() {
		Calendar c = Calendar.getInstance();
		c.set(2007, Calendar.JANUARY, 28, 14, 11, 0);
		String milliseconds = c.get(Calendar.MILLISECOND) + "";
		if (milliseconds.length() == 2) {
			milliseconds = "0" + milliseconds;
		}
		String expectedDate = "2007-01-28 14:11:00." + milliseconds;
		String receivedDate = DctmSysobjectValue.calendarToIso8601(c);
		assertEquals(expectedDate, receivedDate);
	}

}
