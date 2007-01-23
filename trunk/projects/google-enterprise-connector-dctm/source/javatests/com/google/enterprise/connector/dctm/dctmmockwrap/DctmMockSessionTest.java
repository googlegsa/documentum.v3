package com.google.enterprise.connector.dctm.dctmmockwrap;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.RepositoryException;

public class DctmMockSessionTest extends TestCase {
	
	/*
	 * Test method for 'com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSession.getObject(IId)'
	 */
	public void testGetObject() throws RepositoryException {
		IClientX clientX = new DctmMockClient();
		IClient localClient = clientX.getLocalClient();
		
		ISessionManager sessionManager = localClient.newSessionManager();
		
		String user="queryUser";
		String password="p@ssw0rd";
		String docbase="gsadctm";
		
		ILoginInfo loginInfo = localClient.getLoginInfo();
		loginInfo.setUser(user);
		loginInfo.setPassword(password);
		
		sessionManager.setIdentity(docbase, loginInfo);
		
		ISession session = null;
		try {
			session = sessionManager.getSession(docbase);
			Assert.assertNotNull(session);
			Assert.assertTrue(session instanceof DctmMockSession);	
			String idString = getAnExistingObjectId(session);
			System.out.println("idString " + idString);
			IId id = clientX.getId(idString);
			ISysObject object = session.getObject(id);
			Assert.assertNotNull(object);
			Assert.assertTrue(object instanceof DctmMockRepositoryDocument);
			
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}	
	}
	
	private String getAnExistingObjectId(ISession session) {
		// we'll hardcode this...
		String idString = "doc1";
		return idString;
	}	
}
