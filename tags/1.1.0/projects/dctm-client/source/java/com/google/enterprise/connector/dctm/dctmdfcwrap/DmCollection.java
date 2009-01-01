package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.util.logging.Logger;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryException;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;

public class DmCollection implements ICollection {

	IDfCollection idfCollection;

	int numberOfRows = 0;

	private static Logger logger = Logger.getLogger(DmCollection.class
			.getName());

	public DmCollection(IDfCollection idfCollection) {
		this.idfCollection = idfCollection;
		numberOfRows = 0;
	}

	public IValue getValue(String attrName) throws RepositoryException {
		IDfValue dfValue = null;
		try {
			dfValue = idfCollection.getValue(attrName);
			logger.finest("getting the value of attribute "+attrName);
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
			logger.finest("collection.next() returns "+rep);
		} catch (DfException e) {
			e.printStackTrace();
			throw new RepositoryException(e);
		}
		if (rep)
			numberOfRows++;
		logger.finest("number Of Rows is "+numberOfRows);
		return rep;
	}

	public IDfCollection getIDfCollection() {
		return idfCollection;
	}

	public String getString(String colName) throws RepositoryException {
		try {
			logger.finest("column name is "+this.idfCollection.getString(colName));
			return this.idfCollection.getString(colName);
		} catch (DfException e) {

			throw new RepositoryException(e);
		}
	}
	
	public ITime getTime(String colName) throws RepositoryException {
		IDfTime dfTime = null;
		try {
			logger.finest("column name is "+this.idfCollection.getTime(colName));
			dfTime = this.idfCollection.getTime(colName);
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
		return new DmTime(dfTime);
	}

	public void close() throws RepositoryException {
		logger.info(numberOfRows + " documents have been processed");
		try {
			this.idfCollection.close();
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

	public int getState() {
		logger.fine("state of the collection : "+this.idfCollection.getState());
		return this.idfCollection.getState();
	}
	
	
	public ISession getSession(){
		IDfSession dfSession = null;
		dfSession = idfCollection.getSession();
		logger.finest("getting the session from the collection");
		return new DmSession(dfSession);
	}
	
}
