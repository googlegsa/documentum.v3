package com.google.enterprise.connector.dctm;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SpiConstants;

import junit.framework.TestCase;

public class DctmQueryTraversalManagerTest extends TestCase {

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmQueryTraversalManager.startTraversal()'
	 */

	Session session = null;

	Connector connector = null;

	DctmQueryTraversalManager qtm = null;

	protected void setUp() throws Exception {
		super.setUp();

		qtm = null;
		Connector connector = new DctmConnector();

		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector)
				.setWebtopServerUrl(DmInitialize.DM_WEBTOP_SERVER_URL);
		Session sess = (DctmSession) connector.login();
		qtm = (DctmQueryTraversalManager) sess.getQueryTraversalManager();

	}

	public void testMakeCheckpointQueryString() {
		String uuid = "090000018000e100";
		String statement = "";
		try {
			statement = qtm.makeCheckpointQueryString(uuid,
					"2007-01-02 13:58:10");
		} catch (RepositoryException re) {
			re.printStackTrace();
		}

		assertNotNull(statement);
		assertEquals(DmInitialize.DM_CHECKPOINT_QUERY_STRING, statement);

	}

	public void testExtractDocidFromCheckpoint() {
		String checkPoint = "{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02 13:58:10\"}";
		String uuid = null;
		JSONObject jo = null;

		try {
			jo = new JSONObject(checkPoint);
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"checkPoint string does not parse as JSON: " + checkPoint);
		}

		uuid = qtm.extractDocidFromCheckpoint(jo, checkPoint);
		assertNotNull(uuid);
		assertEquals(uuid, "090000018000e100");
	}

	public void testExtractNativeDateFromCheckpoint() {

		String checkPoint = "{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02 13:58:10\"}";
		JSONObject jo = null;
		String modifDate = null;

		try {
			jo = new JSONObject(checkPoint);
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"checkPoint string does not parse as JSON: " + checkPoint);
		}

		modifDate = qtm.extractNativeDateFromCheckpoint(jo, checkPoint);
		assertNotNull(modifDate);
		assertEquals(modifDate, "2007-01-02 13:58:10");

	}

	public void testIDfetchAndVerifyValueForCheckpoint()
			throws RepositoryException {
		DctmSysobjectPropertyMap pm = null;
		pm = new DctmSysobjectPropertyMap("0900000180010b17", qtm
				.getSessionManager(), qtm.getClientX());

		String uuid = qtm.fetchAndVerifyValueForCheckpoint(pm,
				SpiConstants.PROPNAME_DOCID).getString();

		assertEquals(uuid, "0900000180010b17");
	}

	public void testDatefetchAndVerifyValueForCheckpoint()
			throws RepositoryException, ParseException {
		DctmSysobjectPropertyMap pm = null;
		pm = new DctmSysobjectPropertyMap("0900000180010b17", qtm
				.getSessionManager(), qtm.getClientX());
		Calendar calDate = null;

		Calendar c = qtm.fetchAndVerifyValueForCheckpoint(pm,
				SpiConstants.PROPNAME_LASTMODIFY).getDate();

		calDate = DctmSysobjectValue.iso8601ToCalendar("2007-01-02 14:19:29");
		assertEquals(c.getTimeInMillis(), calDate.getTimeInMillis());
		assertEquals(c, calDate);
	}

	public void testCheckpoint() throws RepositoryException {

		String checkPoint = null;
		DctmSysobjectPropertyMap pm = null;
		pm = new DctmSysobjectPropertyMap("0900000180010b17", qtm
				.getSessionManager(), qtm.getClientX());
		checkPoint = qtm.checkpoint(pm);

		assertNotNull(checkPoint);
		assertEquals(
				"{\"uuid\":\"0900000180010b17\",\"lastModified\":\"02/01/2007 14:19:29\"}",
				checkPoint);
	}

	public void testStartTraversal() throws RepositoryException {

		ResultSet resultset = null;
		int counter = 0;

		qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_UNBOUNDED);
		resultset = qtm.startTraversal();
		Iterator iter = resultset.iterator();
		while (iter.hasNext()) {
			iter.next();
			counter++;
		}
		assertEquals(DmInitialize.DM_RETURN_TOP_UNBOUNDED, counter);

	}

	public void testResumeTraversal() throws RepositoryException {
		ResultSet resultSet = null;

		String checkPoint = "{\"uuid\":\"090000018000e100\",\"lastModified\":\"02/01/2007 13:00:00\"}";

		qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_BOUNDED);
		resultSet = qtm.resumeTraversal(checkPoint);

		int counter = 0;

		for (Iterator iter = resultSet.iterator(); iter.hasNext();) {
			iter.next();
			counter++;
		}

		assertEquals(DmInitialize.DM_RETURN_TOP_BOUNDED, counter);
	}

}
