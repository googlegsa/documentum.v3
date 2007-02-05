package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfTypedObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITypedObject;

public class DmTypedObject implements ITypedObject {

	IDfTypedObject idfTypedObject;

	public DmTypedObject(IDfTypedObject idfTypedObject) {
		this.idfTypedObject = idfTypedObject;

	}

}
