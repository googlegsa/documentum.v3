package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;

public class IDctmValue implements IValue{
	IDfValue idfValue;
	
	public IDctmValue(IDfValue idfValue){
		this.idfValue=idfValue;
	}
	
	public String asString(){
		String rep=null;
		rep=idfValue.asString();
		return(rep);
	}
	
	public ITime asTime(){
		IDfTime idfTime=null;
		idfTime=idfValue.asTime();
		return new IDctmTime(idfTime);
	}
	
	public int getDataType(){
		int rep=0;
		rep=idfValue.getDataType();
		return(rep);
	}
}
