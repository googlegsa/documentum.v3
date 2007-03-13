package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.util.Iterator;

import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.mock.MockRepositoryProperty;
import com.google.enterprise.connector.mock.MockRepositoryPropertyList;
import com.google.enterprise.connector.mock.jcr.MockJcrRepository;
import com.google.enterprise.connector.mock.jcr.MockJcrSession;
import com.google.enterprise.connector.spi.RepositoryException;

public class MockDmSession implements ISession {

	private MockJcrRepository mockRep;

	private MockJcrSession mockJcrSession;

	private String sessionFileNameSuffix;

	public MockDmSession(MockJcrRepository mjR, MockJcrSession mjS,
			String dbFileName) {
		this.mockRep = mjR;
		this.mockJcrSession = mjS;
		this.sessionFileNameSuffix = dbFileName;
	}

	public MockRepositoryDocumentStore getStore() {
		return mockRep.getRepo().getStore();
	}

	public String getLoginTicketForUser(String username) {
		// this assumes that Mock authenticated the session by
		// checking username==paswword
		///return mockJcrSession.getUserID();// The only security here is
		return username;
		// inherent to the fact that if
		// authentication failed,
		// Session==null the returning
		// getUserId instead of directly
		// retuning username would throw a
		// nullPointerException
	}

	public String getDocbaseName() {
		return this.sessionFileNameSuffix;
	}

	public ISysObject getObject(IId objectId) throws RepositoryException {
		System.out.println("id vaut "+objectId.toString());
		MockRepositoryDocument myDoc=mockRep.getRepo().getStore().getDocByID(objectId.toString());
		///MockRepositoryDocument myDoc=mockRep.getRepo().getStore().getDocByID("doc2");
		
		///test parcours de la liste
		/*
		MockRepositoryPropertyList pml=myDoc.getProplist();
		System.out.println("In MockDmSession getObject");
		String nom=null;
		String valeur=null;
		for(Iterator myIt=pml.iterator();myIt.hasNext();){
			MockRepositoryProperty myPm=(MockRepositoryProperty)myIt.next();
			nom=myPm.getName();
			System.out.println("le nom vaut "+nom);
			valeur=myPm.getValue();
			System.out.println("la valeur vaut "+valeur);
		}
		*/
		///
		
		MockDmObject dctmMockRepositoryDocument = new MockDmObject(myDoc); 
		return dctmMockRepositoryDocument;
	}	

}
