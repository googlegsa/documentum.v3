package com.google.enterprise.connector.dctm;


import junit.framework.Test;
import junit.framework.TestSuite;

public class DctmSimplePerfTest {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DctmSimplePerfTest.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for dctmQueryTraversalUtilCall");

		suite.addTestSuite(DctmQueryTraversalUtilCall.class);

		return suite;
	}

}
