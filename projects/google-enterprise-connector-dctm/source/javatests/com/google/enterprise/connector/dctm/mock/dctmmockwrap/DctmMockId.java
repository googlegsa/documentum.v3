package com.google.enterprise.connector.dctm.mock.dctmmockwrap;

import com.google.enterprise.connector.dctm.mock.mockwrap.*;

import java.io.ByteArrayInputStream;

//An ID is simply a string for the MockRepository but in order to
//stick to what is done for the DFC, I choosed to implement a specific class for IDs.
public class DctmMockId implements IId {
	private String value;
	public DctmMockId(String paramID){
		this.value=paramID;
	}
	
	public String getValue(){
		return this.value;
	}
}