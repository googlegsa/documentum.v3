package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;

public class IDctmValue implements IValue{
	IDfValue dfValue;
	
	public IDctmValue(IDfValue dfValue){
		this.dfValue=dfValue;
	}
	
	public String asString(){
		String rep=null;
		rep=dfValue.asString();
		return(rep);
	}
	
	public ITime asTime(){
		IDfTime idfTime=null;
		idfTime=dfValue.asTime();
		return new IDctmTime(idfTime);
	}
}
