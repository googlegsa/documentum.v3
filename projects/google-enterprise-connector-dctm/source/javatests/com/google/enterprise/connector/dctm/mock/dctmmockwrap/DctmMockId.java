package com.google.enterprise.connector.dctm.mock.dctmmockwrap;

import com.google.enterprise.connector.dctm.mock.mockwrap.IId;

public class DctmMockId implements IId {
	private String value;
	
	public DctmMockId(String paramID){
		this.value=paramID;
	}
	
	public String getValue(){
		return this.value;
	}
}