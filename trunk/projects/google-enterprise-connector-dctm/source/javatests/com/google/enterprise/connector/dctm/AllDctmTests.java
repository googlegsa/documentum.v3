package com.google.enterprise.connector.dctm;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.google.enterprise.connector.dctm.dctmdfcwrap.AllDmTests;

public class AllDctmTests {
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllDmTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.google.enterprise.connector.dctm");
		suite.addTestSuite(DctmAuthenticationManagerTest.class);
		suite.addTestSuite(DctmAuthorizationManagerTest.class);
		suite.addTestSuite(DctmConnectorTest.class);
		suite.addTestSuite(DctmQueryTraversalManagerTest.class);
		suite.addTestSuite(DctmSessionTest.class);
		suite.addTestSuite(DctmSysobjectPropertyMapTest.class);
		suite.addTestSuite(DctmSysobjectValueTest.class);
		suite.addTestSuite(DctmSysobjectIteratorTest.class);
		return suite;
	}
}
