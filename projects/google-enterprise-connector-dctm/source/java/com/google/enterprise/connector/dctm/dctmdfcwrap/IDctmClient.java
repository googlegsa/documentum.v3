package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;

public class IDctmClient implements IClient{
	//DfClient dfClient;
	
	public ILocalClient getLocalClientEx(){
		IDfClient idfClient=null;
		idfClient=DfClient.getLocalClientEx();
		return new IDctmLocalClient(idfClient);
	}
	
	public IQuery getQuery(){
		return new IDctmQuery(new DfQuery());
	}
	
}
