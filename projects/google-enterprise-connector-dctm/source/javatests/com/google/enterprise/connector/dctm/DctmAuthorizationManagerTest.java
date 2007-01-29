package com.google.enterprise.connector.dctm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmId;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSession;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSessionManager;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSysObject;
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

			/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmAuthorizationManager.authorizeDocids(List, String)'
	 */

public final void testAuthorizeDocids() throws RepositoryException, DfException{
		
		DctmAuthorizationManager authorizationManager;
		authorizationManager = null;
		Connector connector = new DctmConnector();
		
		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector).setQueryStringUnboundedDefault(DmInitialize.DM_QUERY_STRING_UNBOUNDED_DEFAULT);
		((DctmConnector) connector).setWebtopServerUrl(DmInitialize.DM_WEBTOP_SERVER_URL);
		((DctmConnector) connector).setQueryStringBoundedDefault(DmInitialize.DM_QUERY_STRING_BOUNDED_DEFAULT);
		((DctmConnector) connector).setAttributeName(DmInitialize.DM_ATTRIBUTE_NAME);
		((DctmConnector) connector).setQueryStringAuthoriseDefault(DmInitialize.DM_QUERY_STRING_AUTHORISE_DEFAULT);
		Session sess = (DctmSession) connector.login();
		authorizationManager = (DctmAuthorizationManager)sess.getAuthorizationManager(); 
	
		
		DmSessionManager dmSessMger=(DmSessionManager)authorizationManager.getSessionManager();
		DmSession dmSession=(DmSession)authorizationManager.getSessionManager().getSession(dmSessMger.getDocbaseName());
		
		{
	      String username = DmInitialize.DM_LOGIN_OK2;

	      Map expectedResults = new HashMap();
	      
	      
	      
	      String id1=getAuthorizedId(dmSession,username);
	      System.out.println("id1 vaut "+id1);
	      expectedResults.put(id1, Boolean.TRUE);
	      
	
	      String id2=getAuthorizedId(dmSession,username);
	      System.out.println("id2 vaut "+id2);
	      expectedResults.put(id2, Boolean.TRUE);
	      
	      String id3=getAuthorizedId(dmSession,username);
	      System.out.println("id3 vaut "+id3);
	      expectedResults.put(id3, Boolean.TRUE);
	      
	      String id4=getAuthorizedId(dmSession,username);
	      System.out.println("id4 vaut "+id4);
	      expectedResults.put(id4, Boolean.TRUE);
	      
	      String id5=getAuthorizedId(dmSession,username);
	      System.out.println("id5 vaut "+id5);
	      expectedResults.put(id5, Boolean.TRUE);

	      testAuthorization(authorizationManager, expectedResults, username);
	    }
	    
	    {
	      String username = DmInitialize.DM_LOGIN_OK3;

	      Map expectedResults = new HashMap();
	      String id15=getNonAuthorizedId(dmSession,username);
	      System.out.println("id15 vaut "+id15);
	      expectedResults.put(id15, Boolean.FALSE);
	      
	      String id6=getAuthorizedId(dmSession,username);
	      System.out.println("id6 vaut "+id6);
	      expectedResults.put(id6, Boolean.TRUE);
	      
	      String id7=getAuthorizedId(dmSession,username);
	      System.out.println("id7 vaut "+id7);
	      expectedResults.put(id7, Boolean.TRUE);
	      
	      String id8=getAuthorizedId(dmSession,username);
	      System.out.println("id8 vaut "+id8);
	      expectedResults.put(id8, Boolean.TRUE);
	      
	      String id9=getNonAuthorizedId(dmSession,username);
	      System.out.println("id9 vaut "+id9);
	      expectedResults.put(id9, Boolean.FALSE);

	      testAuthorization(authorizationManager, expectedResults, username);
	    }

	    {
	      String username = DmInitialize.DM_LOGIN_OK5;

	      Map expectedResults = new HashMap();
	      
	      String id10=getAuthorizedId(dmSession,username);
	      System.out.println("id10 vaut "+id10);
	      expectedResults.put(id10, Boolean.TRUE);
	      
	      String id11=getAuthorizedId(dmSession,username);
	      System.out.println("id11 vaut "+id11);
	      expectedResults.put(id11, Boolean.TRUE);
	      
	      String id12=getNonAuthorizedId(dmSession,username);
	      System.out.println("id12 vaut "+id12);
	      expectedResults.put(id12, Boolean.FALSE);
	      
	      String id13=getAuthorizedId(dmSession,username);
	      System.out.println("id13 vaut "+id13);
	      expectedResults.put(id13, Boolean.TRUE);
	      
	      String id14=getNonAuthorizedId(dmSession,username);
	      System.out.println("id14 vaut "+id14);
	      expectedResults.put(id14, Boolean.FALSE);

	      testAuthorization(authorizationManager, expectedResults, username);
	    }
		
	}
	
	private void testAuthorization(DctmAuthorizationManager authorizationManager, Map expectedResults, String username) throws RepositoryException {
		List docids = new LinkedList(expectedResults.keySet());
		
		ResultSet resultSet =
	        authorizationManager.authorizeDocids(docids, username);
		
		for (Iterator i = resultSet.iterator(); i.hasNext();) {
		      PropertyMap pm = (PropertyMap) i.next();
		      String uuid =
		          pm.getProperty(SpiConstants.PROPNAME_DOCID).getValue().getString();
		      boolean ok =
		          pm.getProperty(SpiConstants.PROPNAME_AUTH_VIEWPERMIT).getValue()
		              .getBoolean();
		      System.out.println("auth_view_permit pour "+uuid+" vaut "+ok);
		      Boolean expected = (Boolean) expectedResults.get(uuid);
		      Assert.assertEquals(username + " access to " + uuid, expected.booleanValue(), ok);
		  }
	}
	
	private String getAuthorizedId(DmSession session, String username) throws DfException {
		String idString;
		DmSession dctmSession = (DmSession) session;
		IDfSession dfSession = dctmSession.getDfSession();
		IDfId id = dfSession.getIdByQualification("dm_sysobject where world_permit > 3 or owner_name = '"+username+"'");
		idString = id.toString();
		return idString;
	}
	
	private String getNonAuthorizedId(DmSession session, String username) throws DfException {
		String idString;
		DmSession dctmSession = (DmSession) session;
		IDfSession dfSession = dctmSession.getDfSession();
		IDfId id = dfSession.getIdByQualification("dm_sysobject where group_permit < 3 and world_permit < 2 and owner_name != '"+username+"'");
		idString = id.toString();
		return idString;
	}
}



	


