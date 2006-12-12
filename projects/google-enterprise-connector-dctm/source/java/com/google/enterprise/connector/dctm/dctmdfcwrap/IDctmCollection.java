package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfValue;

public class IDctmCollection implements ICollection{
	IDfCollection idfCollection;
	
	public IDctmCollection(IDfCollection idfCollection){
		this.idfCollection=idfCollection;
	}
	
	public IValue getValue(String attrName){
		IDfValue dfValue=null;
		try{
			dfValue=idfCollection.getValue(attrName);
		}catch(DfException de){
			de.getMessage();
		}
		return new IDctmValue(dfValue);
	}
	
}
