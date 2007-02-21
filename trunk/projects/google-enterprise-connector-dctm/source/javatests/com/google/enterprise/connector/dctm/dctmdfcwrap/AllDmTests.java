package com.google.enterprise.connector.dctm.dctmdfcwrap;

import junit.framework.Test;
import junit.framework.TestSuite;
public class AllDmTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllDmTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.google.enterprise.connector.dctm.dctmdfcwrap");
		suite.addTestSuite(DmClientXTest.class);
		suite.addTestSuite(DmClientTest.class);
		suite.addTestSuite(DmSessionManagerTest.class);
		suite.addTestSuite(DmSessionTest.class);
		suite.addTestSuite(DmSysObjectTest.class);
		suite.addTestSuite(DmQueryTest.class);
		suite.addTestSuite(DmLoginInfoTest.class);
		suite.addTestSuite(DmFormatTest.class);
		return suite;
	}

}
