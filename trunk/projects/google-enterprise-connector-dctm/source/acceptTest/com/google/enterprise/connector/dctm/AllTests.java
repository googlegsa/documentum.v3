package com.google.enterprise.connector.dctm;


import com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientATest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientTest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientXATest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientXTest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmFormatATest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmFormatTest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmLoginInfoATest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmLoginInfoTest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmQueryATest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmQueryTest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSessionATest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSessionManagerATest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSessionManagerTest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSessionTest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSysObjectATest;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSysObjectTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.google.enterprise.connector.dctm");
		//$JUnit-BEGIN$
		suite.addTestSuite(DctmAuthorizationManagerTest.class);
		suite.addTestSuite(DctmSysobjectValueTest.class);
		suite.addTestSuite(DctmQueryTraversalManagerTest.class);
		suite.addTestSuite(DctmAuthenticationManagerTest.class);
		suite.addTestSuite(DctmSessionTest.class);
		suite.addTestSuite(DctmSysobjectPropertyMapTest.class);
		suite.addTestSuite(DctmSysobjectIteratorTest.class);
		suite.addTestSuite(DctmConnectorTest.class);
		
		suite.addTestSuite(DmClientXTest.class);
		suite.addTestSuite(DmClientTest.class);
		suite.addTestSuite(DmSessionManagerTest.class);
		suite.addTestSuite(DmSessionTest.class);
		suite.addTestSuite(DmSysObjectTest.class);
		suite.addTestSuite(DmQueryTest.class);
		suite.addTestSuite(DmLoginInfoTest.class);
		suite.addTestSuite(DmFormatTest.class);
		
		suite.addTestSuite(DmClientXATest.class);
		suite.addTestSuite(DmClientATest.class);
		suite.addTestSuite(DmSessionManagerATest.class);
		suite.addTestSuite(DmSessionATest.class);
		suite.addTestSuite(DmSysObjectATest.class);
		suite.addTestSuite(DmQueryATest.class);
		suite.addTestSuite(DmLoginInfoATest.class);
		suite.addTestSuite(DmFormatATest.class);
		
		
		//$JUnit-END$
		return suite;
	}

}
