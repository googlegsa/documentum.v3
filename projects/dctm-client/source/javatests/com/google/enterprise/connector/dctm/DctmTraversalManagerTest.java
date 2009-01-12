package com.google.enterprise.connector.dctm;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
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
		((DctmConnector) connector)
				.setIncluded_object_type(DmInitialize.included_object_type);
		((DctmConnector) connector)
				.setRoot_object_type(DmInitialize.root_object_type);
		Session sess = (DctmSession) connector.login();
		qtm = (DctmTraversalManager) sess.getTraversalManager();
	}

	public void testStartTraversal() throws RepositoryException {
		DocumentList list = null;
		int counter = 0;

		qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_UNBOUNDED);
		list = qtm.startTraversal();

		while (list.nextDocument() != null) {

			counter++;
		}
		assertEquals(DmInitialize.DM_RETURN_TOP_UNBOUNDED, counter);
	}

	public void testMakeCheckpointQueryString() {
		String uuid = "090000018000e100";
		String statement = "";
		
		statement = qtm.makeCheckpointQueryString(uuid,
					"2007-01-02 13:58:10");
		

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

	public void testResumeTraversal() throws RepositoryException {
		DocumentList propertyMapList = null;

		String checkPoint = DmInitialize.DM_CHECKPOINT;

		qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_BOUNDED);
		propertyMapList = qtm.resumeTraversal(checkPoint);

		int counter = 0;

		while (propertyMapList.nextDocument() != null) {

			counter++;
		}

		assertEquals(DmInitialize.DM_RETURN_TOP_BOUNDED, counter);
	}

	public void testResumeTraversalWithSimilarDate() throws RepositoryException {
		DocumentList documentList = null;

		String checkPoint = "{\"uuid\":\"090000018000015d\",\"lastModified\":\"2006-12-14 20:09:13\"}";

		qtm.setBatchHint(1);
		documentList = qtm.resumeTraversal(checkPoint);

		DctmSysobjectDocument map = null;
		while ((map = (DctmSysobjectDocument) documentList.nextDocument()) != null) {

			String docId = map.findProperty(SpiConstants.PROPNAME_DOCID)
					.nextValue().toString();
			String expectedid = "090000018000015e";
			assertEquals(expectedid, docId);
			DctmDateValue date = (DctmDateValue) map.findProperty(
					SpiConstants.PROPNAME_LASTMODIFIED).nextValue();
			String modifyDate = date.toDctmFormat();
			String expecterModifyDate = "2006-12-14 20:09:13";
			assertEquals(expecterModifyDate, modifyDate);
		}
	}

}
