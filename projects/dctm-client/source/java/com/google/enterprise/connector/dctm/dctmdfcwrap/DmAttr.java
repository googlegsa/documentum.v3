package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.common.IDfAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IAttr;

public class DmAttr implements IAttr {
	IDfAttr idfAttr;


	public DmAttr(IDfAttr idfAttr) {
		this.idfAttr = idfAttr;
	}

	public String getName() {
		
		return idfAttr.getName();
	}

}
