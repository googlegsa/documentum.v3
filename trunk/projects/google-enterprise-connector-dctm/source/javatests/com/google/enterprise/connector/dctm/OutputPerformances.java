package com.google.enterprise.connector.dctm;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.*;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmACL;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmFormat;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmId;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLoginInfo;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmQuery;

/**
 * Class that outputs memory usage and process velocity.
 * gets runtime memory then if other applications are running, the impact may be quite important.
 * Results are relevant only for memory and time orders of magnitude negligible regarding times and
 * memory amounts involved by those outputting methods.
 * This class is for development use only then no tests on null handler will be performed.
 *
 */
public class OutputPerformances {
	
	private static Hashtable velocityFlags = new Hashtable(1,1);
	private static Hashtable memoryFlags = new Hashtable(1,1);
	
	/**
	 * Class that highlights the effects of the instatiation of theobjects needed to output results on memory and on processing speed
	 */
	public static void calibrate(){
		try{
			Logger tmpLOG = Logger.getLogger(OutputPerformances.class);
			System.runFinalization();
			System.gc();
			PropertyConfigurator.configure("GSALogs.properties");
			tmpLOG.info("********BEGIN - Successive tests - Highlights unreleased objects memory effects********************************************************");
			OutputPerformances.setPerfFlag(new DctmInstantiator(),"Global load test");
			for (int i=1 ; i<5000 ; i=i+2){
				int j=i+1;
				OutputPerformances.setPerfFlag((new String()),"Free load test #" + i);
				OutputPerformances.endFlag((new String()),"Free load test #" + i);
				OutputPerformances.setPerfFlag((new StringBuffer()),"Free load test #" + j);
				OutputPerformances.endFlag((new StringBuffer()),"Free load test #" + j);
			}
			OutputPerformances.endFlag(new DctmInstantiator(),"Global load test");
			tmpLOG.info("********END - Successive tests - Highlights unreleased objects memory effects********************************************************\n\n");
			

			tmpLOG.info("********BEGIN - Successive tests - Highlights outputing objects memory amount (typically Hashtables memory cost)********************************************************");
			tmpLOG.info("********BEGIN - Successive tests - Load/Unload test (symetry test)******************************");
			OutputPerformances.setPerfFlag((new String()),"Free load test keeping hashtables #1");
			OutputPerformances.setPerfFlag((new StringBuffer()),"Free load test keeping hashtables #2");
			OutputPerformances.setPerfFlag((new Vector()),"Free load test keeping hashtables #3");
			OutputPerformances.setPerfFlag((new Hashtable()),"Free load test keeping hashtables #4");
			OutputPerformances.setPerfFlag((new IDctmLoginInfo(null)),"Free load test keeping hashtables #5");
			OutputPerformances.setPerfFlag((new DctmConnector()),"Free load test keeping hashtables #6");
			OutputPerformances.setPerfFlag((new IDctmQuery()),"Free load test keeping hashtables #7");
			OutputPerformances.setPerfFlag((new IDctmACL()),"Free load test keeping hashtables #8");
			OutputPerformances.setPerfFlag((new IDctmFormat(null)),"Free load test keeping hashtables #9");
			OutputPerformances.setPerfFlag((new IDctmId("")),"Free load test keeping hashtables #10");
			OutputPerformances.endFlag((new IDctmId("")),"Free load test keeping hashtables #10");
			OutputPerformances.endFlag((new IDctmFormat(null)),"Free load test keeping hashtables #9");
			OutputPerformances.endFlag((new IDctmACL()),"Free load test keeping hashtables #8");
			OutputPerformances.endFlag((new IDctmQuery()),"Free load test keeping hashtables #7");
			OutputPerformances.endFlag((new DctmConnector()),"Free load test keeping hashtables #6");
			OutputPerformances.endFlag((new IDctmLoginInfo(null)),"Free load test keeping hashtables #5");
			OutputPerformances.endFlag((new Hashtable()),"Free load test keeping hashtables #4");
			OutputPerformances.endFlag((new Vector()),"Free load test keeping hashtables #3");
			OutputPerformances.endFlag((new StringBuffer()),"Free load test keeping hashtables #2");
			OutputPerformances.endFlag((new String()),"Free load test keeping hashtables #1");
			tmpLOG.info("********END - Successive tests - Load/Unload test (symetry test)******************************\n\n");
			tmpLOG.info("********BEGIN - Successive tests - Hashtables test******************************");
			Hashtable h = new Hashtable(1,1);
			OutputPerformances.setPerfFlag(new DctmInstantiator(),"Global load test");
			for (int i=0 ; i<10000 ; i++){
				OutputPerformances.setPerfFlag(new OutputPerformances(),"Iteration "+i);
				if (true) {
					String o = "##" + i;
					long l = System.currentTimeMillis();
					h.put(o,Long.toString(l));
				}
				OutputPerformances.endFlag(new OutputPerformances(),"Iteration "+i);
			}
			OutputPerformances.endFlag(new DctmInstantiator(),"Global load test - Added 10.000 elements in an Hashtable : ");
			tmpLOG.info("********END - Successive tests - Hashtables test******************************");
			tmpLOG.info("********END - Successive tests - Highlights outputing objects memory amount********************************************************");
		}catch (Exception e){
			//too bad
		}
	}
	
	public static void setPerfFlag(Object loc, String message) {
		Logger logz = Logger.getLogger(loc.getClass());
		//First release memory as far as possible.
		//Running finalization waits till it's done then let's process it prior to speed measurement
		System.runFinalization();
		System.gc();
		Runtime rt = Runtime.getRuntime();
		long free = rt.freeMemory();
		long tot = rt.totalMemory();
		free = (tot-free)/1024;
		logz.debug("Begin process : " + message);
		OutputPerformances.memoryFlags.put((Object) loc.getClass(),
				(Object) Long.toString(free));
		
		
		OutputPerformances.velocityFlags.put((Object) loc.getClass(),
				(Object) Long.toString(System.currentTimeMillis()));
	}
	
	public static Object[] endFlag(Object loc, String message){
		Logger logz = Logger.getLogger(loc.getClass());
		long elapsed;
		if (true) {
			long began = Long.parseLong((String) OutputPerformances.velocityFlags.get(loc.getClass()));
			OutputPerformances.velocityFlags.remove(loc.getClass());
			elapsed = System.currentTimeMillis() - began;//Time valuated, speed performances are no longer impacted by local treatment
			logz.debug("End process : " + message + ".");
			logz.debug("Time elapsed : " + elapsed + " ms");
		}
		System.runFinalization();
		System.gc();
		Runtime rt = Runtime.getRuntime();
		long free = rt.freeMemory();
		long tot = rt.totalMemory();
		free = (tot-free)/1024;
		long initialMem = Long.parseLong((String) OutputPerformances.memoryFlags.get(loc.getClass()));
		OutputPerformances.memoryFlags.remove(loc.getClass());
		Object[] results = new Object[2];
		results[0]=Long.toString(elapsed);
		long used = free-initialMem;
		results[1]=Long.toString(used);
		logz.debug("Memory spent : " + used + " kO");
		return results;
	}
}