package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

public class DctmDocumentListTest extends TestCase {
	IClientX dctmClientX = null;

	IClient localClient = null;

	ISessionManager sessionManager = null;

	IQuery query;

	DctmDocumentList documentList;

	public void setUp() throws Exception {
		super.setUp();
		dctmClientX = new DmClientX();

		localClient = dctmClientX.getLocalClient();

		sessionManager = localClient.newSessionManager();

		ILoginInfo loginInfo = dctmClientX.getLoginInfo();
		loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
		loginInfo.setPassword(DmInitialize.DM_PWD_OK1);
		sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);
		sessionManager.setDocbaseName(DmInitialize.DM_DOCBASE);
		query = dctmClientX.getQuery();
		query
				.setDQL("select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where folder('/test_docs/GoogleDemo',descend)");
		ICollection collec = query.execute(sessionManager, IQuery.READ_QUERY);

		documentList = new DctmDocumentList(collec, sessionManager,
				dctmClientX, false, DmInitialize.included_meta,
				DmInitialize.excluded_meta);
	}

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmDocumentList.nextDocument()'
	 */
	public void testNextDocument() throws RepositoryException {

		Document doc = null;
		int counter = 0;
		while ((doc = documentList.nextDocument()) != null) {
			assertTrue(doc instanceof DctmSysobjectDocument);
			counter++;
		}
		assertEquals(6, counter);

	}

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmDocumentList.checkpoint()'
	 */
	public void testCheckpoint() throws RepositoryException {
		while ((documentList.nextDocument()) != null)
			;
		assertEquals(DmInitialize.DM_CHECKPOINT, documentList.checkpoint());
	}

}
