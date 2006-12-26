package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

public class DctmMockClient implements IClient, ILocalClient {

	public ILocalClient getLocalClientEx(){
		return this;
	}
	
	public ISessionManager newSessionManager(){
		return new DctmMockSessionManager();
	}

	public String getSessionForUser(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	public IQuery getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	public ISession findSession(String dfcSessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public ISession newSession(String string, ILoginInfo logInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	public void authenticate(String docbaseName, ILoginInfo loginInfo) {
		// TODO Auto-generated method stub
		
	}
}
