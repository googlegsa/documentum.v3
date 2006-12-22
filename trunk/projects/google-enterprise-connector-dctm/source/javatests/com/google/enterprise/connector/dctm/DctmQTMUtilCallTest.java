package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.dctm.*;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DctmQTMUtilCallTest extends TestCase {

	/**
	 * @param args
	 */
	public static void testmain(String[] args) {
		
		QueryTraversalUtil qtu=new QueryTraversalUtil();
		Session sess=null;
		Connector myconn=null;
		QueryTraversalManager qtm=null;
		
		myconn=new DctmConnector();
		
		try{
			sess=(DctmSession)myconn.login();
		
			qtm=(DctmQueryTraversalManager)sess.getQueryTraversalManager(); 
			
			
			QueryTraversalUtil.runTraversal(qtm, 0);
			
		}catch(LoginException le){
			le.getMessage();
		}catch(RepositoryException re){
			re.getMessage();
		}	
		Assert.assertEquals(qtm.getClass().getName(),"DctmQueryTraversalManager");
	}

}
