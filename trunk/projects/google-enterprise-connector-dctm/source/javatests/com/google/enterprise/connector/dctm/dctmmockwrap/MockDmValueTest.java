package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.util.Date;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MockDmValueTest extends TestCase {

	IClientX dctmClientX;
	IClient localClient; 
	ISessionManager sessionManager; 
	ISession sess7;
	IId id;
	ISysObject object;
	IQuery query;
	String crID;
	ICollection collec;
	
	public void setUp() throws Exception {

		super.setUp();
		dctmClientX = new MockDmClient();
		localClient = null;
		localClient = dctmClientX.getLocalClient();
		sessionManager = localClient.newSessionManager();
		ILoginInfo ili = new MockDmLoginInfo();
		ili.setUser(DmInitialize.DM_LOGIN_OK4);
		ili.setPassword(DmInitialize.DM_PWD_OK4);
		sessionManager.setIdentity(DmInitialize.DM_DOCBASE, ili);
		sess7 = sessionManager.getSession(DmInitialize.DM_DOCBASE);
		query=localClient.getQuery();
		query.setDQL(DmInitialize.DM_QUERY_STRING_ENABLE);
		collec=query.execute(sessionManager,IQuery.READ_QUERY);
		collec.next();
			
		
		
	}
	
	
	public void testAsString() {
		try {
			IValue val=collec.getValue("r_object_id");
			Assert.assertTrue( val instanceof MockDmValue);
			String valSg=val.asString();
			assertEquals(valSg,DmInitialize.DM_ID1);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	

	public void testGetDataType() {
		try {
			IValue val=collec.getValue("r_object_id");
			Assert.assertTrue( val instanceof MockDmValue);
			int type=((MockDmValue)val).getDataType();
			assertEquals(type,DmInitialize.DM_ID1_TYPE);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

}
