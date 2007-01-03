package com.google.enterprise.connector.dctm;

import junit.framework.TestCase;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

//import com.google.enterprise.connector.test.QueryTraversalUtil;

public class DctmQueryTraversalUtilCall extends TestCase {

	public void testTraversal() {

		Session session = null;
		Connector connector = null;
		QueryTraversalManager qtm = null;

		connector = new DctmConnector();

		((DctmConnector) connector).setLogin("user1");
		((DctmConnector) connector).setPassword("p@ssw0rd");
		
		IClient cl = null;
		try {
			cl = (IClient) Class.forName("com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmClient").newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((DctmConnector) connector).setClient(cl);
		try {
			session = (DctmSession) connector.login();
			qtm = (DctmQueryTraversalManager) session.getQueryTraversalManager();
			DctmQueryTraversalUtil.runTraversal(qtm, 20000);

		} catch (LoginException le) {
			le.getMessage();
		} catch (RepositoryException re) {
			re.getMessage();
		}

	}
}
