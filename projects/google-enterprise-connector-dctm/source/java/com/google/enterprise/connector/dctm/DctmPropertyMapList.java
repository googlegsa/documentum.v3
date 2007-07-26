package com.google.enterprise.connector.dctm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.PropertyMapList;

public class DctmPropertyMapList extends LinkedList implements PropertyMapList {

	private static final long serialVersionUID = 9081981L;

	ICollection collec;

	IClientX clientX;

	ISessionManager sessMag;

	private boolean isPublic;

	private HashSet included_meta;

	private HashSet excluded_meta;

	public DctmPropertyMapList() {
		super();
	}

	public DctmPropertyMapList(ICollection co, ISessionManager sessMag,
			IClientX clientX, boolean isPublic, HashSet included_meta,
			HashSet excluded_meta) {
		this.collec = co;
		this.clientX = clientX;
		this.sessMag = sessMag;
		this.isPublic = isPublic;
		this.included_meta = included_meta;
		this.excluded_meta = excluded_meta;
	}

	public Iterator iterator() {
		DctmSysobjectIterator iterator = new DctmSysobjectIterator(collec,
				sessMag, clientX, isPublic, included_meta, excluded_meta);
		return iterator;
	}

}
