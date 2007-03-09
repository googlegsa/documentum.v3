package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryProperty;

public class MockDmAttr implements IAttr {

private MockRepositoryProperty mockProp;
	
	public MockDmAttr(MockRepositoryProperty mockProp) {
		this.mockProp = mockProp;
	}
	
	public String getName() {
		String propName=mockProp.getName();
		return propName;
	}

}
