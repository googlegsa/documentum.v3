package com.google.enterprise.connector.dctm;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SpiConstants;

import junit.framework.TestCase;

public class DctmMockQueryTraversalManagerTest extends TestCase {

	Session session = null;

	Connector connector = null;

	DctmQueryTraversalManager qtm = null;

	protected void setUp() throws Exception {
		super.setUp();

		qtm = null;
		connector = new DctmConnector();

		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector)
				.setWebtopServerUrl(DmInitialize.DM_WEBTOP_SERVER_URL);
		session = (DctmSession) connector.login();
		qtm = (DctmQueryTraversalManager) session.getQueryTraversalManager();

	}

	public void testStartTraversal() throws RepositoryException {

		ResultSet resultset = null;
		int counter = 0;

		qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_UNBOUNDED);
		resultset = qtm.startTraversal();

		for (Iterator iter = resultset.iterator(); iter.hasNext();) {
			iter.next();
			counter++;
		}

		assertEquals(DmInitialize.DM_RETURN_TOP_UNBOUNDED, counter);

	}

	public void testMakeCheckpointQueryString() {
		String uuid = "doc2";
		String statement = "";

		try {
			statement = qtm.makeCheckpointQueryString(uuid,
					"1970-01-01 01:00:00.020");
		} catch (RepositoryException re) {
			re.printStackTrace();
		}

		assertNotNull(statement);
		assertEquals(DmInitialize.DM_CHECKPOINT_QUERY_STRING, statement);

	}

	public void testExtractDocidFromCheckpoint() {
		String checkPoint = "{\"uuid\":\"doc2\",\"lastModified\":\"1970-01-01 01:00:00.020\"}";
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
		assertEquals(uuid, "doc2");
	}

	public void testExtractNativeDateFromCheckpoint() {

		String checkPoint = "{\"uuid\":\"doc2\",\"lastModified\":\"1970-01-01 01:00:00.020\"}";
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
		assertEquals(modifDate, "1970-01-01 01:00:00.020");

	}

	public void testIDfetchAndVerifyValueForCheckpoint()
			throws RepositoryException {
		DctmSysobjectPropertyMap pm = null;
		pm = new DctmSysobjectPropertyMap("doc2", qtm.getSessionManager(), qtm
				.getClientX());

		String uuid = qtm.fetchAndVerifyValueForCheckpoint(pm,
				SpiConstants.PROPNAME_DOCID).getString();

		assertEquals(uuid, "doc2");
	}

	public void testDatefetchAndVerifyValueForCheckpoint()
			throws RepositoryException, ParseException {
		DctmSysobjectPropertyMap pm = null;
		pm = new DctmSysobjectPropertyMap("doc2", qtm.getSessionManager(), qtm
				.getClientX());
		Calendar calDate = null;

		Calendar c = qtm.fetchAndVerifyValueForCheckpoint(pm,
				SpiConstants.PROPNAME_LASTMODIFY).getDate();

		calDate = DctmSysobjectValue
				.iso8601ToCalendar("1970-01-01 01:00:00.020");
		assertEquals(c.getTimeInMillis(), calDate.getTimeInMillis());
		assertEquals(c, calDate);
	}

	public void testCheckpoint() throws RepositoryException {

		String checkPoint = null;
		DctmSysobjectPropertyMap pm = null;
		pm = new DctmSysobjectPropertyMap("doc2", qtm.getSessionManager(), qtm
				.getClientX());
		checkPoint = qtm.checkpoint(pm);

		assertNotNull(checkPoint);
		assertEquals(
				"{\"uuid\":\"doc2\",\"lastModified\":\"1970-01-01 01:00:00.020\"}",
				checkPoint);
	}

	public void testResumeTraversal() throws RepositoryException {

		session = (DctmSession) connector.login();
		qtm = (DctmQueryTraversalManager) session.getQueryTraversalManager();
		DctmQueryTraversalUtil.runTraversal(qtm, 10000);

		DctmResultSet resultSet = null;

		String checkPoint = "{\"uuid\":\"doc2\",\"lastModified\":\"1969-01-01 01:00:00.010\"}";
		// /query vaut //*[@jcr:primaryType = nt:resource and @jcr:lastModified
		// >= '1970-01-01T01:00:00Z'] order by @jcr:lastModified, @jcr:uuid
		// /buildQueryString checkpoint vaut
		// {"uuid":"doc2","lastModified":"1970-01-01 01:00:00.010"}
		// /buildQueryString query vaut select i_chronicle_id, r_object_id,
		// r_modify_date from dm_sysobject where r_object_type='dm_document' and
		// r_modify_date >= '1970-01-01 01:00:00.010' and i_chronicle_id >
		// 'doc2' order by r_modify_date, i_chronicle_id ENABLE (return_top
		// 10000)

		qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_BOUNDED);
		resultSet = (DctmResultSet) qtm.resumeTraversal(checkPoint);

		assertNotNull(resultSet);

		int counter = 0;
		for (Iterator iter = resultSet.iterator(); iter.hasNext();) {
			iter.next();
			counter++;
		}

		assertEquals(DmInitialize.DM_RETURN_TOP_BOUNDED, counter);
	}

	public void testResumeTraversalWithSimilarDate() throws RepositoryException {
		ResultSet resultSet = null;

		String checkPoint = "{\"uuid\":\"doc2\",\"lastModified\":\"1970-01-01 01:00:00.010\"}";

		qtm.setBatchHint(1);
		resultSet = qtm.resumeTraversal(checkPoint);

		DctmSysobjectIterator iter = (DctmSysobjectIterator) resultSet
				.iterator();
		while (iter.hasNext()) {
			DctmSysobjectPropertyMap map = (DctmSysobjectPropertyMap) iter
					.next();
			String docId = map.getProperty(SpiConstants.PROPNAME_DOCID)
					.getValue().getString();
			String expectedid = "doc10";
			assertEquals(expectedid, docId);
			String modifyDate = DctmSysobjectValue.calendarToIso8601(map
					.getProperty(SpiConstants.PROPNAME_LASTMODIFY).getValue()
					.getDate());
			String expecterModifyDate = "1970-01-01 01:00:00.010";
			assertEquals(expecterModifyDate, modifyDate);
		}
	}

}