package com.google.enterprise.connector.dctm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SpiConstants;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DctmAuthorizationManagerTest extends TestCase {

	public DctmAuthorizationManagerTest(String arg0) {
		super(arg0);
	}

	public final void testAuthorizeDocids() throws RepositoryException {

		AuthorizationManager authorizationManager;
		authorizationManager = null;
		Connector connector = new DctmConnector();

		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector)
				.setWebtopServerUrl(DmInitialize.DM_WEBTOP_SERVER_URL);
		Session sess = (DctmSession) connector.login();
		authorizationManager = (DctmAuthorizationManager) sess
				.getAuthorizationManager();

		{
			String username = DmInitialize.DM_LOGIN_OK2;

			Map expectedResults = new HashMap();
			expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID2, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID3, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID4, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID5, Boolean.TRUE);
			testAuthorization((DctmAuthorizationManager) authorizationManager,
					expectedResults, username);
		}

		{
			String username = DmInitialize.DM_LOGIN_OK3;

			Map expectedResults = new HashMap();
			expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID2, Boolean.FALSE);
			expectedResults.put(DmInitialize.DM_ID3, Boolean.FALSE);
			expectedResults.put(DmInitialize.DM_ID4, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID5, Boolean.TRUE);
			testAuthorization((DctmAuthorizationManager) authorizationManager,
					expectedResults, username);
		}

		{
			String username = DmInitialize.DM_LOGIN_OK5;

			Map expectedResults = new HashMap();
			expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID2, Boolean.FALSE);
			expectedResults.put(DmInitialize.DM_ID3, Boolean.FALSE);
			expectedResults.put(DmInitialize.DM_ID4, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID5, Boolean.TRUE);
			testAuthorization((DctmAuthorizationManager) authorizationManager,
					expectedResults, username);
		}

	}

	private void testAuthorization(
			DctmAuthorizationManager authorizationManager, Map expectedResults,
			String username) throws RepositoryException {
		List docids = new LinkedList(expectedResults.keySet());

		ResultSet resultSet = authorizationManager.authorizeDocids(docids,
				username);
		for (Iterator i = resultSet.iterator(); i.hasNext();) {

			PropertyMap pm = (PropertyMap) i.next();
			String uuid = pm.getProperty(SpiConstants.PROPNAME_DOCID)
					.getValue().getString();
			boolean ok = pm.getProperty(SpiConstants.PROPNAME_AUTH_VIEWPERMIT)
					.getValue().getBoolean();
			Boolean expected = (Boolean) expectedResults.get(uuid);
			Assert.assertEquals(username + " access to " + uuid, expected
					.booleanValue(), ok);
		}
	}

}
