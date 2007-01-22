package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.mock.jcr.MockJcrRepository;
import com.google.enterprise.connector.mock.jcr.MockJcrSession;

public class DctmMockSession implements ISession {
	
	private MockJcrRepository mockRep;
	private MockJcrSession mockJcrSession;
//	private String sessionID;
	private String sessionFileNameSuffix;
	
	public DctmMockSession(MockJcrRepository mjR , MockJcrSession mjS, String dbFileName){
		this.mockRep = mjR;
		this.mockJcrSession = mjS;
//		this.sessionID=sessID;
		this.sessionFileNameSuffix = dbFileName;
	}
	
	public MockRepositoryDocumentStore getStore(){
		return mockRep.getRepo().getStore();
	}


//	public String getSessionId() {
//		return sessionID;
//	}

	public String getLoginTicketForUser(String username) {
		//this assumes that Mock authenticated the session by
		//checking username==paswword (which was the case at least till
		//2007/01/19 but it is so aukward that I thought it could
		//be changed anytime...)
		return mockJcrSession.getUserID();//The only security here is inherent to the fact that if authentication failed, Session==null the returning getUserId instead of directly retuning username would throw a nullPointerException
	}

	public String getDocbaseName() {
		return this.sessionFileNameSuffix;
	}

	public MockJcrSession getMockJcrSession() {
		return mockJcrSession;
	}

	public void setMockJcrSession(MockJcrSession mockJcrSession) {
		this.mockJcrSession = mockJcrSession;
	}


	

	
	
}
