package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;

public interface ICollection {

	public static final int DF_WAITING_STATE = 0;

	public static final int DF_READY_STATE = 1;

	public static final int DF_CLOSED_STATE = 2;

	public IValue getValue(String attrName) throws RepositoryException;
	
	public boolean hasNext() throws RepositoryException;

	public boolean next() throws RepositoryException;

	public String getString(String colName) throws RepositoryException;

	public void close() throws RepositoryException;

	public int getState();
	
	public ISession getSession();
	
	public ITime getTime(String colName) throws RepositoryException; 

}
