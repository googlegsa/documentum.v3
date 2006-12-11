package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.common.IDfValue;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;

public class IDctmValue implements IValue{
	IDfValue dfValue;
	
	public IDctmValue(IDfValue dfValue){
		this.dfValue=dfValue;
	}
}
