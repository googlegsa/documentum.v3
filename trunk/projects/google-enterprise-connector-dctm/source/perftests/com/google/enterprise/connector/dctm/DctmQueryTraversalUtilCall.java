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
		System.out.println("avant setClient");
		((DctmConnector) connector).setLogin(user);
		((DctmConnector) connector).setPassword(password);
		((DctmConnector) connector).setDocbase(docbase);
		((DctmConnector) connector)
				.setQueryStringUnboundedDefault("select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document' "
						+ "order by r_modify_date, i_chronicle_id ");
		((DctmConnector) connector)
				.setWebtopServerUrl("http://swp-vm-wt:8080/webtop/drl/objectId/");
		((DctmConnector) connector)
				.setQueryStringBoundedDefault("select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type=''dm_document'' and r_modify_date >= "
						+ "''{0}'' " + "order by r_modify_date, i_chronicle_id");
		((DctmConnector) connector).setClientX(clientX);
		System.out.println("apres setClient");

		/**
		 * End simulation
		 */

		try {
			session = (DctmSession) connector.login();
			qtm = (DctmQueryTraversalManager) session
					.getQueryTraversalManager();
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
