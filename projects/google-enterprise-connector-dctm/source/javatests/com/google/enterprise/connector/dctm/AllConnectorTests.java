package com.google.enterprise.connector.dctm;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllConnectorTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllConnectorTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.google.enterprise.connector.dctm");
		//$JUnit-BEGIN$
		suite.addTestSuite(DctmSessionTest.class);
		suite.addTestSuite(DctmSysobjectValueTest.class);
		suite.addTestSuite(DctmAuthorizationManagerTest.class);
		suite.addTestSuite(QueryTraverserTest.class);
		suite.addTestSuite(DctmConnectorTest.class);
		suite.addTestSuite(DctmQueryTraversalManagerTest.class);
		suite.addTestSuite(DctmAuthenticationManagerTest.class);
		suite.addTestSuite(DctmSimpleValueTest.class);
		//$JUnit-END$
		return suite;
	}

}
