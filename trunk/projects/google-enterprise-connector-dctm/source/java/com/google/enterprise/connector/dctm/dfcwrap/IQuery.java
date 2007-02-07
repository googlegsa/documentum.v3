package com.google.enterprise.connector.dctm.dfcwrap;

import com.documentum.fc.client.IDfQuery;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;

public interface IQuery {

	public static int DF_READ_QUERY = IDfQuery.DF_READ_QUERY;

	///ICollection execute(ISessionManager sessionManager, int queryType)throws RepositoryException;

	ResultSet execute(ISessionManager sessionManager, int queryType, IClientX clientX)throws RepositoryException;
	
	public void setDQL(String dqlStatement);

}
