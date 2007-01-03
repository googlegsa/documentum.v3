package com.google.enterprise.connector.dctm.dfcwrap;

import com.documentum.fc.client.IDfQuery;

public interface IQuery {
	
	public static int DF_READ_QUERY = IDfQuery.DF_READ_QUERY; 
	 ICollection execute(ISession session, int queryType);
	 public void setDQL(String dqlStatement);
	 public int getDF_READ_QUERY();
	 public void setDF_READ_QUERY(int df_read_query);
}
