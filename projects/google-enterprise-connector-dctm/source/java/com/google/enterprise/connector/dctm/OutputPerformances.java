package com.google.enterprise.connector.dctm;

import java.util.HashMap;

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

	private static HashMap velocityFlags = new HashMap(1, 1);

	private static HashMap memoryFlags = new HashMap(1, 1);

	private static Logger logger;

	static {
		logger = Logger.getLogger(OutputPerformances.class);
		PatternLayout layout = new PatternLayout("%d [%t] %p - %m%n");
		RollingFileAppender appender = null;
		try {

			appender = new RollingFileAppender(layout,
					"logs/outputPerformances.log", false);
			appender.setMaxBackupIndex(10);
			appender.setMaxFileSize("1MB");

		} catch (Exception e) {
			logger.error("[DCTM] " + e.getMessage());
		}
		logger.addAppender(appender);
		logger.setLevel((Level) Level.DEBUG);
	}

	public static void setPerfFlag(String identifier, String message,
			String additionalInfo) {

		if (additionalInfo != null) {
			logger
					.debug("Begin process : " + message + "\n- "
							+ additionalInfo);
		} else {
			logger.debug("Begin process : " + message);
		}

		// First release memory as far as possible.
		// Running finalization waits till it's done then let's process it prior
		// to speed measurement
		System.runFinalization();
		System.gc();
		Runtime rt = Runtime.getRuntime();
		long free = rt.freeMemory();
		long tot = rt.totalMemory();
		free = (tot - free) / 1024;

		// No test are performed to check whether this identifier has been used
		// before. Then use it carefully
		OutputPerformances.memoryFlags.put(identifier, (Object) Long
				.toString(free));
		OutputPerformances.velocityFlags.put(identifier, (Object) Long
				.toString(System.currentTimeMillis()));
	}

	public static void endFlag(String identifier, String message) {
		long elapsed;
		if (true) {
			long began = Long
					.parseLong((String) OutputPerformances.velocityFlags

					.get(identifier));
			OutputPerformances.velocityFlags.remove(identifier);
			elapsed = System.currentTimeMillis() - began;

			// Once time is valuated, speed performances are no longer impacted
			// by local treatment
			logger.debug("End process : " + message + ".");
			logger.debug("Time elapsed : " + elapsed + " ms");

		}
		System.runFinalization();
		System.gc();
		Runtime rt = Runtime.getRuntime();
		long free = rt.freeMemory();
		long tot = rt.totalMemory();
		free = (tot - free) / 1024;
		long initialMem = Long
				.parseLong((String) OutputPerformances.memoryFlags
						.get(identifier));
		OutputPerformances.memoryFlags.remove(identifier);
		long used = free - initialMem;
		logger.debug("Memory spent : " + used + " KBytes");
	}
}