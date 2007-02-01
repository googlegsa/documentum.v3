package com.google.enterprise.connector.dctm;

import java.util.Hashtable;

import org.apache.log4j.*;

/**
 * Class that outputs memory usage and process velocity. gets runtime memory
 * then if other applications are running, the impact may be quite important.
 * Results are relevant only for memory and time orders of magnitude negligible
 * regarding times and memory amounts involved by those outputting methods. This
 * class is for development use only then no tests on null handler will be
 * performed.
 * 
 */
public class OutputPerformances {

	private static Hashtable velocityFlags = new Hashtable(1, 1);

	private static Hashtable memoryFlags = new Hashtable(1, 1);
	private static Logger logz ;
	
	static {
		logz = Logger.getLogger(OutputPerformances.class);
		PatternLayout layout = new PatternLayout("%d [%t] %p - %m%n");
		RollingFileAppender appender = null;
		try {
			appender = new RollingFileAppender(layout, "logs/outputPerformances.log", false);
			appender.setMaxBackupIndex(1);
		} catch (Exception e) {
			logz.error("[DCTM] " + e.getMessage());
		}
		logz.addAppender(appender);
		logz.setLevel((Level) Level.DEBUG);
	}

	public static void setPerfFlag(Object loc, String message) {
		
		// First release memory as far as possible.
		// Running finalization waits till it's done then let's process it prior
		// to speed measurement
		System.runFinalization();
		System.gc();
		Runtime rt = Runtime.getRuntime();
		long free = rt.freeMemory();
		long tot = rt.totalMemory();
		free = (tot - free) / 1024;
		logz.debug("Begin process : " + message);
		OutputPerformances.memoryFlags.put((Object) loc.getClass(),
				(Object) Long.toString(free));

		OutputPerformances.velocityFlags.put((Object) loc.getClass(),
				(Object) Long.toString(System.currentTimeMillis()));
	}

	public static Object[] endFlag(Object loc, String message) {
		Logger logz = Logger.getLogger(loc.getClass());
		long elapsed;
		if (true) {
			long began = Long
					.parseLong((String) OutputPerformances.velocityFlags
							.get(loc.getClass()));
			OutputPerformances.velocityFlags.remove(loc.getClass());
			elapsed = System.currentTimeMillis() - began;// Time valuated,
															// speed
															// performances are
															// no longer
															// impacted by local
															// treatment
			logz.debug("End process : " + message + ".");
			logz.debug("Time elapsed : " + elapsed + " ms");
		}
		System.runFinalization();
		System.gc();
		Runtime rt = Runtime.getRuntime();
		long free = rt.freeMemory();
		long tot = rt.totalMemory();
		free = (tot - free) / 1024;
		long initialMem = Long
				.parseLong((String) OutputPerformances.memoryFlags.get(loc
						.getClass()));
		OutputPerformances.memoryFlags.remove(loc.getClass());
		Object[] results = new Object[2];
		results[0] = Long.toString(elapsed);
		long used = free - initialMem;
		results[1] = Long.toString(used);
		logz.debug("Memory spent : " + used + " Ko");
		return results;
	}
}