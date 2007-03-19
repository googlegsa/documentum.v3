package com.google.enterprise.connector.dctm;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClientTest;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmFormatTest;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmIdTest;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmLoginInfoTest;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmObjectTest;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmQueryTest;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmSessionTest;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmTimeTest;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmValueTest;

public class AllMockTests {
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.google.enterprise.connector.dctm");
		// $JUnit-BEGIN$
		suite.addTestSuite(DctmMockAuthorizationManagerTest.class);
		suite.addTestSuite(DctmMockSysobjectValueTest.class);
		suite.addTestSuite(DctmMockQueryTraversalManagerTest.class);
		suite.addTestSuite(DctmMockAuthenticationManagerTest.class);
		suite.addTestSuite(DctmMockSessionTest.class);
		suite.addTestSuite(DctmMockSysobjectPropertyMapTest.class);
		suite.addTestSuite(DctmMockSysobjectIteratorTest.class);
		suite.addTestSuite(DctmMockConnectorTest.class);

		suite.addTestSuite(MockDmClientTest.class);
		suite.addTestSuite(MockDmSessionTest.class);
		suite.addTestSuite(MockDmObjectTest.class);
		suite.addTestSuite(MockDmQueryTest.class);
		suite.addTestSuite(MockDmLoginInfoTest.class);
		suite.addTestSuite(MockDmFormatTest.class);
		suite.addTestSuite(MockDmIdTest.class);
		suite.addTestSuite(MockDmTimeTest.class);
		suite.addTestSuite(MockDmValueTest.class);

		// $JUnit-END$
		return suite;
	}
}
