package com.google.enterprise.connector.dctm;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class OutputPerformancesTest extends TestCase {
	/**
	 * This is not really a UNITTest. It aims to evaluate prcoessing time and
	 * memory costs.
	 */

	public void testInstantiation() {

		PropertyConfigurator.configure("GSALogs.properties");

		OutputPerformances.setPerfFlag("azaz", "Instantiation cost test", null);

		String user, password, clientX, docbase;

		user = "user1";
		password = "p@ssw0rd";
		clientX = "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX";
		docbase = "gsadctm";
		Session session = null;
		Connector connector = null;
		QueryTraversalManager qtm = null;
		connector = new DctmConnector();

		((DctmConnector) connector).setLogin(user);
		((DctmConnector) connector).setPassword(password);
		((DctmConnector) connector).setDocbase(docbase);
		((DctmConnector) connector).setClientX(clientX);
		try {
			session = (DctmSession) connector.login();
			qtm = (DctmQueryTraversalManager) session
					.getQueryTraversalManager();
		} catch (LoginException e) {
			throw new AssertionFailedError(
					"Login exception on post login tests. "
							+ "Check initial values. (" + e.getMessage()
							+ " ; " + e.getCause() + ")");
		} catch (RepositoryException e) {
			throw new AssertionFailedError(
					"Repository exception on post instantiation tests. "
							+ "Check initial values. (" + e.getMessage()
							+ " ; " + e.getCause() + ")");
		}

		OutputPerformances.endFlag("azaz", "Instantiation cost test");

		String quetalhaido = calibrate();
		assertEquals(
				"com.google.enterprise.connector.dctm.DctmQueryTraversalManager",
				qtm.getClass().getName());
		assertEquals(quetalhaido, "so far so good");
	}

	/**
	 * Class that highlights the effects of the instatiation of theobjects
	 * needed to output results on memory and on processing speed
	 */
	public static String calibrate() {
		String ret = "so far so good";
		try {
			Logger tmpLOG = Logger.getLogger(OutputPerformances.class);
			System.runFinalization();
			System.gc();
			tmpLOG
					.info("********BEGIN - Successive tests - Highlights unreleased objects memory effects********************************************************");

			OutputPerformances.setPerfFlag("1", "Global load test", null);

			for (int i = 1; i < 5000; i = i + 2) {
				int j = i + 1;

				OutputPerformances.setPerfFlag("2", "Free load test #" + i,
						null);
				OutputPerformances.endFlag("2", "Free load test #" + i);
				OutputPerformances.setPerfFlag("3", "Free load test #" + j,
						null);
				OutputPerformances.endFlag("3", "Free load test #" + j);

			}

			OutputPerformances.endFlag("1", "Global load test");
			tmpLOG
					.info("********END - Successive tests - Highlights unreleased objects memory effects********************************************************\n\n");

			tmpLOG
					.info("********BEGIN - Successive tests - Highlights outputing objects memory amount (typically Hashtables memory cost)********************************************************");
			tmpLOG
					.info("********BEGIN - Successive tests - Load/Unload test (symetry test)******************************");

			OutputPerformances.setPerfFlag("a",
					"Free load test keeping hashtables #1", null);
			OutputPerformances.setPerfFlag("z",
					"Free load test keeping hashtables #2", null);
			OutputPerformances.setPerfFlag("e",
					"Free load test keeping hashtables #3", null);
			OutputPerformances.setPerfFlag("r",
					"Free load test keeping hashtables #4", null);
			OutputPerformances.setPerfFlag("t",
					"Free load test keeping hashtables #5", null);
			OutputPerformances.setPerfFlag("y",
					"Free load test keeping hashtables #6", null);
			OutputPerformances.setPerfFlag("u",
					"Free load test keeping hashtables #7", null);
			OutputPerformances.setPerfFlag("i",
					"Free load test keeping hashtables #8", null);
			OutputPerformances.setPerfFlag("o",
					"Free load test keeping hashtables #9", null);
			OutputPerformances.setPerfFlag("p",
					"Free load test keeping hashtables #10", null);
			OutputPerformances.endFlag("a",
					"Free load test keeping hashtables #10");
			OutputPerformances.endFlag("z",
					"Free load test keeping hashtables #9");
			OutputPerformances.endFlag("e",
					"Free load test keeping hashtables #8");
			OutputPerformances.endFlag("r",
					"Free load test keeping hashtables #7");
			OutputPerformances.endFlag("t",
					"Free load test keeping hashtables #6");
			OutputPerformances.endFlag("y",
					"Free load test keeping hashtables #5");
			OutputPerformances.endFlag("u",
					"Free load test keeping hashtables #4");
			OutputPerformances.endFlag("i",
					"Free load test keeping hashtables #3");
			OutputPerformances.endFlag("o",
					"Free load test keeping hashtables #2");
			OutputPerformances.endFlag("p",
					"Free load test keeping hashtables #1");

			tmpLOG
					.info("********END - Successive tests - Load/Unload test (symetry test)******************************\n\n");
			tmpLOG
					.info("********BEGIN - Successive tests - Hashtables test******************************");

			HashMap h = new HashMap(1, 1);
			OutputPerformances.setPerfFlag("qskdjfbilqsdufbiqs",
					"Global load test", "fQDFHOSDUQSDOPFQSDOFHQUISD");

			for (int i = 0; i < 10000; i++) {

				OutputPerformances.setPerfFlag("snbfklqshdg" + i, "Iteration "
						+ i, "qsdjfhomisd^qsdjiof^qsdihfôqsdijfqsdoi");

				if (true) {
					String o = "##" + i;
					long l = System.currentTimeMillis();
					h.put(o, Long.toString(l));
				}

				OutputPerformances.endFlag("snbfklqshdg" + i, "Iteration " + i);

			}

			OutputPerformances
					.endFlag("qskdjfbilqsdufbiqs",
							"Global load test - Added 10.000 elements in an Hashtable : ");
			tmpLOG
					.info("********END - Successive tests - Hashtables test******************************");
			tmpLOG
					.info("********END - Successive tests - Highlights outputing objects memory amount********************************************************");
		} catch (Exception e) {

			ret = e.getMessage();
		}
		return ret;
	}
}
