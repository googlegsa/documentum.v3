package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.common.DfException;

public class IDctmFormat implements IFormat {
	IDfFormat idfFormat;

	public IDctmFormat(IDfFormat idfFormat) {
		this.idfFormat = idfFormat;
	}

	public boolean canIndex() throws RepositoryException {
		boolean rep = false;
		try {
			if(idfFormat != null){
				rep = idfFormat.canIndex();
			}else{
				rep = false;
			}
			
		} catch (DfException de) {
			RepositoryException re = new LoginException(de.getMessage(),de.getCause());
			re.setStackTrace(de.getStackTrace());
			throw re;
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
