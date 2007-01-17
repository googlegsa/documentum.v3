package com.google.enterprise.connector.dctm;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.google.enterprise.connector.dctm.dctmmockwrap.DctmMockFormat;
import com.google.enterprise.connector.dctm.dctmmockwrap.DctmMockId;
import com.google.enterprise.connector.dctm.dctmmockwrap.DctmMockLoginInfo;
import com.google.enterprise.connector.dctm.dctmmockwrap.DctmMockQuery;
import com.google.enterprise.connector.dctm.dctmmockwrap.DctmMockValue;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class OutputPerformancesTest extends TestCase {
	/**
	 * This is not really a UNITTest. It aims to evaluate prcoessing time and memory costs.
	 */
	public void testCalibrate(){
		PropertyConfigurator.configure("GSALogs.properties");
		OutputPerformances.setPerfFlag(this,"Instantiation cost test");
		String user, password, client, docbase;
		user="user1";
		password="p@ssw0rd";
		client="com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmClient";
		docbase="gsadctm";
		String QUERY_STRING_UNBOUNDED_DEFAULT = "select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document' " +
		"order by r_modify_date, i_chronicle_id ";
		String QUERY_STRING_BOUNDED_DEFAULT = 
			"select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type=''dm_document'' and r_modify_date >= "+ 
			"''{0}'' "+
			"order by r_modify_date, i_chronicle_id";
		Session session = null;
		Connector connector = null;
		QueryTraversalManager qtm = null;		
		connector = new DctmConnector();
		
		((DctmConnector) connector).setLogin(user);
		((DctmConnector) connector).setPassword(password);
		((DctmConnector) connector).setDocbase(docbase);
		((DctmConnector) connector).setClient(client);
		try {
			session = (DctmSession) connector.login();
			qtm = (DctmQueryTraversalManager) session.getQueryTraversalManager();
		} catch (LoginException e) {
			throw new AssertionFailedError("Login exception on post login tests. " +
					"Check initial values. (" + e.getMessage() + " ; " + e.getCause() + ")");
		} catch (RepositoryException e) {
			throw new AssertionFailedError("Repository exception on post instantiation tests. " +
					"Check initial values. (" + e.getMessage() + " ; " + e.getCause() + ")");
		}
		OutputPerformances.endFlag(this,"Instantiation cost test");
		
		String quetalhaido = calibrate();
		assertEquals("com.google.enterprise.connector.dctm.DctmQueryTraversalManager",qtm.getClass().getName());
		assertEquals(quetalhaido,"so far so good");
	}
	

	/**
	 * Class that highlights the effects of the instatiation of theobjects needed to output results on memory and on processing speed
	 */
	public static String calibrate(){
		String ret = "so far so good";
		try{
			Logger tmpLOG = Logger.getLogger(OutputPerformances.class);
			System.runFinalization();
			System.gc();
			tmpLOG.info("********BEGIN - Successive tests - Highlights unreleased objects memory effects********************************************************");
			OutputPerformances.setPerfFlag((new Vector()),"Global load test");
			for (int i=1 ; i<5000 ; i=i+2){
				int j=i+1;
				OutputPerformances.setPerfFlag((new String()),"Free load test #" + i);
				OutputPerformances.endFlag((new String()),"Free load test #" + i);
				OutputPerformances.setPerfFlag((new StringBuffer()),"Free load test #" + j);
				OutputPerformances.endFlag((new StringBuffer()),"Free load test #" + j);
			}
			OutputPerformances.endFlag((new Vector()),"Global load test");
			tmpLOG.info("********END - Successive tests - Highlights unreleased objects memory effects********************************************************\n\n");
			

			tmpLOG.info("********BEGIN - Successive tests - Highlights outputing objects memory amount (typically Hashtables memory cost)********************************************************");
			tmpLOG.info("********BEGIN - Successive tests - Load/Unload test (symetry test)******************************");
			OutputPerformances.setPerfFlag((new String()),"Free load test keeping hashtables #1");
			OutputPerformances.setPerfFlag((new StringBuffer()),"Free load test keeping hashtables #2");
			OutputPerformances.setPerfFlag((new Vector()),"Free load test keeping hashtables #3");
			OutputPerformances.setPerfFlag((new Hashtable()),"Free load test keeping hashtables #4");
			OutputPerformances.setPerfFlag((new DctmMockLoginInfo()),"Free load test keeping hashtables #5");
			OutputPerformances.setPerfFlag((new DctmConnector()),"Free load test keeping hashtables #6");
			OutputPerformances.setPerfFlag((new DctmMockQuery()),"Free load test keeping hashtables #7");
			OutputPerformances.setPerfFlag((new DctmMockValue(null)),"Free load test keeping hashtables #8");
			OutputPerformances.setPerfFlag((new DctmMockFormat()),"Free load test keeping hashtables #9");
			OutputPerformances.setPerfFlag((new DctmMockId("")),"Free load test keeping hashtables #10");
			OutputPerformances.endFlag((new DctmMockId("")),"Free load test keeping hashtables #10");
			OutputPerformances.endFlag((new DctmMockFormat()),"Free load test keeping hashtables #9");
			OutputPerformances.endFlag((new DctmMockValue(null)),"Free load test keeping hashtables #8");
			OutputPerformances.endFlag((new DctmMockQuery()),"Free load test keeping hashtables #7");
			OutputPerformances.endFlag((new DctmConnector()),"Free load test keeping hashtables #6");
			OutputPerformances.endFlag((new DctmMockLoginInfo()),"Free load test keeping hashtables #5");
			OutputPerformances.endFlag((new Hashtable()),"Free load test keeping hashtables #4");
			OutputPerformances.endFlag((new Vector()),"Free load test keeping hashtables #3");
			OutputPerformances.endFlag((new StringBuffer()),"Free load test keeping hashtables #2");
			OutputPerformances.endFlag((new String()),"Free load test keeping hashtables #1");
			tmpLOG.info("********END - Successive tests - Load/Unload test (symetry test)******************************\n\n");
			tmpLOG.info("********BEGIN - Successive tests - Hashtables test******************************");
			Hashtable h = new Hashtable(1,1);
			OutputPerformances.setPerfFlag((new Vector()),"Global load test");
			for (int i=0 ; i<10000 ; i++){
				OutputPerformances.setPerfFlag(new OutputPerformances(),"Iteration "+i);
				if (true) {
					String o = "##" + i;
					long l = System.currentTimeMillis();
					h.put(o,Long.toString(l));
				}
				OutputPerformances.endFlag(new OutputPerformances(),"Iteration "+i);
			}
			OutputPerformances.endFlag((new Vector()),"Global load test - Added 10.000 elements in an Hashtable : ");
			tmpLOG.info("********END - Successive tests - Hashtables test******************************");
			tmpLOG.info("********END - Successive tests - Highlights outputing objects memory amount********************************************************");
		}catch (Exception e){
			ret = e.getMessage();
		}
		return ret;
	}
}
