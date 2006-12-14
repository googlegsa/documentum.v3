package com.google.enterprise.connector.dctm;

import java.io.ByteArrayInputStream;

import javax.jcr.query.QueryManager;
/*
import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;
*/
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmClient;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmCollection;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmFormat;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLocalClient;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLoginInfo;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmQuery;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSession;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSessionManager;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSysObject;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmValue;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.SimpleProperty;
import com.google.enterprise.connector.spi.SimplePropertyMap;
import com.google.enterprise.connector.spi.SimpleResultSet;
import com.google.enterprise.connector.spi.SimpleValue;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.ValueType;

import junit.framework.TestCase;

public class DctmQueryTraversalManagerTest extends TestCase {
	IDctmSession idctmses;
	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmQueryTraversalManager.startTraversal()'
	 */
	
	
	
	
	
	public void testSetIdctmses() {
		//System.out.println("Native Library path(s): " + System.getProperty("java.library.path"));
		//System.out.println("Native Library path(s): " + System.getProperty("path"));
	  	IDctmClient dctmClient=new IDctmClient();
	  	IDctmLocalClient dctmLocalClient=(IDctmLocalClient)dctmClient.getLocalClientEx();
	  	IDctmSessionManager dctmsessionmanager=(IDctmSessionManager)dctmLocalClient.newSessionManager(); 
	  	IDctmLoginInfo dctmLoginInfodctmLoginInfo=new IDctmLoginInfo();
	  	dctmLoginInfodctmLoginInfo.setUser("emilie");
	  	dctmLoginInfodctmLoginInfo.setPassword("emilie2");
	  	dctmsessionmanager.setIdentity("gdoc", dctmLoginInfodctmLoginInfo);
	  	idctmses = ((IDctmSession)dctmsessionmanager.newSession("gdoc"));
	  	assertNotNull(idctmses);
	}
	
	
	
	public void testStartTraversal() throws RepositoryException{
		
		ResultSet resu=null;
		  String query=null;
		  IDctmCollection col=null;
		  byte[]buf=null;
		  int count = 0;
		  
		  String modifDate=null;
		  String crID=null;
		  String mimetype=null;
		  
		  SimpleValue vlDate=null;
		  SimpleValue vlID=null;
		  SimpleValue vlMime=null;
		  
		  SimplePropertyMap pm=null;
		  
		  ByteArrayInputStream content=null;
		  
		  testSetIdctmses();
		 
		  IDctmSysObject dctmSysObj = null;
		  IDctmFormat dctmForm = null;
		  
		  DctmQueryTraversalManager 
		  col=testExecQuery();
		  assertNotNull(col);
		  
		  IDfCollection idfcol=col.getIDfCollection();
		  assertNotNull(idfcol);
		  
		  resu=new SimpleResultSet(); 
		
		  
			  while (col.next()){
				  pm=new SimplePropertyMap();
				 
				  crID = col.getValue("i_chronicle_id").asString();
				  vlID=new SimpleValue(ValueType.STRING,crID);
				  pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_DOCID,vlID));
				  
				  IDctmValue valDate=(IDctmValue)col.getValue("r_modify_date");
					  //modifDate = (col.getValue("r_modify_date")).asString();
				if (valDate != null){  
				modifDate = valDate.asString();
					  vlDate=new SimpleValue(ValueType.DATE,modifDate);
					  pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_LASTMODIFY,vlDate)); 
				}  
				 
				  dctmSysObj = (IDctmSysObject)idctmses.getObjectByQualification("dm_document where i_chronicle_id = '" + crID + "'");
				  dctmForm = (IDctmFormat)dctmSysObj.getFormat();
				  
				  if(dctmForm.canIndex()){
					  content=dctmSysObj.getContent();
					  mimetype=dctmForm.getMIMEType();
				  }
				  vlMime=new SimpleValue(ValueType.STRING,mimetype);
				  pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_MIMETYPE,vlMime));
					 
			  }
		 	  
			  assertNotNull(resu);
		
		
	}
	
	

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmQueryTraversalManager.execQuery(String)'
	 */
	
	public void testExecQuery() {
		IDctmCollection dctmCollection = null; // Collection for the result
		IDctmSession dctmSes=testGetIdctmses();
		assertNotNull(dctmSes);
		
		IDfSession idfSes=dctmSes.getDfSession();
		assertNotNull(idfSes);
		
		IDctmQuery dctmQuery = new IDctmQuery(); // Create query object
		String query="select i_chronicle_id from dm_sysobject where r_object_type='dm_document' and r_creator_name!='Administrator' order by r_modify_date, i_chronicle_id";
		dctmQuery.setDQL(query); // Give it the query
		
		dctmCollection = (IDctmCollection)dctmQuery.execute(dctmSes, IDctmQuery.DF_READ_QUERY);
		assertNotNull(dctmCollection);
		
	}
	
	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmQueryTraversalManager.getIdctmses()'
	 */
	

	public IDctmSession testGetIdctmses() {
		return idctmses;
	}
	
	/*
	public void TestDFCConnexion(){
		IDfSession idfsession=null;
		IDfClient client=null;
		IDfSessionManager sMgr=null;
		IDfLoginInfo loginInfo=null;
		String docbase=null;
		String username=null;
		
		client = DfClient.getLocalClientEx();

	  	//.getClientNetworkLocations
		sMgr = client.newSessionManager(); 
		
		loginInfo = new DfLoginInfo();
		loginInfo.setUser("emilie");
		loginInfo.setPassword("emilie2");
		try{
		sMgr.setIdentity( "gdoc", loginInfo );
		idfsession = sMgr.getSession( "gdoc" );
		}catch(DfException de){
			de.getMessage();
		}
		assertNotNull(idfsession);
	}
	*/
}
