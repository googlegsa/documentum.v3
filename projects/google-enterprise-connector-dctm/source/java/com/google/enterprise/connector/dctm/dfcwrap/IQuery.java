package com.google.enterprise.connector.dctm.dfcwrap;

import com.documentum.fc.client.IDfQuery;
import com.google.enterprise.connector.spi.RepositoryException;

public interface IQuery {

	public static int DF_READ_QUERY = IDfQuery.DF_READ_QUERY;

	ICollection execute(ISessionManager sessionManager, int queryType)
			throws RepositoryException;

	public void setDQL(String dqlStatement);
	// public int getDF_READ_QUERY();
	// public void setDF_READ_QUERY(int df_read_query);
}
