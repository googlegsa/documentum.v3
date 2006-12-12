package com.google.enterprise.connector.dctm.dfcwrap;

public interface IQuery {
	 ICollection execute(ISession session, int queryType); 
}
