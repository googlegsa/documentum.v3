package com.google.enterprise.connector.dctm.dfcwrap;

public interface ISessionManager{
	 public ISession getSession(String docbase); 
	 public ISession newSession(String docbase);
	 public void setIdentity(String docbase,ILoginInfo identity);
}
