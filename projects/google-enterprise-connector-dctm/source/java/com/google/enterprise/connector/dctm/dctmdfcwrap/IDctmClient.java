package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

public class IDctmClient implements IClient{
	//DfClient dfClient;
	
	
	public IDctmLocalClient getLocalClientEx(){
		IDfClient idfClient=null;
		idfClient=DfClient.getLocalClientEx();
		return new IDctmLocalClient(idfClient);
	}
	
}
