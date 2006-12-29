package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.mock.MockRepository;
import com.google.enterprise.connector.mock.jcr.MockJcrRepository;

public class DctmMockClient implements IClient, ILocalClient {

	
	private MockRepository mockRep;
	private MockJcrRepository mockJcrRep;

	private DctmMockQuery query;
	
	DctmMockClient(MockJcrRepository mock, DctmMockQuery query){
		this.mockJcrRep = mock;
		this.query = query;
	}
	
	public ILocalClient getLocalClientEx(){
		return this;
	}
	
	public ISessionManager newSessionManager(){
		return new DctmMockSessionManager();
	}

	
	public IQuery getQuery() {
		return query;
	}

	
	

	public void authenticate(String docbaseName, ILoginInfo loginInfo) {
		// TODO Auto-generated method stub
		
	}

	public ISession newSession(String string, ILoginInfo logInfo) {
		
		return null;
	}

	

	public ISession findSession(String dfcSessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public MockRepository getMockRep() {
		return mockRep;
	}

	public String getSessionForUser(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setMockRep(MockRepository mockRep) {
		this.mockRep = mockRep;
	}


	public void setQuery(DctmMockQuery query) {
		this.query = query;
	}

	public ILoginInfo getLoginInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public IId getId(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
