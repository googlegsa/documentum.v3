package com.google.enterprise.connector.dctm;


/*
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

import junit.framework.TestCase;

public class InstanciationTest extends TestCase {
	
	
	public void testAcceptanceTests(){
		boolean errorCaught=false;
		Session session = null;
		Connector connector = null;
		QueryTraversalManager qtm = null;
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin("queryUser");
		((DctmConnector) connector).setPassword("p@ssw0rd");
		((DctmConnector) connector).setDocbase("gsadctm");
		
		((DctmConnector) connector).setClientX("com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		
		
		try {
			session = (DctmSession) connector.login();
			qtm = (DctmQueryTraversalManager) session.getQueryTraversalManager();
			qtm.setBatchHint(1);
		} catch (LoginException le) {
			errorCaught=true;
			System.out.println("Root Cause : " + le.getCause() + " ; Message : " + le.getMessage());
		} catch (RepositoryException re) {
			errorCaught=true;
			System.out.println("Root Cause : " + re.getCause() + " ; Message : " + re.getMessage());
		}
		assertEquals(errorCaught,false);
		
		
		errorCaught=false;
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin("mark");
		((DctmConnector) connector).setPassword("mark");
		((DctmConnector) connector).setDocbase("MockRepositoryEventLog7.txt");
		
		
		((DctmConnector) connector).setClientX("com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClient");
		
		try {
			session = (DctmSession) connector.login();
			qtm = (DctmQueryTraversalManager) session.getQueryTraversalManager();
			qtm.setBatchHint(1);
		} catch (LoginException le) {
			errorCaught=true;
			System.out.println("Root Cause : " + le.getCause() + " ; Message : " + le.getMessage());
		} catch (RepositoryException re) {
			errorCaught=true;
			System.out.println("Root Cause : " + re.getCause() + " ; Message : " + re.getMessage());
		}
		assertEquals(errorCaught,false);
		
		
		errorCaught=false;
		boolean loginExcept=false;
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin("queryUser");
		((DctmConnector) connector).setPassword("passw0rd");//Bad password
		((DctmConnector) connector).setDocbase("gsadctm");
		
		((DctmConnector) connector).setClientX("com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		
		try {
			session = (DctmSession) connector.login();
		} catch (LoginException le) {
			errorCaught=true;
			loginExcept=true;
			System.out.println("Root Cause : " + le.getCause() + " ; Message : " + le.getMessage());
			
		} catch (RepositoryException re) {
			System.out.println("Root Cause : " + re.getCause() + " ; Message : " + re.getMessage());
		}
		assertEquals(errorCaught,true);
		assertEquals(loginExcept,true);
		
		//Eroneous credentials Mock instantiation
		errorCaught=false;
		loginExcept=false;
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin("mark");
		((DctmConnector) connector).setPassword("jean-hubert");//Bad password
		((DctmConnector) connector).setDocbase("MockRepositoryEventLog7.txt");
		
		((DctmConnector) connector).setClientX("com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		
		try {
			session = (DctmSession) connector.login();
		} catch (LoginException le) {
			errorCaught=true;
			loginExcept=true;
			System.out.println("Root Cause : " + le.getCause() + " ; Message : " + le.getMessage());
			
		} catch (RepositoryException re) {
			System.out.println("Root Cause : " + re.getCause() + " ; Message : " + re.getMessage());
		}
		assertEquals(errorCaught,true);
		assertEquals(loginExcept,true);
		
		//Eroneous docbaseName DFC instantiation
		errorCaught=false;
		loginExcept=false;
		boolean repExcept=false;
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin("queryUser");
		((DctmConnector) connector).setPassword("p@ssw0rd");
		((DctmConnector) connector).setDocbase("gzadctm");//docbase does not exist
	
		((DctmConnector) connector).setClientX("com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		
		try {
			session = (DctmSession) connector.login();
		} catch (LoginException le) {
			errorCaught=true;
			loginExcept=true;
			System.out.println("Root Cause : " + le.getCause() + " ; Message : " + le.getMessage());
			
		} catch (RepositoryException re) {
			repExcept=true;
			errorCaught=true;
			System.out.println("Root Cause : " + re.getCause() + " ; Message : " + re.getMessage());
		}
		assertEquals(errorCaught,true);
		assertEquals(loginExcept || repExcept,true);
		
		
		errorCaught=false;
		loginExcept=false;
		repExcept=false;
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin("mark");
		((DctmConnector) connector).setPassword("mark");
		((DctmConnector) connector).setDocbase("erhgdwfgzsd");//docbase does not exist
		
		((DctmConnector) connector).setClientX("com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
		
		try {
			session = (DctmSession) connector.login();
		} catch (LoginException le) {
			errorCaught=true;
			loginExcept=true;
			System.out.println("Root Cause : " + le.getCause() + " ; Message : " + le.getMessage());
			
		} catch (RepositoryException re) {
			repExcept=true;
			errorCaught=true;
			System.out.println("Root Cause : " + re.getCause() + " ; Message : " + re.getMessage());
		}
		assertEquals(errorCaught,true);
		assertEquals(loginExcept,true);
		
		
		errorCaught=false;
		connector = new DctmConnector();
		((DctmConnector) connector).setLogin("user1");
		((DctmConnector) connector).setPassword("p@ssword");
		((DctmConnector) connector).setDocbase("gsadctm");
	
		((DctmConnector) connector).setClientX("com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX");
	
		try {
			session = (DctmSession) connector.login();
		} catch (LoginException le) {
			System.out.println("Root Cause : " + le.getCause() + " ; Message : " + le.getMessage());
			errorCaught=true;
		} catch (RepositoryException re) {
			System.out.println("Root Cause : " + re.getCause() + " ; Message : " + re.getMessage());
			errorCaught=true;
		}
		assertEquals(errorCaught,true);
		
		
	}
	public void performanceTests(){
		
	}
	
}
*/
