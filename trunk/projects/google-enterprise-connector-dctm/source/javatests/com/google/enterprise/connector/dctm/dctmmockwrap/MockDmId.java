package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IId;

public class MockDmId implements IId {
	private String value;
	
	public MockDmId(String paramID){
		this.value=paramID;
	}
	
	public String getValue(){
		return this.value;
	}
}