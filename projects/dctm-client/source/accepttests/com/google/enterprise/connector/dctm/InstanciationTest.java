package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.TraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

import junit.framework.TestCase;

public class InstanciationTest extends TestCase {

	public void testAcceptanceTests() {
		boolean errorCaught = false;
		Session session = null;
		Connector connector = null;
		TraversalManager qtm = null;
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin("queryUser");
		((DctmConnector) connector).setPassword("p@ssw0rd");
		((DctmConnector) connector).setDocbase("gsadctm");

		((DctmConnector) connector)
				.setClientX("com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		((DctmConnector) connector)
				.setWebtop_display_url("http://swp-vm-wt:8080/webtop/drl/objectId/");

		try {
			session = (DctmSession) connector.login();
			qtm = (DctmTraversalManager) session
					.getTraversalManager();
			qtm.setBatchHint(1);
		} catch (RepositoryLoginException le) {
			errorCaught = true;
			System.out.println("Root Cause : " + le.getCause()
					+ " ; Message : " + le.getMessage());
		} catch (RepositoryException re) {
			errorCaught = true;
			System.out.println("Root Cause : " + re.getCause()
					+ " ; Message : " + re.getMessage());
		}
		assertEquals(errorCaught, false);

		errorCaught = false;
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin("mark");
		((DctmConnector) connector).setPassword("mark");
		((DctmConnector) connector).setDocbase("MockRepositoryEventLog7.txt");

		((DctmConnector) connector)
				.setClientX("com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClient");

		try {
			session = (DctmSession) connector.login();
			qtm = (DctmTraversalManager) session
					.getTraversalManager();
			qtm.setBatchHint(1);
		} catch (RepositoryLoginException le) {
			errorCaught = true;
			System.out.println("Root Cause : " + le.getCause()
					+ " ; Message : " + le.getMessage());
		} catch (RepositoryException re) {
			errorCaught = true;
			System.out.println("Root Cause : " + re.getCause()
					+ " ; Message : " + re.getMessage());
		}
		assertEquals(errorCaught, false);

		errorCaught = false;
		boolean loginExcept = false;
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin("queryUser");
		((DctmConnector) connector).setPassword("passw0rd");// Bad password
		((DctmConnector) connector).setDocbase("gsadctm");
		((DctmConnector) connector)
				.setWebtop_display_url("http://swp-vm-wt:8080/webtop/drl/objectId/");

		((DctmConnector) connector)
				.setClientX("com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");

		try {
			session = (DctmSession) connector.login();
		} catch (RepositoryLoginException le) {
			errorCaught = true;
			loginExcept = true;
			System.out.println("Root Cause : " + le.getCause()
					+ " ; Message : " + le.getMessage());

		} catch (RepositoryException re) {
			System.out.println("Root Cause : " + re.getCause()
					+ " ; Message : " + re.getMessage());
		}
		assertEquals(errorCaught, true);
		assertEquals(loginExcept, true);

		// Eroneous docbaseName DFC instantiation
		errorCaught = false;
		loginExcept = false;
		boolean repExcept = false;
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin("queryUser");
		((DctmConnector) connector).setPassword("p@ssw0rd");
		((DctmConnector) connector).setDocbase("gzadctm");// docbase does not
		// exist
		((DctmConnector) connector)
				.setWebtop_display_url("http://swp-vm-wt:8080/webtop/drl/objectId/");
		((DctmConnector) connector)
				.setClientX("com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");

		try {
			session = (DctmSession) connector.login();
		} catch (RepositoryLoginException le) {
			errorCaught = true;
			loginExcept = true;

		} catch (RepositoryException re) {
			repExcept = true;
			errorCaught = true;
		}
		assertEquals(errorCaught, true);
		assertEquals(loginExcept || repExcept, true);

		errorCaught = false;
		loginExcept = false;
		repExcept = false;
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin("mark");
		((DctmConnector) connector).setPassword("mark");
		((DctmConnector) connector).setDocbase("erhgdwfgzsd");// docbase does
		// not exist
		((DctmConnector) connector)
				.setWebtop_display_url("http://swp-vm-wt:8080/webtop/drl/objectId/");
		((DctmConnector) connector)
				.setClientX("com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClient");

		try {
			session = (DctmSession) connector.login();
		} catch (RepositoryLoginException le) {
			errorCaught = true;
			loginExcept = true;

		} catch (RepositoryException re) {
			repExcept = true;
			errorCaught = true;
		}
		assertEquals(errorCaught, true);
		assertEquals(repExcept, true);

		errorCaught = false;
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin("user1");
		((DctmConnector) connector).setPassword("p@ssword");
		((DctmConnector) connector).setDocbase("gsadctm");
		((DctmConnector) connector)
				.setWebtop_display_url("http://swp-vm-wt:8080/webtop/drl/objectId/");

		((DctmConnector) connector)
				.setClientX("com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");

		try {
			session = (DctmSession) connector.login();
		} catch (RepositoryLoginException le) {

			errorCaught = true;
		} catch (RepositoryException re) {

			errorCaught = true;
		}
		assertEquals(errorCaught, true);

	}

}
