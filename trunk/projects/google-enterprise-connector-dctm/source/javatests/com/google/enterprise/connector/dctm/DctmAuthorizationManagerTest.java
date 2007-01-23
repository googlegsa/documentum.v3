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
		
		DctmAuthorizationManager authorizationManager;
		authorizationManager = null;
		Connector connector = new DctmConnector();
		
		((DctmConnector) connector).setLogin("emilie");
		((DctmConnector) connector).setPassword("p@ssw0rd");
		((DctmConnector) connector).setDocbase("gsadctm");
		((DctmConnector) connector).setClient("com.google.enterprise.connector.dctm.dctmdfcwrap.DmClient");
		((DctmConnector) connector).setQUERY_STRING_UNBOUNDED_DEFAULT("select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document' " +"order by r_modify_date, i_chronicle_id ");
		((DctmConnector) connector).setWEBTOP_SERVER_URL("http://swp-vm-wt:8080/webtop/drl/objectId/");
		((DctmConnector) connector).setQUERY_STRING_BOUNDED_DEFAULT("select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type=''dm_document'' and r_modify_date >= "+ "''{0}'' "+"order by r_modify_date, i_chronicle_id");
		((DctmConnector) connector).setATTRIBUTE_NAME("r_object_id");
		((DctmConnector) connector).setQUERY_STRING_AUTHORISE_DEFAULT("select r_object_id from dm_sysobject where r_object_id in (");
		Session sess = (DctmSession) connector.login();
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

	


