package com.google.enterprise.connector.dctm;

import java.io.IOException;
import java.util.Iterator;

import com.google.enterprise.connector.pusher.Pusher;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DctmQTMUtilCallTest {
	
	public static void main(String[] args) {
		if (args.length!=4){
			System.out.println("Illegal number of arguments. <ClientClass_Path> <DocbaseName> <SuperUserLogin> <SuperUserPwd>");
			return;
		}		
		Connector conn=null;
		try {
			conn = createConnectorInstance(args);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		if (conn==null){
			System.out.println("Connector instance not created correctly. Exiting.");
			return;
		}
		QueryTraversalManager qtm=null;
		try{
			DctmSession sess=(DctmSession) conn.login();
			qtm=sess.getQueryTraversalManager();
			qtm.setBatchHint(5);
			
			ResultSet set = qtm.startTraversal();
			Iterator iter = set.iterator();
			PropertyMap prop;
			DctmPusher push = new DctmPusher();
			push.setClient(sess.getClient());
			push.setSession(sess.getSession());
			while(iter.hasNext()){
				prop = (PropertyMap) iter.next();
				push.take(prop,"dctm");
			}
			/*try {
				push.opS.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			
		}catch(LoginException le){
			le.getMessage();
		}catch(RepositoryException re){
			re.getMessage();
		}	
		//Assert.assertEquals(qtm.getClass().getName(),"DctmQueryTraversalManager");
	}
	
	/**
	 * 
	 * @param args
	 * @return
	 * @throws RepositoryException
	 */
	private static Connector createConnectorInstance(String[] args) throws RepositoryException{
		DctmConnector currentConnector = new DctmConnector();
		
		IClient cl = null;
		try {
			cl = (IClient) Class.forName(args[0]).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (cl == null) throw new RepositoryException("The path of the client class cannot be resolved.");
		currentConnector.setClient(cl);
		currentConnector.setDocbase(args[1]);
		currentConnector.setLogin(args[2]);
		currentConnector.setPassword(args[3]);
		currentConnector.setRepository(args[1]);
		
		return currentConnector;
	}
	
}
