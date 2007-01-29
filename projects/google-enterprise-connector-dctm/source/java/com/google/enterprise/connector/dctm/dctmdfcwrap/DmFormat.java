package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.common.DfException;

public class DmFormat implements IFormat {
	IDfFormat idfFormat;

	public DmFormat(IDfFormat idfFormat) {
		this.idfFormat = idfFormat;
	}

	public boolean canIndex() throws LoginException {
		boolean rep = false;
		try {
			if(idfFormat != null){
				rep = idfFormat.canIndex();
			}else{
				rep = false;
			}
			
		} catch (DfException de) {
			LoginException le = new LoginException(de);
			le.setStackTrace(de.getStackTrace());
			throw le;
		}
		return rep;
	}

	public String getMIMEType() {
		String rep = null;
		try {
			if(idfFormat != null){
				rep = idfFormat.getMIMEType();
			}else{
				rep="";
			}
		} catch (DfException de) {
			de.getMessage();
		}
		return (rep);
	}
}
