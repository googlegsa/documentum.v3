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
			suite.addTestSuite(DctmQueryTraversalManagerTest.class);
			suite.addTestSuite(DctmAuthenticationManagerTest.class);
			suite.addTestSuite(DctmSessionTest.class);
			suite.addTestSuite(DctmSysobjectPropertyMapTest.class);
			suite.addTestSuite(DctmSysobjectIteratorTest.class);
			suite.addTestSuite(DctmConnectorTest.class);
			
			// $JUnit-END$
			return suite;
		}

}
