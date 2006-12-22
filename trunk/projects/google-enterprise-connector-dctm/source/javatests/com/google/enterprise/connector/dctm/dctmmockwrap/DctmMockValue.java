package com.google.enterprise.connector.dctm.dctmmockwrap;

import javax.jcr.Property;

import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;

public class DctmMockValue implements IValue {
	Property property;
	
	public DctmMockValue(Property prop){
		property=prop;
	}

	public String asString() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITime asTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getDataType() {
		// TODO Auto-generated method stub
		return 0;
	}

}
