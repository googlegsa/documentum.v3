package com.google.enterprise.connector.dctm.dfcwrap;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmClient;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLocalClient;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSession;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSessionManager;

public interface IClient{
	public IDctmLocalClient getLocalClientEx();
	
}
