package com.google.enterprise.connector.dctm;

import java.util.Iterator;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;

public class DctmSysobjectIterator implements Iterator {
	ICollection co;

	ISessionManager sessMag;

	IClientX clientX;

	int index;

	DctmSysobjectIterator(ICollection co, ISessionManager sessMag,
			IClientX clientX) {
		this.co = co;
		this.index = 0;
		this.sessMag = sessMag;
		this.clientX = clientX;
	}

	public void remove() {
		// TODO Auto-generated method stub

	}

	public boolean hasNext() {
		System.out.println("boolean hasNext");
		boolean hasNextVal = false;
		try {
			hasNextVal = co.next();
		} catch (RepositoryException e) {
			return false;
		}
		return hasNextVal;
	}

	public Object next() {
		String crID = "";
		try {
			crID = co.getValue("r_object_id").asString();
		} catch (RepositoryException e) {
			return null;
		}
		return new DctmSysobjectPropertyMap(crID, sessMag, clientX);
	}
}
