package com.google.enterprise.connector.dctm.dctmmockwrap;


import junit.framework.Test;
import junit.framework.TestSuite;

public class AllMockTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllMockTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.google.enterprise.connector.dctm.dctmmockwrap");
		// $JUnit-BEGIN$
		suite.addTestSuite(MockDmCollectionTest.class);
		suite.addTestSuite(MockDmClientTest.class);
		suite.addTestSuite(MockDmSessionTest.class);
		suite.addTestSuite(MockDmCollectionTest.class);
		suite.addTestSuite(MockDmFormatTest.class);
		suite.addTestSuite(MockDmIdTest.class);
		suite.addTestSuite(MockDmObjectTest.class);
		suite.addTestSuite(MockDmQueryTest.class);
		suite.addTestSuite(MockDmValueTest.class);
		suite.addTestSuite(MockDmTimeTest.class);
		// $JUnit-END$
		return suite;
	}

}
