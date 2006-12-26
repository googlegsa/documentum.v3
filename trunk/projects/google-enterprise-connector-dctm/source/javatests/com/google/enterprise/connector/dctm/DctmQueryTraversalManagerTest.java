package com.google.enterprise.connector.dctm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;


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
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmTime;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmValue;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.Session;
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
	
	 private static final String QUERY_STRING_UNBOUNDED_DEFAULT = "select i_chronicle_id, r_modify_date from dm_sysobject where r_object_type='dm_document' and r_creator_name!='Administrator' order by r_modify_date, i_chronicle_id";
	  
	 private static final String QUERY_STRING_BOUNDED_DEFAULT = 
		 "select i_chronicle_id, r_modify_date from dm_sysobject where r_object_type=''dm_document'' and r_creator_name!=''Administrator'' and r_modify_date >= "+ 
		 "''{0}'' "+
		 "order by i_chronicle_id, r_modify_date";
	
	 private String unboundedTraversalQuery;
	 private String boundedTraversalQuery;
	
	
	
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
		
		 SimpleResultSet resu=null;
		  IQuery query=null;
		  ICollection col=null;
		  byte[]buf=null;
		  int count = 0;
		  
		  String modifDate=null;
		  String crID=null;
		  String mimetype=null;
		  
		  SimpleValue vlDate=null;
		  SimpleValue vlID=null;
		  SimpleValue vlMime=null;
		  SimpleValue vlCont=null;
		  
		  SimplePropertyMap pm=null;
		  
		  ByteArrayInputStream content=null;
		  
		  int size=0;
		  byte[] bufContent;
		  
		  testSetIdctmses();
		  
		  ISysObject dctmSysObj = null;
		  IFormat dctmForm = null;
		  
		  
		  
		 
		  testExecQuery();
		  
		  resu=new SimpleResultSet(); 
		  
			  while (col.next()){
				  pm=new SimplePropertyMap();
				 
				  crID = col.getValue("i_chronicle_id").asString();
				  vlID=new SimpleValue(ValueType.STRING,crID);
				  pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_DOCID,vlID));
				
				  System.out.println(col.getValue("r_modify_date").toString());
				  //modifDate = col.getValue("r_modify_date").asString();
				  modifDate = col.getValue("r_modify_date").toString();
				  
				  vlDate=new SimpleValue(ValueType.DATE,modifDate);
				  pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_LASTMODIFY,vlDate)); 
				  
				  dctmSysObj = (IDctmSysObject)idctmses.getObjectByQualification("dm_document where i_chronicle_id = '" + crID + "'");
				  dctmForm = (IDctmFormat)dctmSysObj.getFormat();
				  
				  if(dctmForm.canIndex()){
					  content=dctmSysObj.getContent();
					  mimetype=dctmForm.getMIMEType();
					  size=new Long(dctmSysObj.getContentSize()).intValue();
						 
					   bufContent = new byte[size];
						ByteArrayOutputStream output=new ByteArrayOutputStream(); 
						 try{
							 
							 while ((count = content.read(bufContent)) > -1){
							 
								 output.write(bufContent, 0, count);
							 }
							 content.close();
						 }catch(IOException ie){
							 System.out.println(ie.getMessage());
						 }
						 //content.
						 if(bufContent.length>0){
							 vlCont=new SimpleValue(ValueType.BINARY,bufContent);
							 pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_CONTENT,vlCont));
						 }else{
							 vlCont=new SimpleValue(ValueType.BINARY,"");
							 pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_CONTENT,vlCont));
						 }
				  }
				  
				  vlMime=new SimpleValue(ValueType.STRING,mimetype);
				  pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_MIMETYPE,vlMime));
				  resu.add(pm); 
			  }
		  assertNotNull(resu); 
		  
		
	}
	
	

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmQueryTraversalManager.execQuery(String)'
	 */
	/*
	public void testResumeTraversal() throws RepositoryException{
		DctmSession sess=null;
		ISession idctmses=null;
		
		QueryTraversalUtil qtu=new QueryTraversalUtil();

		Connector myconn=null;
		QueryTraversalManager qtm=null;
		
		
		///recup d'une session
		myconn=new DctmConnector();
		
		((DctmConnector)myconn).setLogin("emilie");
		((DctmConnector)myconn).setPassword("emilie2");
		((DctmConnector)myconn).setDocbase("gdoc");
		
		
		
		sess=(DctmSession)myconn.login();
		IClient client=sess.getClient();
		idctmses=client.get();
		
		assertNotNull(idctmses);
		///
		
		///String checkPoint="{\"uuid\":\"0900045780030e40\",\"lastModified\":\"2006-09-26T22:00:00.000Z\"}";
		String checkPoint="{\"uuid\":\"0900045780030dae\",\"lastModified\":\"2006-10-03\"}";
		///{"uuid":"0900045780030dae","lastModified":"2006-10-03"}
		DctmResultSet resu=null;
		JSONObject jo = null;
		IQuery query=null;
		ICollection col=null;
		byte[]buf=null;
		int count = 0;
		
		String modifDate=null;
		String crID=null;
		String mimetype=null;
		
		DctmValue vlDate=null;
		DctmValue vlID=null;
		DctmValue vlMime=null;
		DctmValue vlCont=null;
		
		DctmPropertyMap pm=null;
		
		ByteArrayInputStream content=null;
		
		int size=0;
		byte[] bufContent;
		
		//ISession dctmSes = getIdctmses();
		ISysObject dctmSysObj = null;
		IFormat dctmForm = null;
		IDctmValue val=null;
		ITime itime=null;
		
		try {
			jo = new JSONObject(checkPoint);
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"checkPoint string does not parse as JSON: " + checkPoint);
		}
		String uuid = extractDocidFromCheckpoint(jo, checkPoint);
		Calendar c = extractCalendarFromCheckpoint(jo, checkPoint);
		String queryString = makeCheckpointQueryString(uuid, c);
		System.out.println("querystring vaut "+queryString);
		assertNotNull(queryString);
		
	
		query=makeCheckpointQuery(queryString);
		assertNotNull(idctmses);
		assertNotNull(query);
		col=query.execute((ISession)idctmses, IDctmQuery.DF_READ_QUERY);
		assertNotNull(col);
		resu=new DctmResultSet();
		while (col.next()){
			pm=new DctmPropertyMap();
			
			crID = col.getValue("i_chronicle_id").asString();
			int rep_Id=col.getValue("i_chronicle_id").getDataType();
			
			vlID=new DctmValue(ValueType.STRING,crID);
			pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_DOCID,vlID));
			
			// System.out.println("r_modify_date "+col.getValue("r_modify_date"));
			
			
			
			
			val=(IDctmValue)col.getValue("r_modify_date");
			int rep=val.getDataType();
			itime=val.asTime();
			modifDate = itime.asString(IDctmTime.DF_TIME_PATTERN45);
			Date mydate=itime.getDate();
			
			System.out.println("crID vaut "+crID+" : modifdate vaut "+modifDate);
			vlDate=new DctmValue(ValueType.DATE,modifDate);
			pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_LASTMODIFY,vlDate)); 
			
			dctmSysObj = (IDctmSysObject)((ISession)idctmses).getObjectByQualification("dm_document where i_chronicle_id = '" + crID + "'");
			dctmForm = (IDctmFormat)dctmSysObj.getFormat();
			
			if(dctmForm.canIndex()){
				content=dctmSysObj.getContent();
				mimetype=dctmForm.getMIMEType();
				size=new Long(dctmSysObj.getContentSize()).intValue();
				
				bufContent = new byte[size];
				ByteArrayOutputStream output=new ByteArrayOutputStream(); 
				try{
					
					while ((count = content.read(bufContent)) > -1){
						
						output.write(bufContent, 0, count);
					}
					content.close();
				}catch(IOException ie){
					System.out.println(ie.getMessage());
				}
				//content.
				if(bufContent.length>0){
					vlCont=new DctmValue(ValueType.BINARY,bufContent);
					pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_CONTENT,vlCont));
				}else{
					vlCont=new DctmValue(ValueType.BINARY,"");
					pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_CONTENT,vlCont));
				}
			}
			
			vlMime=new DctmValue(ValueType.STRING,mimetype);
			pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_MIMETYPE,vlMime));
			assertNotNull(pm);
			resu.add(pm);	 
		}
		int nb=resu.size();	
		
		System.out.println("nb vaut "+nb);
		
		
		
		
	}
	*/
	
	String extractDocidFromCheckpoint(JSONObject jo, String checkPoint) {
		String uuid = null;
		try {
			uuid = jo.getString("uuid");
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"could not get uuid from checkPoint string: " + checkPoint);
		}
		return uuid;
	}
	
	Calendar extractCalendarFromCheckpoint(JSONObject jo, String checkPoint) {
		String dateString = null;
		try {
			dateString = jo.getString("lastModified");
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"could not get lastmodify from checkPoint string: " + checkPoint);
		}
		Calendar c = null;
		try {
			c = DctmValue.iso8601ToCalendar(dateString);
		} catch (ParseException e) {
			throw new IllegalArgumentException(
					"could not parse date string from checkPoint string: " + dateString);
		}
		return c;
	}
	
	private String makeCheckpointQueryString(String uuid, Calendar c)
	throws RepositoryException {
		String statement=null;
		String time = DctmValue.calendarToIso8601(c);
		System.out.println("time vaut "+time);
		Object[] arguments = { time };
		boundedTraversalQuery=QUERY_STRING_BOUNDED_DEFAULT;
		System.out.println("boundedTraversalQuery vaut "+boundedTraversalQuery);
		assertNotNull(arguments);
		assertNotNull(boundedTraversalQuery);
		try{
			statement = MessageFormat.format(boundedTraversalQuery,arguments);
		}catch(IllegalArgumentException ie){
			ie.getMessage();
		}
		return statement;
	}
	
	
	
	 public void testExecQuery() {
		  	ICollection dctmCollection = null; // Collection for the result
		  	IQuery query=null;
		  	try{
		  	query=makeCheckpointQuery(unboundedTraversalQuery);
		  	}catch(RepositoryException re){
			re.getMessage();
		  	}
		  	assertNotNull(idctmses);
		  	assertNotNull(idctmses.getDfSession());
		  	dctmCollection = query.execute(idctmses, IDctmQuery.DF_READ_QUERY);
			assertNotNull(dctmCollection);
	}
	
	
	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmQueryTraversalManager.getIdctmses()'
	 */
	private IQuery makeCheckpointQuery(String queryString) throws RepositoryException {
	    IQuery query = null;
	    query=new IDctmQuery();
	    System.out.println(queryString);
	    query.setDQL(queryString);
	    return query;
	}

	public IDctmSession testGetIdctmses() {
		return idctmses;
	}
	
	
	public ICollection execQuery(IQuery query) {
		ICollection dctmCollection = null; // Collection for the result
		dctmCollection = query.execute(idctmses, IDctmQuery.DF_READ_QUERY);
		return dctmCollection;
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
