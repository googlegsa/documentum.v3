package com.google.enterprise.connector.dctm.dctmmockwrap;

import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.jcradaptor.SpiResultSetFromJcr;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.mock.jcr.MockJcrQueryManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MockDmCollectionTest extends TestCase {
	IClientX dctmClientX;
	IClient localClient; 
	ISessionManager sessionManager; 
	ISession sess7; 
	
	public void setUp() throws Exception {

		super.setUp();

		dctmClientX = new MockDmClient();
		localClient = null;
		localClient = dctmClientX.getLocalClient();
		sessionManager = localClient.newSessionManager();
		ILoginInfo ili = new MockDmLoginInfo();
		ili.setUser("mark");
		ili.setPassword("mark");
		sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);
		sess7 = sessionManager.getSession("MockRepositoryEventLog7.txt");
		DmInitialize dmInit=new DmInitialize(false);
	}
	
	public void testNextAndGetString() {
		try {
			

			MockRepositoryDocumentStore mrDS = ((MockDmSession) sess7)
					.getStore();
			MockJcrQueryManager mrQueryMger = new MockJcrQueryManager(mrDS);
			Query q = null;
			QueryResult qr = null;
			try {
				q = mrQueryMger
						.createQuery(
								"//*[@jcr:primaryType='nt:resource'] order by @jcr:lastModified, @jcr:uuid",
								"xpath");
				qr = q.execute();
			} catch (InvalidQueryException e1) {
				assertTrue(false);
			} catch (javax.jcr.RepositoryException e1) {
				assertTrue(false);
			}
			MockDmCollection co = new MockDmCollection(qr);

			// now it begins
			assertTrue(co.next());


			// 2nd element of the collection looks like this :
			/*
			 * jcr:content : --> This is the public document. jcr:lastModified :
			 * --> 1970-01-01T00:00:10.000Z jcr:uuid : --> doc1 acl : --> joe
			 * --> mary google:ispublic : --> true
			 */
			
			/*
			assertEquals(co.getString("jcr:uuid"), "doc1");
			try {
				assertEquals(co.getAuthorizedUsers()[0].getString(), "joe");
			} catch (Exception e) {
				assertTrue(false);
			}
			*/


		} catch (RepositoryException e) {
			assertTrue(false);
		}
	}
	
	public void testGetValue() throws RepositoryException {
		IQuery query=null;
		query=localClient.getQuery();
		///System.out.println("DM_QUERY_STRING_ENABLE vaut "+DmInitialize.DM_QUERY_STRING_ENABLE);
		query.setDQL(DmInitialize.DM_QUERY_STRING_ENABLE);
		ICollection collec=query.execute(sessionManager,IQuery.DF_READ_QUERY);
		if(collec.next()){
			IValue val=collec.getValue("r_object_id");
			Assert.assertTrue( val instanceof MockDmValue);
		}
	}
	
	public void testGetString() throws RepositoryException {
		IQuery query=null;
		String rep="";
		query=localClient.getQuery();
		query.setDQL(DmInitialize.DM_QUERY_STRING_ENABLE);
		ICollection collec=query.execute(sessionManager,IQuery.DF_READ_QUERY);
		if(collec.next()){
			rep=collec.getString("jcr:uuid");
			Assert.assertEquals(rep,DmInitialize.DM_ID1);
		}	
	}
	
}