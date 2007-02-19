package com.google.enterprise.connector.dctm;

import junit.framework.TestCase;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

public class DctmQueryTraversalUtilCall extends TestCase {

	private final boolean DFC = true;

	private String user, password, clientX, docbase;

	public void testTraversal() {
		if (DFC) {
			user = "queryUser";
			password = "p@ssw0rd";
			clientX = "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX";
			docbase = "gsadctm";
		} else {
			user = "mark";
			password = "mark";
			clientX = "com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClientX";
			docbase = "MockRepositoryEventLog7.txt";
		}

		Session session = null;
		Connector connector = null;
		QueryTraversalManager qtm = null;

		connector = new DctmConnector();

		/**
		 * Simulation of the setters used by Instance.xml
		 */
		((DctmConnector) connector).setLogin(user);
		((DctmConnector) connector).setPassword(password);
		((DctmConnector) connector).setDocbase(docbase);
		((DctmConnector) connector)
				.setWebtopServerUrl("http://swp-vm-wt:8080/webtop/drl/objectId/");
		((DctmConnector) connector).setClientX(clientX);

		/**
		 * End simulation
		 */

		try {
			session = (DctmSession) connector.login();
			qtm = (DctmQueryTraversalManager) session
					.getQueryTraversalManager();
			qtm.setBatchHint(50);
			DctmQueryTraversalUtil.runTraversal(qtm, 10000);

		} catch (LoginException le) {
			System.out.println("Root Cause : " + le.getCause()
					+ " ; Message : " + le.getMessage());
		} catch (RepositoryException re) {
			System.out.println("Root Cause : " + re.getCause()
					+ " ; Message : " + re.getMessage());
		}

	}
}
