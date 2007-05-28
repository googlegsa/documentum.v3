package com.google.enterprise.connector.dctm;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllDctmTests {
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllDctmTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.google.enterprise.connector.dctm");
		// $JUnit-BEGIN$
		suite.addTestSuite(DctmAuthorizationManagerTest.class);
		suite.addTestSuite(DctmSysobjectValueTest.class);
		suite.addTestSuite(DctmTraversalManagerTest.class);
		suite.addTestSuite(DctmAuthenticationManagerTest.class);
		suite.addTestSuite(DctmSessionTest.class);
		suite.addTestSuite(DctmSysobjectPropertyMapTest.class);
		suite.addTestSuite(DctmSysobjectIteratorTest.class);
		suite.addTestSuite(DctmConnectorTest.class);

		// $JUnit-END$
		return suite;
	}

}
