package com.google.enterprise.connector.dctm;

import junit.framework.TestCase;

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
