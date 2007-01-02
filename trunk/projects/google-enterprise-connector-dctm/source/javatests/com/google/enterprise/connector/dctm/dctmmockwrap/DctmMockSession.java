package com.google.enterprise.connector.dctm.dctmmockwrap;

import javax.jcr.query.QueryManager;

import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.mock.MockRepository;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.mock.MockRepositoryEventList;
import com.google.enterprise.connector.mock.jcr.MockJcrQueryManager;
import com.google.enterprise.connector.mock.jcr.MockJcrRepository;
import com.google.enterprise.connector.mock.jcr.MockJcrSession;

public class DctmMockSession implements ISession {
	
	private MockJcrRepository mockRep;
	private MockJcrSession mockJcrSession;
	
	public DctmMockSession(MockJcrRepository mjR , MockJcrSession mjS){
		this.mockRep = mjR;
		this.mockJcrSession = mjS;
	}
	
	public ISysObject getObjectByQualification(String qualification){
		return null;
	}
	
	public ISysObject getObject(IId objectId){
		MockRepositoryDocument mrD = mockRep.getRepo().getStore().getDocByID(objectId.toString());
		return new DctmMockRepositoryDocument(mrD);
	}
	
	public MockRepositoryDocumentStore getStore(){
		return mockRep.getRepo().getStore();
	}


	public String getSessionId() {
		// TODO Auto-generated method stub
		return null;
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


	

	
	
}
