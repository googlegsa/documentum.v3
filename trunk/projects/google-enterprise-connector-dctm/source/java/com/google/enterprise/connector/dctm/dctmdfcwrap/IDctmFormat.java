package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.common.DfException;

public class IDctmFormat implements IFormat {
	IDfFormat idfFormat;

	public IDctmFormat(IDfFormat idfFormat) {
		this.idfFormat = idfFormat;
	}

	public boolean canIndex() {
		boolean rep = false;
		try {
			if(idfFormat != null){
				rep = idfFormat.canIndex();
			}
			
		} catch (DfException de) {
			de.getMessage();
		}
		return rep;
	}

	public String getMIMEType() {
		String rep = null;
		try {
			rep = idfFormat.getMIMEType();
		} catch (DfException de) {
			de.getMessage();
		}
		return (rep);
	}
}
