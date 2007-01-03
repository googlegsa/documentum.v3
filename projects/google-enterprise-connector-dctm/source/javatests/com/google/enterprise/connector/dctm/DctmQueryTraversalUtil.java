package com.google.enterprise.connector.dctm;


import java.util.Iterator;


import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.SpiConstants;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;



public class DctmQueryTraversalUtil {
	
	public static void runTraversal(QueryTraversalManager queryTraversalManager,
			int batchHint) throws RepositoryException {
		
		DctmQueryTraversalManager dctmQTM = (DctmQueryTraversalManager) queryTraversalManager;
		dctmQTM.setBatchHint(batchHint);
		
		ResultSet resultSet = dctmQTM.startTraversal();
//		int nb=resultSet.size();
		//System.out.println("nb vaut "+nb);	
		// The real connector manager will not always start from the beginning.
		// It will start from the beginning if it receives an explicit admin
		// command to do so, or if it thinks that it has never run this connector
		// before. It decides whether it has run a connector before by storing
		// every checkpoint it receives from
		// the connector. If it can find no stored checkpoint, it assumes that
		// it has never run this connector before and starts from the beginning,
		// as here.
		if (resultSet == null) {
			// in this test program, we will stop in this situation. The real
			// connector manager might wait for a while, then try again
			return;
		}
		
		DctmPusher push = new DctmPusher();
		push.setClient(dctmQTM.getClient());
		while (true) {
			int counter = 0;
			
			PropertyMap pm = null;
			for (Iterator iter = resultSet.iterator(); iter.hasNext();) {
				//pm = ;
				counter++;
				
				if (counter == batchHint) {
					System.out.println("counter == batchhint !!!!");
					// this test program only takes batchHint results from each
					// resultSet. The real connector manager may take fewer - for
					// example, if it receives a shutdown request
					//suggestion d'ajout : counter = 0;	
					
					break;
				}else{
					push.take((PropertyMap) iter.next (),"dctm");
				}
				
			}
			
			if (counter == 0) {
				// this test program stops if it receives zero results in a resultSet.
				// the real connector Manager might wait a while, then try again
				break;
			}
			
			String checkPointString = dctmQTM.checkpoint(pm);
							
			resultSet = dctmQTM.resumeTraversal(checkPointString);
			
			// the real connector manager will call checkpoint (as here) as soon
			// as possible after processing the last property map it wants to process.
			// It would then store the checkpoint string it received in persistent
			// store.
			// Unlike here, it might not then immediately turn around and call
			// resumeTraversal. For example, it may have received a shutdown command,
			// so it won't call resumeTraversal again until it starts up again.
			// Or, it may be running this connector on a schedule and there may be a
			// scheduled pause.
		}
	}
	
	
//	public static void main(String[] args) {
//		if (args.length!=4){
//			System.out.println("Illegal number of arguments. <ClientClass_Path> <DocbaseName> <SuperUserLogin> <SuperUserPwd>");
//			return;
//		}		
//		Connector conn=null;
//		try {
//			conn = (DctmConnector)createConnectorInstance(args);
//		} catch (RepositoryException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//		if (conn==null){
//			System.out.println("Connector instance not created correctly. Exiting.");
//			return;
//		}
//		QueryTraversalManager qtm=null;
//		try{
//
//			
//			
//			DctmSession sess = (DctmSession) conn.login();
//			qtm = sess.getQueryTraversalManager();
//			qtm.setBatchHint(5);
//			
//			ResultSet set = qtm.startTraversal();
//
//			Iterator iter = set.iterator();
//			PropertyMap prop;
//			DctmPusher push = new DctmPusher();
//			push.setClient(sess.getClient());
//			System.out.println("dans main" + sess.getClient().getSession().getSessionId());
//			while(iter.hasNext()){
//				prop = (DctmPropertyMap)iter.next();
//				push.take(prop,"dctm");
//			}
//
//			
//		}catch(LoginException le){
//			le.getMessage();
//		}catch(RepositoryException re){
//			re.getMessage();
//		}	
//		//Assert.assertEquals(qtm.getClass().getName(),"DctmQueryTraversalManager");
//	}
//	
	/**
	 * 
	 * @param args
	 * @return
	 * @throws RepositoryException
	 */
//	private static Connector createConnectorInstance(String[] args) throws RepositoryException{
//		DctmConnector currentConnector = new DctmConnector();
//		
//		IClient cl = null;
//		try {
//			cl = (IClient) Class.forName(args[0]).newInstance();
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		if (cl == null) throw new RepositoryException("The path of the client class cannot be resolved.");
//		currentConnector.setClient(cl);
//		currentConnector.setDocbase(args[1]);
//		currentConnector.setLogin(args[2]);
//		currentConnector.setPassword(args[3]);
//		currentConnector.setRepository(args[1]);
//		
//		return currentConnector;
//	}
	
	
	
}
