package com.google.enterprise.connector.dctm;

import java.util.Iterator;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.PropertyMapList;
import com.google.enterprise.connector.spi.TraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;

import junit.framework.TestCase;

public class DctmMockSysobjectIteratorTest extends TestCase {

	TraversalManager qtm = null;

	DctmSession dctmSession = null;

	DctmConnector connector = null;

	public void setUp() throws Exception {
		super.setUp();

		connector = new DctmConnector();
		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector)
				.setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
		((DctmConnector) connector).setIs_public("false");
		dctmSession = (DctmSession) connector.login();

		qtm = (DctmTraversalManager) dctmSession.getTraversalManager();
		qtm.setBatchHint(2);

	}

	public void testHasNext() throws RepositoryException {

		PropertyMapList propertyMapList = qtm.startTraversal();
		Iterator iter = propertyMapList.iterator();
		boolean rep = iter.hasNext();
		assertTrue(rep);
	}

	public void testNext() throws RepositoryException {
		int counter = 0;
		PropertyMapList propertyMapList = qtm.startTraversal();
		PropertyMap pm = null;
		Property prop = null;
		Iterator iter = propertyMapList.iterator();

		while (iter.hasNext()) {
			Object obj = iter.next();
			assertTrue(obj instanceof PropertyMap);
			assertTrue(obj instanceof DctmSysobjectPropertyMap);

			pm = (PropertyMap) obj;
			prop = pm.getProperty(SpiConstants.PROPNAME_DOCID);

			assertNotNull(prop);

			if (counter == 2) {

				break;
			}

		}

	}
}
