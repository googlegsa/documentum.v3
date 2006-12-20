package com.google.enterprise.connector.dctm.dfcwrap;

public interface IQuery {
	 ICollection execute(ISession session, int queryType);
	 public void setDQL(String dqlStatement);
	 public int getDF_READ_QUERY();
	 public void setDF_READ_QUERY(int df_read_query);
}
