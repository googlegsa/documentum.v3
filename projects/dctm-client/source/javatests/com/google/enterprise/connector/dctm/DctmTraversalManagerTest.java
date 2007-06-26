package com.google.enterprise.connector.dctm;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.PropertyMapList;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SpiConstants;

import junit.framework.TestCase;

public class DctmTraversalManagerTest extends TestCase {

	Session session = null;

	Connector connector = null;

	DctmTraversalManager qtm = null;

	protected void setUp() throws Exception {
		super.setUp();
		qtm = null;
		Connector connector = new DctmConnector();
		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector)
				.setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
		((DctmConnector) connector).setIs_public("false");
		Session sess = (DctmSession) connector.login();
		qtm = (DctmTraversalManager) sess.getTraversalManager();
	}

	public void testStartTraversal() throws RepositoryException {
		PropertyMapList list = null;
		int counter = 0;

		qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_UNBOUNDED);
		list = qtm.startTraversal();
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			iter.next();
			counter++;
		}
		assertEquals(DmInitialize.DM_RETURN_TOP_UNBOUNDED, counter);
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
		pm = new DctmSysobjectPropertyMap("0900000180000523", qtm
				.getSessionManager(), qtm.getClientX(), qtm.isPublic() ? "true"
				: "false", DmInitialize.included_meta, DmInitialize.excluded_meta);

		String uuid = qtm.fetchAndVerifyValueForCheckpoint(pm,
				SpiConstants.PROPNAME_DOCID).getString();

		assertEquals(uuid, "0900000180000523");
	}

	public void testDatefetchAndVerifyValueForCheckpoint()
			throws RepositoryException, ParseException {
		DctmSysobjectPropertyMap pm = null;
		pm = new DctmSysobjectPropertyMap("0900000180000523", qtm
				.getSessionManager(), qtm.getClientX(), qtm.isPublic() ? "true"
				: "false", DmInitialize.included_meta, DmInitialize.excluded_meta);
		Calendar calDate = null;

		Calendar c = qtm.fetchAndVerifyValueForCheckpoint(pm,
				SpiConstants.PROPNAME_LASTMODIFIED).getDate();
		calDate = DctmSysobjectValue.iso8601ToCalendar("2006-12-14 22:35:58");
		assertEquals(c.getTimeInMillis(), calDate.getTimeInMillis());
		assertEquals(c, calDate);
	}

	public void testCheckpoint() throws RepositoryException {

		String checkPoint = null;
		DctmSysobjectPropertyMap pm = null;
		pm = new DctmSysobjectPropertyMap("0900000180000523", qtm
				.getSessionManager(), qtm.getClientX(), qtm.isPublic() ? "true"
				: "false", DmInitialize.included_meta, DmInitialize.excluded_meta);
		checkPoint = qtm.checkpoint(pm);

		assertNotNull(checkPoint);
		assertEquals(
				"{\"uuid\":\"0900000180000523\",\"lastModified\":\"2006-12-14 22:35:58.000\"}",
				checkPoint);
	}

	public void testResumeTraversal() throws RepositoryException {
		PropertyMapList propertyMapList = null;

		String checkPoint = "{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02 14:19:29.000\"}";

		qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_BOUNDED);
		propertyMapList = qtm.resumeTraversal(checkPoint);

		int counter = 0;

		for (Iterator iter = propertyMapList.iterator(); iter.hasNext();) {
			iter.next();
			counter++;
		}

		assertEquals(DmInitialize.DM_RETURN_TOP_BOUNDED, counter);
	}

	public void testResumeTraversalWithSimilarDate() throws RepositoryException {
		PropertyMapList propertyMapList = null;

		String checkPoint = "{\"uuid\":\"090000018000015d\",\"lastModified\":\"2006-12-14 20:09:13.000\"}";

		qtm.setBatchHint(1);
		propertyMapList = qtm.resumeTraversal(checkPoint);

		DctmSysobjectIterator iter = (DctmSysobjectIterator) propertyMapList
				.iterator();
		while (iter.hasNext()) {
			DctmSysobjectPropertyMap map = (DctmSysobjectPropertyMap) iter
					.next();
			String docId = map.getProperty(SpiConstants.PROPNAME_DOCID)
					.getValue().getString();
			String expectedid = "090000018000015e";
			assertEquals(expectedid, docId);
			String modifyDate = DctmSysobjectValue.calendarToIso8601(map
					.getProperty(SpiConstants.PROPNAME_LASTMODIFIED).getValue()
					.getDate());
			String expecterModifyDate = "2006-12-14 20:09:13.000";
			assertEquals(expecterModifyDate, modifyDate);
		}
	}

}
