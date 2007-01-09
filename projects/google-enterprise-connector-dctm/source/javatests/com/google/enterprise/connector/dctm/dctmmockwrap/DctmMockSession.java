package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.mock.jcr.MockJcrRepository;
import com.google.enterprise.connector.mock.jcr.MockJcrSession;

public class DctmMockSession implements ISession {
	
	private MockJcrRepository mockRep;
	private MockJcrSession mockJcrSession;
	private String sessionID;
	
	public DctmMockSession(MockJcrRepository mjR , MockJcrSession mjS, String sessID){
		this.mockRep = mjR;
		this.mockJcrSession = mjS;
		this.sessionID=sessID;
	}
	
	public ISysObject getObject(IId objectId){
		DctmMockId id = (DctmMockId) objectId;
		MockRepositoryDocument mrD = mockRep.getRepo().getStore().getDocByID(id.getValue());
		return new DctmMockRepositoryDocument(mrD);
	}
	
	public MockRepositoryDocumentStore getStore(){
		return mockRep.getRepo().getStore();
	}


	public String getSessionId() {
		return sessionID;
	}

	public String getLoginTicketForUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDocbaseName() {
		mockJcrSession.getRepository();
		return null;
	}

	public MockJcrSession getMockJcrSession() {
		return mockJcrSession;
	}

	public void setMockJcrSession(MockJcrSession mockJcrSession) {
		this.mockJcrSession = mockJcrSession;
	}

	public void setServerUrl(String url) {
		// TODO Auto-generated method stub
		
	}

	public String getServerUrl() {
		// TODO Auto-generated method stub
		return null;
	}


	

	
	
}
