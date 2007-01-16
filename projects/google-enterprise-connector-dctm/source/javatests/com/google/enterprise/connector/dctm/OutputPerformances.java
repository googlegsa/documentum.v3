package com.google.enterprise.connector.dctm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.*;

import com.documentum.thirdparty.javassist.bytecode.ByteArray;

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
	//private static final Logger root = Logger.getRootLogger();
	
	public static void calibrate(){
		//TODO jpn : process set/end calls without doing anything between the two calls in order to evaluate residual weight of the process
		//Plus : Analyse the influence of the number of elements in the hashtables 
		OutputPerformances.setPerfFlag("LOG1","Free load test #1");
		OutputPerformances.endFlag("LOG1","Free load test #1");
		OutputPerformances.setPerfFlag("LOG2","Free load test #2");
		OutputPerformances.endFlag("LOG2","Free load test #2");
		OutputPerformances.setPerfFlag("LOG1","Free load test #3");
		OutputPerformances.endFlag("LOG1","Free load test #3");
		OutputPerformances.setPerfFlag("LOG2","Free load test #4");
		OutputPerformances.endFlag("LOG2","Free load test #4");
		

		OutputPerformances.setPerfFlag("LOG1","Free load test keeping hashtables #1");
		OutputPerformances.setPerfFlag("LOG2","Free load test keeping hashtables #2");
		OutputPerformances.setPerfFlag("LOG3","Free load test keeping hashtables #3");
		OutputPerformances.setPerfFlag("LOG4","Free load test keeping hashtables #4");
		OutputPerformances.setPerfFlag("LOG5","Free load test keeping hashtables #5");
		OutputPerformances.setPerfFlag("LOG6","Free load test keeping hashtables #6");
		OutputPerformances.setPerfFlag("LOG7","Free load test keeping hashtables #7");
		OutputPerformances.setPerfFlag("LOG8","Free load test keeping hashtables #8");
		OutputPerformances.setPerfFlag("LOG9","Free load test keeping hashtables #9");
		OutputPerformances.setPerfFlag("LOG10","Free load test keeping hashtables #10");
		OutputPerformances.endFlag("LOG10","Free load test keeping hashtables #10");
		OutputPerformances.endFlag("LOG9","Free load test keeping hashtables #1");
		OutputPerformances.endFlag("LOG8","Free load test keeping hashtables #2");
		OutputPerformances.endFlag("LOG1","Free load test keeping hashtables #3");
		OutputPerformances.endFlag("LOG2","Free load test keeping hashtables #4");
		OutputPerformances.endFlag("LOG3","Free load test keeping hashtables #5");
		OutputPerformances.endFlag("LOG4","Free load test keeping hashtables #6");
		OutputPerformances.endFlag("LOG5","Free load test keeping hashtables #7");
		OutputPerformances.endFlag("LOG6","Free load test keeping hashtables #8");
		OutputPerformances.endFlag("LOG7","Free load test keeping hashtables #9");
	}
	
	public static void setPerfFlag(Object loc, String message) {
		//First release memory as far as possible.
		//Running finalization waits till it's done then let's process it prior to speed measurement
		System.runFinalization();
		System.gc();
		Runtime rt = Runtime.getRuntime();
		long free = rt.freeMemory();
		long tot = rt.totalMemory();
		free = (tot-free)/1024;
		Logger curLog = Logger.getLogger(loc.getClass());
		curLog.debug("Begin process : " + message);
		OutputPerformances.memoryFlags.put((Object) loc.getClass().getName(),
				(Object) Long.toString(free));
		
		
		curLog.debug("Begin process : " + message);
		OutputPerformances.velocityFlags.put((Object) loc.getClass().getName(),
				(Object) Long.toString(System.currentTimeMillis()));
	}
	
	public static Object[] endFlag(Object loc, String message){
		long elapsed;
		if (true) {
			long began = Long.parseLong((String) OutputPerformances.velocityFlags.get(loc.getClass().getName()));
			OutputPerformances.velocityFlags.remove(loc.getClass().getName());
			elapsed = System.currentTimeMillis() - began;//Time valuated, speed performances are no longer impacted by local treatment
			Logger curLog = Logger.getLogger(loc.getClass());
			curLog.debug("End process : " + message + "\nTime elapsed : " + elapsed);
		}
		System.runFinalization();
		System.gc();
		Runtime rt = Runtime.getRuntime();
		long free = rt.freeMemory();
		long tot = rt.totalMemory();
		free = (tot-free)/1024;
		long initialMem = Long.parseLong((String) OutputPerformances.memoryFlags.get(loc.getClass().getName()));
		OutputPerformances.memoryFlags.remove(loc.getClass().getName());
		Object[] results = new Object[2];
		results[0]=Long.toString(elapsed);
		long used = free-initialMem;
		results[1]=Long.toString(used);
		Logger curLog = Logger.getLogger(loc.getClass());
		curLog.debug("\n\tMemory required : " + used);
		return results;
	}
}