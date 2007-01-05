package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.LoginException;

public interface ISessionManager{
	 public ISession getSession(String docbase) throws LoginException; 
	 public ISession newSession(String docbase) throws LoginException;
	 public void setIdentity(String docbase,ILoginInfo identity) throws LoginException;
}
