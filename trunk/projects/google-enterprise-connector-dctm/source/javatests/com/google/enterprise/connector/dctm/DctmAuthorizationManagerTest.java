package com.google.enterprise.connector.dctm;

import java.util.ArrayList;
import java.util.List;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

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
		
		List idList;
		DctmAuthorizationManager authoriseManager;
		DctmResultSet resultSetWaited;
			idList = new ArrayList(3);
		idList.add("0900000180003328");
		idList.add("090000018000333a");
		idList.add("0900000180003321");
		authoriseManager = null;
		resultSetWaited = new DctmResultSet();
		DctmPropertyMap propMap = new DctmPropertyMap();
		propMap.putProperty(new DctmProperty("PROPNAME_AUTH_VIEWPERMIT",false));
		propMap.putProperty(new DctmProperty("PROPNAME_DOCID","0900000180003321"));
		resultSetWaited.add(propMap);
		propMap = new DctmPropertyMap();
		propMap.putProperty(new DctmProperty("PROPNAME_AUTH_VIEWPERMIT",true));
		propMap.putProperty(new DctmProperty("PROPNAME_DOCID","090000018000333a"));
		resultSetWaited.add(propMap);
		propMap = new DctmPropertyMap();
		propMap.putProperty(new DctmProperty("PROPNAME_AUTH_VIEWPERMIT",true));
		propMap.putProperty(new DctmProperty("PROPNAME_DOCID","0900000180003328"));
		resultSetWaited.add(propMap);
		Connector myconn=new DctmConnector();
		
		((DctmConnector)myconn).setLogin("emilie");
		((DctmConnector)myconn).setPassword("p@ssw0rd");
		Session sess = (DctmSession) myconn.login();
		authoriseManager = (DctmAuthorizationManager)sess.getAuthorizationManager(); 
		Assert.assertEquals(resultSetWaited, authoriseManager.authorizeDocids((List)idList,"user2"));
	}

	

}
