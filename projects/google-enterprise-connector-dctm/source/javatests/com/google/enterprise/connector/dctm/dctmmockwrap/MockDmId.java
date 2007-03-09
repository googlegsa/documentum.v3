package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.mock.MockRepositoryDocument;

public class MockDmId implements IId {
	
private MockRepositoryDocument mockDocument;
	private String docId;
	
	public MockDmId(String docid) {
		this.docId= docid;
	}

	public String toString(){
		return docId;
	}
}
