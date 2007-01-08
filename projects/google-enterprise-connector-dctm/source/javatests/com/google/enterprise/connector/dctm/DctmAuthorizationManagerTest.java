package com.google.enterprise.connector.dctm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	public final void testAuthorizeDocids() throws RepositoryException{
		
//		List idList;
		DctmAuthorizationManager authorizationManager;
//		DctmResultSet expectedResults;
//		idList = new ArrayList(3);
//		idList.add("0900000180007e71");
//		idList.add("0900000180007f9f");
//		idList.add("0900000180007e85");
		authorizationManager = null;
//		expectedResults = new DctmResultSet();
//		SimplePropertyMap propMap = new SimplePropertyMap();
//		propMap.putProperty(new SimpleProperty("PROPNAME_AUTH_VIEWPERMIT",false));
//		propMap.putProperty(new SimpleProperty("PROPNAME_DOCID","0900000180007e71"));
//		expectedResults.add(propMap);
//		propMap = new SimplePropertyMap();
//		propMap.putProperty(new SimpleProperty("PROPNAME_AUTH_VIEWPERMIT",true));
//		propMap.putProperty(new SimpleProperty("PROPNAME_DOCID","0900000180007f9f"));
//		expectedResults.add(propMap);
//		propMap = new SimplePropertyMap();
//		propMap.putProperty(new SimpleProperty("PROPNAME_AUTH_VIEWPERMIT",true));
//		propMap.putProperty(new SimpleProperty("PROPNAME_DOCID","0900000180007e85"));
//		expectedResults.add(propMap);
		Connector myconn=new DctmConnector();
		
		((DctmConnector)myconn).setLogin("user1");
		((DctmConnector)myconn).setPassword("p@ssw0rd");
		Session sess = (DctmSession) myconn.login();
		authorizationManager = (DctmAuthorizationManager)sess.getAuthorizationManager(); 
	{
	      String username = "user1";

	      Map expectedResults = new HashMap();
	      expectedResults.put("0900000180007e71", Boolean.TRUE);
	      expectedResults.put("0900000180007f9f", Boolean.TRUE);
	      expectedResults.put("0900000180007e85", Boolean.TRUE);
	      expectedResults.put("0900000180007e91", Boolean.TRUE);
	      expectedResults.put("0900000180007f93 ", Boolean.TRUE);

	      testAuthorization(authorizationManager, expectedResults, username);
	    }

	    {
	      String username = "user2";

	      Map expectedResults = new HashMap();
	      expectedResults.put("0900000180007e71", Boolean.FALSE);
	      expectedResults.put("0900000180007f9f", Boolean.TRUE);
	      expectedResults.put("0900000180007e85", Boolean.TRUE);
	      expectedResults.put("0900000180007e91", Boolean.TRUE);
	      expectedResults.put("0900000180007f93", Boolean.FALSE);

	      testAuthorization(authorizationManager, expectedResults, username);
	    }

	    {
	      String username = "Fred";

	      Map expectedResults = new HashMap();
	      expectedResults.put("0900000180007e71", Boolean.TRUE);
	      expectedResults.put("0900000180007f9f", Boolean.TRUE);
	      expectedResults.put("0900000180007e85", Boolean.FALSE);
	      expectedResults.put("0900000180007e91", Boolean.TRUE);
	      expectedResults.put("0900000180007f93", Boolean.FALSE);

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
		      Boolean expected = (Boolean) expectedResults.get(uuid);
		      Assert.assertEquals(username + " access to " + uuid, expected.booleanValue(), ok);
		    }
		  }
	
		
	}

	


