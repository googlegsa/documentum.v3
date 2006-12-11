package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;

public class IDctmLocalClient implements ILocalClient{
	IDfClient idfClient; 
	
	public IDctmLocalClient(IDfClient idfClient){
		this.idfClient=idfClient;
	}
	
	public IDctmSessionManager newSessionManager(){
		IDfSessionManager dfSessionManager=null;
		dfSessionManager=idfClient.newSessionManager();
		return new IDctmSessionManager(dfSessionManager);
	}
}
