package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryException;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfValue;

public class DmCollection implements ICollection {

	IDfCollection idfCollection;

	public DmCollection(IDfCollection idfCollection) {
		this.idfCollection = idfCollection;
	}

	public IValue getValue(String attrName) throws RepositoryException {
		IDfValue dfValue = null;
		try {
			dfValue = idfCollection.getValue(attrName);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
		return new DmValue(dfValue);
	}

	public boolean next() throws RepositoryException {
		boolean rep = false;
		try {
			rep = idfCollection.next();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
		return (rep);
	}

	public IDfCollection getIDfCollection() {
		return idfCollection;
	}

	public String getString(String colName) throws RepositoryException {
		try {
			return this.idfCollection.getString(colName);
		} catch (DfException e) {
			
			throw new RepositoryException(e);
		}
	}
	
	public void close() throws RepositoryException{
		try {
			this.idfCollection.close();
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}
}
