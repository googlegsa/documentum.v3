package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.common.DfException;

public class DmFormat implements IFormat {

	IDfFormat idfFormat;

	public DmFormat(IDfFormat idfFormat) {
		this.idfFormat = idfFormat;
	}

	public boolean canIndex() throws RepositoryLoginException {
		boolean rep = false;
		try {
			if (idfFormat != null) {
				rep = idfFormat.canIndex();
			} else {
				rep = false;
			}

		} catch (DfException de) {
			RepositoryLoginException le = new RepositoryLoginException(de);
			throw le;
		}
		return rep;
	}

	public String getMIMEType() {
		String rep = null;
		try {
			if (idfFormat != null) {
				rep = idfFormat.getMIMEType();
			} else {
				rep = "application/octet-stream";
			}
		} catch (DfException de) {
			rep = "application/octet-stream";
		}
		return rep;
	}
	
	public String getDOSExtension() {
		String rep = null;
		try {
			if (idfFormat != null) {
				rep = idfFormat.getDOSExtension();
			} 
		} catch (DfException de) {
			rep = "";
		}
		return rep;
	}
	
}
