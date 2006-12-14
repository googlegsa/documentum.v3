package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSession;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.test.QueryTraversalUtil;

public class DctmQueryTraversalUtilCall {
	public static void main(String[] args){
		
		QueryTraversalUtil qtu=new QueryTraversalUtil();
		Session sess=null;
		Connector myconn=null;
		QueryTraversalManager qtm=null;
		
		myconn=new DctmConnector();
		
		try{
			sess=(DctmSession)myconn.login();
		
			qtm= new DctmQueryTraversalManager();
			qtm=(DctmQueryTraversalManager)sess.getQueryTraversalManager(); 
			
			QueryTraversalUtil.runTraversal(qtm, 15);
			
		}catch(LoginException le){
			le.getMessage();
		}catch(RepositoryException re){
			re.getMessage();
		}	
		
		
		
	}
}
