package com.google.enterprise.connector.dctm.dctmmockwrap;

import junit.framework.TestCase;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.spi.RepositoryException;

public class MockMockListTest extends TestCase {

	public void testMockMockList() {
		IClientX dctmClientX = new MockDmClient();
		IClient localClient = null;
		try {
			localClient = dctmClientX.getLocalClient();
		} catch (RepositoryException e) {
			assertTrue(false);
		}
		ISessionManager sessionManager = localClient.newSessionManager();
		ILoginInfo ili = new MockDmLoginInfo();
		ili.setUser("mark");
		ili.setPassword("mark");
		try {
			sessionManager.setIdentity("SwordEventLog.txt", ili);
			sessionManager.getSession("SwordEventLog.txt");
		} catch (RepositoryException e) {
			assertTrue(false);
		}
		String query = "kqsfgopqsudhnfpioqsdf^qsdfhqsdo 'doc26', 'doc2', 'doc3', 'doc4'";
		String[] ids = query.split("', '");
		ids[0] = ids[0].substring(ids[0].lastIndexOf("'") + 1, ids[0].length());
		ids[ids.length - 1] = ids[ids.length - 1].substring(0, ids.length);
		MockMockList lst = null;
		try {
			lst = new MockMockList(ids, sessionManager);
		} catch (Exception e) {
			assertTrue(false);
		}
		assertTrue(lst.iterator().next() instanceof MockRepositoryDocument);
	}

}
