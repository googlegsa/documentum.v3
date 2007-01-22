package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;


public class IDctmLocalClient implements ILocalClient{
	IDfClient idfClient; 
	
	public IDctmLocalClient(IDfClient idfClient){
		this.idfClient=idfClient;
	}
	
	public ISessionManager newSessionManager(){
		IDfSessionManager dfSessionManager=null;
		dfSessionManager=idfClient.newSessionManager();
		return new IDctmSessionManager(dfSessionManager);
	}
	
//	public ISession findSession(String dfcSessionId) throws RepositoryException{
//		IDfSession dfSession=null;
//		try{
//			dfSession=idfClient.findSession(dfcSessionId);
//		}catch(DfException de){
//			RepositoryException re = new LoginException(de.getMessage(),de.getCause());
//			re.setStackTrace(de.getStackTrace());
//			throw re;
//		}
//		return new IDctmSession(dfSession);
//	}
}
