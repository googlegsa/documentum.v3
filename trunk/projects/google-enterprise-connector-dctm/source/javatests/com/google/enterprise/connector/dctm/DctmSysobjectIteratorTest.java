package com.google.enterprise.connector.dctm;

import java.util.Iterator;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SpiConstants;

import junit.framework.TestCase;

public class DctmSysobjectIteratorTest extends TestCase {
	QueryTraversalManager qtm = null;
	private final boolean DFC = true;

	private String user, password, clientX, docbase;
	
	
	public void setUp() throws Exception {
		super.setUp();
		
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
		
		session = (DctmSession) connector.login();
		qtm = (DctmQueryTraversalManager) session
				.getQueryTraversalManager();
		qtm.setBatchHint(2);

		
	}
	
	public void testHasNext() throws RepositoryException {
		int counter = 0;
		ResultSet resultSet = qtm.startTraversal();
		PropertyMap pm = null;
		Property prop = null;
		Iterator iter = resultSet.iterator();
		boolean rep = iter.hasNext();
		assertTrue(rep);
	}
	

	public void testNext() throws RepositoryException {
		int counter = 0;
		ResultSet resultSet = qtm.startTraversal();
		PropertyMap pm = null;
		Property prop = null;
		Property prop1 = null;
		Iterator iter = resultSet.iterator();
		
		while(iter.hasNext()) {
			pm = (PropertyMap) iter.next();
			prop = pm.getProperty(SpiConstants.PROPNAME_DOCID);
			
			assertNotNull(prop);
		
			if (counter == 2) {
				System.out.println("counter == batchhint !!!!");
				
				break;
			}

		}

	}

}
