package com.google.enterprise.connector.dctm;

import java.util.Iterator;
import java.util.LinkedList;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.PropertyMapList;

public class DctmResultSet extends LinkedList implements PropertyMapList {

	private static final long serialVersionUID = 9081981L;

	ICollection collec;

	IClientX clientX;

	ISessionManager sessMag;

	private boolean isPublic;

	public DctmResultSet() {
		super();
	}

	public DctmResultSet(ICollection co, ISessionManager sessMag,
			IClientX clientX, boolean isPublic) {
		this.collec = co;
		this.clientX = clientX;
		this.sessMag = sessMag;
		this.isPublic = isPublic;
	}

	public Iterator iterator() {
		DctmSysobjectIterator iterator = new DctmSysobjectIterator(collec,
				sessMag, clientX, isPublic);
		return iterator;
	}

}
