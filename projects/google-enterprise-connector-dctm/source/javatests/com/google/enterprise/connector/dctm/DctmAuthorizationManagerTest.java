package com.google.enterprise.connector.dctm;

import java.util.ArrayList;
import java.util.List;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SimpleProperty;
import com.google.enterprise.connector.spi.SimplePropertyMap;

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
		idList.add("0900000180007e71");
		idList.add("0900000180007f9f");
		idList.add("0900000180007e85");
		authoriseManager = null;
		resultSetWaited = new DctmResultSet();
		SimplePropertyMap propMap = new SimplePropertyMap();
		propMap.putProperty(new SimpleProperty("PROPNAME_AUTH_VIEWPERMIT",false));
		propMap.putProperty(new SimpleProperty("PROPNAME_DOCID","0900000180007e71"));
		resultSetWaited.add(propMap);
		propMap = new SimplePropertyMap();
		propMap.putProperty(new SimpleProperty("PROPNAME_AUTH_VIEWPERMIT",true));
		propMap.putProperty(new SimpleProperty("PROPNAME_DOCID","0900000180007f9f"));
		resultSetWaited.add(propMap);
		propMap = new SimplePropertyMap();
		propMap.putProperty(new SimpleProperty("PROPNAME_AUTH_VIEWPERMIT",true));
		propMap.putProperty(new SimpleProperty("PROPNAME_DOCID","0900000180007e85"));
		resultSetWaited.add(propMap);
		Connector myconn=new DctmConnector();
		
		((DctmConnector)myconn).setLogin("emilie");
		((DctmConnector)myconn).setPassword("p@ssw0rd");
		Session sess = (DctmSession) myconn.login();
		authoriseManager = (DctmAuthorizationManager)sess.getAuthorizationManager(); 
		Assert.assertEquals(resultSetWaited, authoriseManager.authorizeDocids((List)idList,"user2"));
	}

	

}
