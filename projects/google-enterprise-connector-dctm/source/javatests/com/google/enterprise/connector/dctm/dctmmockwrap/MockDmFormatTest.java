package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

public class MockDmFormatTest extends TestCase {
	
	IClientX dctmClientX;
	IClient localClient; 
	ISessionManager sessionManager; 
	ISession sess7;
	IId id;
	ISysObject object;
	IQuery query;
	String crID;
	IFormat format;
	
	public void setUp() throws Exception {

		super.setUp();
		dctmClientX = new MockDmClient();
		localClient = null;
		localClient = dctmClientX.getLocalClient();
		sessionManager = localClient.newSessionManager();
		ILoginInfo ili = new MockDmLoginInfo();
		ili.setUser("mark");
		ili.setPassword("mark");
		sessionManager.setIdentity("SwordEventLog.txt", ili);
		sess7 = sessionManager.getSession("SwordEventLog.txt");
		DmInitialize dmInit=new DmInitialize(false);
		query=localClient.getQuery();
		query.setDQL(DmInitialize.DM_QUERY_STRING_ENABLE);
		
		id = dctmClientX.getId(DmInitialize.DM_ID2);
		object = sess7.getObject(id);
		try {
			format=object.getFormat();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} 
		
	}
	
	public void testCanIndex() {
		boolean indexable=false;
			try {
				indexable=((MockDmFormat)format).canIndex();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			assertTrue(indexable);
	}

	public void testGetMIMEType() {
		String mime="";
		mime=((MockDmFormat)format).getMIMEType();
		assertEquals(mime,DmInitialize.DM_DEFAULT_MIMETYPE);
	}

}
