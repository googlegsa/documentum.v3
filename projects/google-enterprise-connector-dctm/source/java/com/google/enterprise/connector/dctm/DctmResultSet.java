package com.google.enterprise.connector.dctm;

import java.util.Iterator;
import java.util.LinkedList;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.ResultSet;

public class DctmResultSet extends LinkedList implements ResultSet {

	private static final long serialVersionUID = 9081981L;

	ICollection collec;

	IClientX clientX;

	ISessionManager sessMag;

	public DctmResultSet() {
		super();
	}

	public DctmResultSet(ICollection co, ISessionManager sessMag,
			IClientX clientX) {
		this.collec = co;
		this.clientX = clientX;
		this.sessMag = sessMag;
	}

	public Iterator iterator() {
		DctmSysobjectIterator iterator = new DctmSysobjectIterator(collec,
				sessMag, clientX);
		return iterator;
	}

}
