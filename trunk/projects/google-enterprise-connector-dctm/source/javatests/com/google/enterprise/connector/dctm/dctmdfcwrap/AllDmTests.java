package com.google.enterprise.connector.dctm.dctmdfcwrap;

import junit.framework.Test;
import junit.framework.TestSuite;
public class AllDmTests {

	public static void main(String[] args) {
		//junit.swingui.TestRunner.run(AllDmTests.class);
		junit.textui.TestRunner.run(AllDmTests.suite());
		//AllDmTests.suite();
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.google.enterprise.connector.dctm.dctmdfcwrap");
		//$JUnit-BEGIN$
		suite.addTestSuite(DmClientXTest.class);
		suite.addTestSuite(DmClientTest.class);
		suite.addTestSuite(DmSessionManagerTest.class);
		suite.addTestSuite(DmSessionTest.class);
		suite.addTestSuite(DmSysObjectTest.class);
		suite.addTestSuite(DmQueryTest.class);
		suite.addTestSuite(DmLoginInfoTest.class);
		suite.addTestSuite(DmFormatTest.class);
		//$JUnit-END$
		return suite;
	}

}
