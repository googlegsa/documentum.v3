package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

public class MockDmSessionTest extends TestCase {

	IClientX dctmClientX;
	IClient localClient; 
	ISessionManager sessionManager; 
	ISession sess7; 
	
	public void setUp() throws Exception {
		super.setUp();
		dctmClientX = new MockDmClient();
		localClient = null;
		localClient = dctmClientX.getLocalClient();
		sessionManager = localClient.newSessionManager();
		ILoginInfo ili = new MockDmLoginInfo();
		ili.setUser(DmInitialize.DM_LOGIN_OK4);
		ili.setPassword(DmInitialize.DM_PWD_OK4);
		sessionManager.setIdentity(DmInitialize.DM_DOCBASE, ili);
		sess7 = sessionManager.getSession(DmInitialize.DM_DOCBASE);
	}
	
	
	public void testGetStore() {
		MockRepositoryDocumentStore a = null;
		
		a = ((MockDmSession)sess7).getStore();
		assertNotNull(a);
		MockRepositoryDocument mockDoc=a.getDocByID(DmInitialize.DM_ID1);
		String docID=mockDoc.getDocID();
		assertEquals(docID,DmInitialize.DM_ID1);
	}

	
	public void testGetLoginTicketForUser() {
		String userID="";
		try {
			userID=sess7.getLoginTicketForUser(DmInitialize.DM_LOGIN_OK1);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		assertEquals(userID,DmInitialize.DM_LOGIN_OK1);
	}

	
	public void testGetDocbaseName() {
		String docbase=((MockDmSession)sess7).getDocbaseName();
		assertEquals(docbase,DmInitialize.DM_DOCBASE);
	}

	
	public void testGetObject() {
		IId id = dctmClientX.getId(DmInitialize.DM_ID2);
		ISysObject dctmMockRepositoryDocument=null;
		boolean idString=true;
		try {
			dctmMockRepositoryDocument = sess7.getObject(id);
			idString=((MockDmObject)dctmMockRepositoryDocument).getBoolean("google:ispublic");
			assertEquals(idString,DmInitialize.DM_ID2_IS_PUBLIC);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
	}

}
