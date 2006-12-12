package com.google.enterprise.connector.dctm.dfcwrap;

public interface ISessionManager{
	 ISession getSession(String docbase); 
	 
	 public void setIdentity(String docbase,ILoginInfo identity);
}
