package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;

public interface IQuery {

	public static int READ_QUERY = 0;

	ICollection execute(ISessionManager sessionManager, int queryType)
			throws RepositoryException;

	public void setDQL(String dqlStatement);

}
