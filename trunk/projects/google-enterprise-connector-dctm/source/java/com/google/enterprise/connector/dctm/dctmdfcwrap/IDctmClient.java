package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;

public class IDctmClient implements IClient{
	//DfClient dfClient;
	
	public ILocalClient getLocalClientEx(){
		IDfClient idfClient=null;
		idfClient=DfClient.getLocalClientEx();
		return new IDctmLocalClient(idfClient);
	}
	
}
