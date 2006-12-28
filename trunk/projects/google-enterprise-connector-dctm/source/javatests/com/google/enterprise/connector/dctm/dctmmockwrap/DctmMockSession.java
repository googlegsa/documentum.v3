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

public class DctmMockSession implements ISession {
	
	private MockRepository mockRep;
	private MockRepositoryEventList mrel;
	private QueryManager qm;
	
	public ISysObject getObjectByQualification(String qualification){
		return null;
	}
	
	public ISysObject getObject(IId objectId){
		MockRepositoryDocument mrD = mockRep.getStore().getDocByID(objectId.toString());
		return new DctmMockRepositoryDocument(mrD);
	}
	
	public MockRepositoryDocumentStore getStore(){
		return mockRep.getStore();
	}
<<<<<<< .mine

	public String getSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLoginTicketForUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDocbaseName() {
		// TODO Auto-generated method stub
		return null;
	}
=======

	public String getLoginTicketForUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDocbaseName() {
//		 TODO Auto-generated method stub
		return null;
	}

	public String getSessionId() {
		// TODO Auto-generated method stub
		return null;
	}
>>>>>>> .r76
}
