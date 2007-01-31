package com.google.enterprise.connector.dctm.dfcwrap;

import com.documentum.fc.client.IDfSession;
import junit.framework.TestCase;
import org.json.JSONException;
import org.json.JSONObject;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.DctmConnector;
import com.google.enterprise.connector.dctm.DctmQueryTraversalManager;
import com.google.enterprise.connector.dctm.DctmSession;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmId;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSysObject;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

public class IDctmSessionTest extends TestCase{

	Session session = null;

	Connector connector = null;
	
	public IDctmSessionTest(String arg0) {
		super(arg0);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		connector = new DctmConnector();
		session = connector.login();
	}
	
	
	public void testGetObject() throws RepositoryException {
		
		/*
		String phrase="tralala";
		assertEquals(phrase,"tralala");
		*/
		
		ISysObject mySysObject = null;
		IId objectId=new DmId("090000018000027e");
		/*
		ISession mySession=((DctmSession)session).getSession();
		mySysObject = mySession.getObject(objectId);
		assertEquals(mySysObject.getTitle(),"Records Migration Agent Code");
		*/
		
	}
	
	
}