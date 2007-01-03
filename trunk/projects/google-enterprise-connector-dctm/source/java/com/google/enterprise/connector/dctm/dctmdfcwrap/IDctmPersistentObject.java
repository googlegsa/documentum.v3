package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfTypedObject;
import com.google.enterprise.connector.dctm.dfcwrap.IPersistentObject;

public class IDctmPersistentObject extends IDctmTypedObject implements IPersistentObject{
	IDfPersistentObject idfPersistentObject;
	
	public IDctmPersistentObject(IDfPersistentObject idfPersistentObject){
		super((IDfTypedObject)idfPersistentObject);
		this.idfPersistentObject=idfPersistentObject;
	}
	
}
