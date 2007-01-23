package com.google.enterprise.connector.dctm;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

public class DctmSysobjectValueTest extends TestCase {
	
	public void testTraversal() throws LoginException, RepositoryException {
		String user = "queryUser";
		String password="p@ssw0rd";
		String client="com.google.enterprise.connector.dctm.dctmdfcwrap.DmClient";
		String docbase="gsadctm";
		
		Connector connector = new DctmConnector();
		((DctmConnector) connector).setLogin(user);
		((DctmConnector) connector).setPassword(password);
		((DctmConnector) connector).setDocbase(docbase);		
		((DctmConnector) connector).setClient(client);
		
		DctmSession session = (DctmSession) connector.login();
		Assert.assertNotNull(session);
		
		IClient iclient = session.getClient();
		ISessionManager sessionManager = iclient.getSessionManager();
		ISession isession = null;
		try {
			isession = sessionManager.getSession(docbase);
			
		} 
		finally {
			sessionManager.release(isession);
		}
		
		
	}
	
}
