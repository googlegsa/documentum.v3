package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.mock.MockRepositoryProperty;

public class MockDmAttr implements IAttr {

	private MockRepositoryProperty mockProp;

	public MockDmAttr(MockRepositoryProperty mockProp) {
		this.mockProp = mockProp;
	}

	public String getName() {

		return mockProp.getName();
	}

	public int getDataType() {
		String type = mockProp.getType().toString();
		if (type.equals("string")) {
			return IAttr.DM_STRING;
		} else if (type.equals("date")) {
			return IAttr.DM_TIME;
		} else if (type.equals("integer")) {
			return IAttr.DM_INTEGER;
		}
		return IAttr.DM_UNDEFINED;
	}

}
