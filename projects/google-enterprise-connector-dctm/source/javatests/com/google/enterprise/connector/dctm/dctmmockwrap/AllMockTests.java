package com.google.enterprise.connector.dctm.dctmmockwrap;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllMockTests {

	public static void main(String[] args) {
		AllMockTests.suite();
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.google.enterprise.connector.dctm.dctmmockwrap");
		//$JUnit-BEGIN$
		suite.addTestSuite(MockDmCollectionTest.class);
		suite.addTestSuite(MockDmClientTest.class);
		suite.addTestSuite(MockDmSessionManagerTest.class);
		suite.addTestSuite(MockDmClientXTest.class);
		//$JUnit-END$
		return suite;
	}

}
