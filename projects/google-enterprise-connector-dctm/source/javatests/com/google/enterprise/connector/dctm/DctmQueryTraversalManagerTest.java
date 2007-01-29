package com.google.enterprise.connector.dctm;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.logging.Logger;

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
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SimpleProperty;
import com.google.enterprise.connector.spi.SimplePropertyMap;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.ValueType;

import junit.framework.TestCase;

public class DctmQueryTraversalManagerTest extends TestCase {

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmQueryTraversalManager.startTraversal()'
	 */


	Session session = null;

	Connector connector = null;

	DctmQueryTraversalManager qtm = null;
	
	private static final Logger logger =
	      Logger.getLogger(DctmQueryTraversalManagerTest.class.getName());
	
	
	public DctmQueryTraversalManagerTest(String arg0) {
		super(arg0);
	}
	
	
	protected void setUp() throws Exception {
		super.setUp();

		
		qtm = null;
		Connector connector = new DctmConnector();
		
		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector).setQueryStringUnboundedDefault(DmInitialize.DM_QUERY_STRING_UNBOUNDED_DEFAULT);
		((DctmConnector) connector).setWebtopServerUrl(DmInitialize.DM_WEBTOP_SERVER_URL);
		((DctmConnector) connector).setQueryStringBoundedDefault(DmInitialize.DM_QUERY_STRING_BOUNDED_DEFAULT);
		((DctmConnector) connector).setAttributeName(DmInitialize.DM_ATTRIBUTE_NAME);
		((DctmConnector) connector).setQueryStringAuthoriseDefault(DmInitialize.DM_QUERY_STRING_AUTHORISE_DEFAULT);
		Session sess = (DctmSession) connector.login();
		qtm = (DctmQueryTraversalManager)sess.getQueryTraversalManager(); 
		
	}

	/*
	public void testMakeCheckpointQueryString(){
		String uuid="090000018000e100";
		String statement="";
		///try{
			///calDate=DctmSimpleValue.iso8601ToCalendar("2007-01-02T13:58:10Z");
			///calDate=DctmSimpleValue.iso8601ToCalendar("2007-01-02 13:58:10");
		///}catch(ParseException pe){
			///pe.printStackTrace();
		///}
		try{
			statement=qtm.makeCheckpointQueryString(uuid,"2007-01-02 13:58:10");
		}catch(RepositoryException re){
			re.printStackTrace();
		}
		
		assertNotNull(statement);
		///assertEquals(statement,"select i_chronicle_id, r_modify_date from dm_sysobject where r_object_type='dm_document' and r_modify_date >= '2007-01-02 13:58:10.000' order by r_modify_date, i_chronicle_id");
		assertEquals(statement,"select i_chronicle_id, r_modify_date from dm_sysobject where r_object_type='dm_document' and r_modify_date >= '2007-01-02 13:58:10' order by r_modify_date, i_chronicle_id");
		
	}
	
	
	
	public void testExtractDocidFromCheckpoint(){
		String checkPoint="{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02 13:58:10\"}";
		String uuid = null;
		JSONObject jo = null;
		
		
		try {
			jo = new JSONObject(checkPoint);
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"checkPoint string does not parse as JSON: " + checkPoint);
		}
		
		
		uuid=qtm.extractDocidFromCheckpoint(jo,checkPoint);

		System.out.println("uuid vaut "+uuid);
		assertNotNull(uuid);
		assertEquals(uuid, "090000018000e100");
	}
	*/
	
	/*public void testExtractCalendarFromCheckpoint() {

		String checkPoint="{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02 13:58:10\"}";
		JSONObject jo = null;
		Calendar modifDate = null;
		Calendar calDate = null;
		try {
			jo = new JSONObject(checkPoint);
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"checkPoint string does not parse as JSON: " + checkPoint);
		}
		
		modifDate = qtm.extractCalendarFromCheckpoint(jo,checkPoint);
		try{

			calDate=DctmSimpleValue.iso8601ToCalendar("2007-01-02 13:58:10");

		}catch(ParseException pe){
			pe.printStackTrace();	
		}
		
		System.out.println("modifDate vaut "+modifDate);
		
		assertNotNull(modifDate);
		assertEquals(modifDate,calDate);
		assertEquals(modifDate.getTimeInMillis(),calDate.getTimeInMillis());
		
		
	}*/
	
	/*
	public void testIDfetchAndVerifyValueForCheckpoint() throws RepositoryException{
		SimplePropertyMap pm = null;
		pm = new SimplePropertyMap();
		
		try{
		pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_DOCID,
				new DctmSimpleValue(ValueType.STRING, "0900000180010b17")));
		///pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_LASTMODIFY, new DctmSimpleValue(ValueType.DATE, "2007-01-02T14:19:29Z")));
		pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_LASTMODIFY, new DctmSimpleValue(ValueType.DATE, "2007-01-02 14:19:29")));
		}catch(RepositoryException re){
			re.printStackTrace();
		}
		
		
		String uuid = qtm.fetchAndVerifyValueForCheckpoint(pm,
				SpiConstants.PROPNAME_DOCID).getString();
		
		
		System.out.println("id vaut "+uuid);
		
		
		assertEquals(uuid, "0900000180010b17");
	}
	
	
	public void testDatefetchAndVerifyValueForCheckpoint()throws RepositoryException{
		SimplePropertyMap pm = null;
		pm = new SimplePropertyMap();
		Calendar calDate=null;
		try{
		pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_DOCID,
				new DctmSimpleValue(ValueType.STRING, "0900000180010b17")));
		///pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_LASTMODIFY, new DctmSimpleValue(ValueType.DATE, "2007-01-02T14:19:29Z")));
		pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_LASTMODIFY, new DctmSimpleValue(ValueType.DATE, "2007-01-02 14:19:29")));
		}catch(RepositoryException re){
			re.printStackTrace();
		}
		
		Calendar c = qtm.fetchAndVerifyValueForCheckpoint(pm,SpiConstants.PROPNAME_LASTMODIFY).getDate();
		
		try{
			///calDate=DctmSimpleValue.iso8601ToCalendar("2007-01-02T14:19:29Z");
			calDate=DctmSimpleValue.iso8601ToCalendar("2007-01-02 14:19:29");
		}catch(ParseException pe){
				pe.printStackTrace();
		}
		
		System.out.println("date vaut "+c);
		assertEquals(c.getTimeInMillis(), calDate.getTimeInMillis());
		assertEquals(c, calDate);
	}
	
	public void testCheckpoint(){
		
		SimplePropertyMap pm = null;
		String checkPoint=null;
		pm = new SimplePropertyMap();
		try{
		pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_DOCID,
				new DctmSimpleValue(ValueType.STRING, "0900000180010b17")));
		///pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_LASTMODIFY, new DctmSimpleValue(ValueType.DATE, "2007-01-02T14:19:29Z")));
		pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_LASTMODIFY, new DctmSimpleValue(ValueType.DATE, "2007-01-02 14:19:29")));
		}catch(RepositoryException re){
			re.printStackTrace();
		}
		
		try{
			checkPoint=qtm.checkpoint(pm);
		}catch(RepositoryException re){
			re.printStackTrace();
		}
		
		
		//String checkPoint="{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02 13:58:10\"}";
		assertNotNull(checkPoint);
		///assertEquals(checkPoint,"{\"uuid\":\"0900000180010b17\",\"lastModified\":\"2007-01-02T14:19:29.000Z\"}");
		///assertEquals(checkPoint,"{\"uuid\":\"0900000180010b17\",\"lastModified\":\"2007-01-02 14:19:29.000\"}");
		assertEquals(checkPoint,"{\"uuid\":\"0900000180010b17\",\"lastModified\":\"2007-01-02 14:19:29\"}");
	}
	*/
	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmQueryTraversalManager.startTraversal()'
	 */
	
	public void testStartTraversal() {
		ResultSet resultset=null;
		PropertyMap propertyMap=null;
		int counter = 0;
		try {
			resultset = qtm.startTraversal();
			for (Iterator iter = resultset.iterator(); iter.hasNext();) {
		        propertyMap = (PropertyMap) iter.next();
		        logger.info(propertyMap.getProperty(SpiConstants.PROPNAME_DOCID)
		            .getValue().getString());
		        counter++;
		      }
			assertEquals(DmInitialize.DM_RETURN_TOP,counter);


		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	public void testResumeTraversal(){
		ResultSet myResu=null;
		PropertyMap propertyMap=null;
		//String checkPoint="{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02T13:58:10.000Z\"}";
		///String checkPoint="{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02T13:00:00.000Z\"}";
		///String checkPoint="{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02 13:00:00.000\"}";
		String checkPoint="{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02 13:00:00\"}";
		try{
			myResu=qtm.resumeTraversal(checkPoint);
		}catch(RepositoryException re){
			re.printStackTrace();
		}
		int counter = 0;
		try {
			for (Iterator iter = myResu.iterator(); iter.hasNext();) {
		        propertyMap = (PropertyMap) iter.next();
		        logger.info(propertyMap.getProperty(SpiConstants.PROPNAME_DOCID)
		            .getValue().getString());
		        counter++;
		      }
		} catch(RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		assertEquals(12010,counter);
	}

*/
	
	
	/*
	public void testExtractNativeDateFromCheckpoint() {

		String checkPoint="{\"uuid\":\"090000018000e100\",\"lastModified\":\"2007-01-02 13:58:10\"}";
		JSONObject jo = null;
		String modifDate = null;
		try {
			jo = new JSONObject(checkPoint);
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"checkPoint string does not parse as JSON: " + checkPoint);
		}
		
		modifDate = qtm.extractNativeDateFromCheckpoint(jo,checkPoint);
	
		
		System.out.println("modifDate vaut "+modifDate);
		
		assertNotNull(modifDate);
		assertEquals(modifDate,"2007-01-02 13:58:10");
		//assertEquals(modifDate.getTimeInMillis(),calDate.getTimeInMillis());
		
		
	}
*/
	

}
