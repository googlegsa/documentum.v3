package com.google.enterprise.connector.dctm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
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
		assertNotNull(authorizationManager);
		
		{
			String username = DmInitialize.DM_LOGIN_OK2;

			Map expectedResults = new HashMap();
			expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID2, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID3, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID4, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID5, Boolean.FALSE);
			assertNotNull((DctmAuthorizationManager) authorizationManager);
			assertNotNull(expectedResults);
			assertNotNull(username);
			testAuthorization((DctmAuthorizationManager) authorizationManager,
					expectedResults, username);
		}
		
		
		{
			String username = DmInitialize.DM_LOGIN_OK3;

			Map expectedResults = new HashMap();
			expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID2, Boolean.FALSE);
			expectedResults.put(DmInitialize.DM_ID3, Boolean.FALSE);
			expectedResults.put(DmInitialize.DM_ID4, Boolean.FALSE);
			expectedResults.put(DmInitialize.DM_ID5, Boolean.FALSE);
			testAuthorization((DctmAuthorizationManager) authorizationManager,
					expectedResults, username);
		}
		
		{
			String username = DmInitialize.DM_LOGIN_OK1;

			Map expectedResults = new HashMap();
			expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID2, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID3, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID4, Boolean.TRUE);
			expectedResults.put(DmInitialize.DM_ID5, Boolean.TRUE);
			
			testAuthorization((DctmAuthorizationManager) authorizationManager,
					expectedResults, username);
		}
		
	}

	private void testAuthorization(
			DctmAuthorizationManager authorizationManager, Map expectedResults,
			String username) throws RepositoryException {
		
		System.out.println("testAuthorization");
		List docids = new LinkedList(expectedResults.keySet());
		
		for(int j=0;j<docids.size();j++){
			String id=(String)docids.get(j);
			System.out.println("j vaut "+id);
		}
		
		assertNotNull(docids);
		ResultSet resultSet = authorizationManager.authorizeDocids(docids,
				username);
		assertNotNull(resultSet);
		for (Iterator i = resultSet.iterator(); i.hasNext();) {
			System.out.println("dans boucle");
			PropertyMap pm = (PropertyMap) i.next();
			assertNotNull(pm);
			System.out.println("pm vaut "+pm);
			String uuid = pm.getProperty(SpiConstants.PROPNAME_DOCID)
					.getValue().getString();
			assertNotNull(uuid);
			System.out.println("uuid vaut "+uuid);
			///String test = pm.getProperty(SpiConstants.PROPNAME_ISPUBLIC).getValue().getString();
			///assertNotNull(test);
			///System.out.println("test vaut "+test);
			boolean ok = pm.getProperty(SpiConstants.PROPNAME_AUTH_VIEWPERMIT)
					.getValue().getBoolean();
			String test = pm.getProperty(SpiConstants.PROPNAME_AUTH_VIEWPERMIT)
			.getValue().getString();
			System.out.println("ok vaut "+ok);
			System.out.println("test vaut "+test);
			Boolean expected = (Boolean) expectedResults.get(uuid);
			Assert.assertEquals(username + " access to " + uuid, expected
					.booleanValue(), ok);
		}
	}

}
