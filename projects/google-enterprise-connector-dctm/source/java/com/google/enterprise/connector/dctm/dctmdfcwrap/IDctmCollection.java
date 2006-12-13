package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ITypedObject;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfValue;

public class IDctmCollection extends IDctmTypedObject implements ICollection{
	IDfCollection idfCollection;
	
	public IDctmCollection(IDfCollection idfCollection){
		super(idfCollection);
		
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
	
	public boolean next(){
		boolean rep=false;
		try{
			rep =idfCollection.next();
		}catch(DfException de){
			de.getMessage();
		}
		return(rep);
	}
	
	public ITypedObject getTypedObject(){
		IDfTypedObject dfTypedObj=null;
		try{
			dfTypedObj=idfCollection.getTypedObject();
		}catch(DfException de){
			de.getMessage();
		}
		return new IDctmTypedObject(dfTypedObj);
	}
	
	public IDfCollection getIDfCollection(){
		return idfCollection;
	}
}
