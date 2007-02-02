package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;

public interface ICollection {
	public IValue getValue(String attrName) throws RepositoryException;

	public boolean next() throws RepositoryException;

	public String getString(String colName) throws RepositoryException;

	public ResultSet buildResulSetFromCollection(
			ISessionManager sessionManager, IClientX clientX)
			throws RepositoryException;

}
