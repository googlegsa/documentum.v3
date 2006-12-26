package com.google.enterprise.connector.dctm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSession;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SimpleProperty;
import com.google.enterprise.connector.dctm.QueryTraversalUtil;
//import com.google.enterprise.connector.test.QueryTraversalUtil;

public class DctmQueryTraversalUtilCall {
	public static void main(String[] args){
		
		QueryTraversalUtil qtu=new QueryTraversalUtil();
		Session sess=null;
		Connector myconn=null;
		QueryTraversalManager qtm=null;
		AuthenticationManager authenticateManager = null;
		AuthorizationManager authoriseManager = null;
		List idList = new ArrayList(3);
		idList.add("0900045780030dae");
		idList.add("0900045780030def");
		idList.add("0900045780030db9");
		myconn=new DctmConnector();
		
		((DctmConnector)myconn).setLogin("emilie");
		((DctmConnector)myconn).setPassword("emilie2");
		//((DctmConnector)myconn).setDocbase("gdoc");
		
		
		
		try{
			sess = (DctmSession) myconn.login();

			
			//qtm= new DctmQueryTraversalManager();n
			//qtm=(DctmQueryTraversalManager)sess.getQueryTraversalManager(); 
			authenticateManager = sess.getAuthenticationManager();
			authenticateManager.authenticate("raph","raphie");
			authenticateManager.authenticate("raph","raph");
			authoriseManager = sess.getAuthorizationManager();
			ResultSet result = authoriseManager.authorizeDocids((List)idList,"raph");
			Iterator iter = result.iterator();
			PropertyMap propMap = null;
			Iterator iterProp = null;
			DctmProperty prop = null;
			while(iter.hasNext()){
				propMap = (PropertyMap) iter.next();
				iterProp = propMap.getProperties();
				while(iterProp.hasNext()){
					prop = (DctmProperty)iterProp.next();
				
					System.out.println("prop " + prop.getName()+ " : " + prop.getValue().getString());
				}
				
			}
			
			//QueryTraversalUtil.runTraversal(qtm, 15);
			
			//sess.getAuthorizationManager();
			
		}catch(LoginException le){
			le.getMessage();
		}catch(RepositoryException re){
			re.getMessage();
		}	
		
		
		
	}
}
