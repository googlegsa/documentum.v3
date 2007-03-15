package com.google.enterprise.connector.dctm;

import java.util.Iterator;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
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
	
	DctmSession dctmSession = null;

	DctmConnector connector = null;

	private final boolean DFC = false;

	private String user, password, clientX, docbase;

	public void setUp() throws Exception {
		super.setUp();
		
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector)
				.setWebtopServerUrl(DmInitialize.DM_WEBTOP_SERVER_URL);
		dctmSession = (DctmSession) connector.login();
		
		qtm = (DctmQueryTraversalManager) dctmSession.getQueryTraversalManager();
		qtm.setBatchHint(2);

	}

	public void testHasNext() throws RepositoryException {

		ResultSet resultSet = qtm.startTraversal();
		Iterator iter = resultSet.iterator();
		boolean rep = iter.hasNext();
		assertTrue(rep);
	}

	public void testNext() throws RepositoryException {
		int counter = 0;
		ResultSet resultSet = qtm.startTraversal();
		PropertyMap pm = null;
		Property prop = null;
		Iterator iter = resultSet.iterator();

		while (iter.hasNext()) {
			pm = (PropertyMap) iter.next();
			prop = pm.getProperty(SpiConstants.PROPNAME_DOCID);

			assertNotNull(prop);

			if (counter == 2) {

				break;
			}

		}

	}
}