package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IId;

public class MockDmId implements IId {

	private String docId;

	public MockDmId(String docid) {
		this.docId = docid;
	}

	public String toString() {
		return docId;
	}

	public String getId() {

		return docId;
	}
}
