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

	private boolean didPeek = false;
	private boolean peekResult;
	private int peekState;

	private static Logger logger = Logger.getLogger(DmCollection.class
			.getName());

	public DmCollection(IDfCollection idfCollection) {
		this.idfCollection = idfCollection;
		numberOfRows = 0;
		didPeek = false;
	}

	public IValue getValue(String attrName) throws RepositoryException {
		if (didPeek) {
			throw new IllegalStateException("Cannot access current row after hasNext()");
		}
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

	/* IDfCollection.DF_NO_MORE_ROWS_STATE is fundamentally broken
	 * as it is not testable from DF_INITIAL_STATE.	 Therefore, we
	 * cannot tell if a IDfCollection as more than zero rows without
	 * calling next() first.	This hasNext() peeks ahead, actually
	 * calling next() under the covers, then caching the result.
	 * This has a serious side effect, in that once hasNext() has
	 * been called, you cannot access data from the "current" row
	 * (as it is no longer "current"), until you actually call next().
	 * At this time, hasNext() is only called at the time the collection
	 * is in DF_INITIAL_STATE, so there is no "current" row.
	 */
	public boolean hasNext() throws RepositoryException {
		if (!didPeek) {
			peekState = getState();
			peekResult = next();
			didPeek = true;
		}
		return peekResult;
	}

	public boolean next() throws RepositoryException {
		if (didPeek) {
			didPeek = false;
			return peekResult;
		}
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
		if (didPeek) {
			throw new IllegalStateException("Cannot access current row after hasNext()");
		}
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
			didPeek = false;
			this.idfCollection.close();
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

	public int getState() {
		int state = (didPeek) ? peekState : this.idfCollection.getState();
		logger.fine("state of the collection : " + state);
		return state;
	}
	
	
	public ISession getSession() {
		IDfSession dfSession = null;
		dfSession = idfCollection.getSession();
		logger.finest("getting the session from the collection");
		return new DmSession(dfSession);
	}
	
	protected void finalize() throws RepositoryException {
		if (getState() != ICollection.DF_CLOSED_STATE) {
			logger.warning("Open DmCollection getting reaped by GC: " + this);
			close();
		}
	}
}
