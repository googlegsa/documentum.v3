package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dctmdfcwrap.AllDmTests;
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

public class AllAcceptTests {
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllAcceptTests.suite());
		
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.google.enterprise.connector.dctm");
		//$JUnit-BEGIN$
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
