package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ITypedObject;

public class IDctmTypedObject implements ITypedObject{
	IDfTypedObject idfTypedObject;
	
	public IDctmTypedObject(IDfTypedObject idfTypedObject){
		this.idfTypedObject=idfTypedObject;
	}
	
	
	public IId getObjectId(){
		IDfId idfId=null;
		try{
			idfId=idfTypedObject.getObjectId();
		}catch(DfException de){
			de.getMessage();
		}
		return new IDctmId(idfId);
	}
}
