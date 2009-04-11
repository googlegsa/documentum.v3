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

	public String getName() {
		String rep;
		try {
			if (idfFormat != null) {
				rep = idfFormat.getName();
			} else {
				rep = "";
			}

		} catch (DfException de) {
			rep = "";
		}
		return rep;
	}

	/** 
	 * Gets whether this format is configured for indexing on the
	 * server. If the answer isn't definitive, err on the side of
	 * feeding the content.
	 */
	public boolean canIndex() {
		boolean rep;
		try {
			if (idfFormat != null) {
				rep = idfFormat.canIndex();
			} else {
				rep = true;
			}

		} catch (DfException de) {
			rep = true;
		}
		return rep;
	}

	public String getMIMEType() {
		String rep;
		try {
			if (idfFormat != null) {
				rep = idfFormat.getMIMEType();
				if (rep == null || rep.length() == 0)
					rep = "application/octet-stream";
			} else {
				rep = "application/octet-stream";
			}
		} catch (DfException de) {
			rep = "application/octet-stream";
		}
		return rep;
	}
	
	public String getDOSExtension() {
		String rep;
		try {
			if (idfFormat != null) {
				rep = idfFormat.getDOSExtension();
			} else {
				rep = "";
			}
		} catch (DfException de) {
			rep = "";
		}
		return rep;
	}
	
}
