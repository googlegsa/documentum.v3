package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfType;
import com.google.enterprise.connector.dctm.dfcwrap.IType;

public class DmType implements IType {
	
	private IDfType idfType;

	public DmType(IDfType idfType) {
		this.idfType = idfType;

	}

}
