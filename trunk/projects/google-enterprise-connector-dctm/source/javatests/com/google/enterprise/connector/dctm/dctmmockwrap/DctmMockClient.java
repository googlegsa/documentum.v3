package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

public class DctmMockClient implements IClient, ILocalClient {

	public ILocalClient getLocalClientEx(){
		return this;
	}
	
	public ISessionManager newSessionManager(){
		return new DctmMockSessionManager();
	}
}
