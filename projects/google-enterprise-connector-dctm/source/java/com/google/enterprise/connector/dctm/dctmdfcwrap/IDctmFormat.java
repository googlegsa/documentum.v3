package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.documentum.fc.client.IDfFormat;

public class IDctmFormat implements IFormat{
	IDfFormat DfFormat;
	
	public IDctmFormat (IDfFormat DfFormat){
		this.DfFormat=DfFormat;
	}
}
