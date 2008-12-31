package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.RepositoryException;

public interface ISessionManager {
	public ISession getSession(String docbase) throws RepositoryLoginException,
			RepositoryException;

	public ISession newSession(String docbase) throws RepositoryLoginException,
			RepositoryException;

	public void setIdentity(String docbase, ILoginInfo identity)
			throws RepositoryLoginException;

	public void release(ISession session);
	
	public void releaseSessionAdd();
	
	public void releaseSessionDel(); 
	
	public void releaseSessionAuto(); 
	
	public void setSessionAdd(ISession sess);
	
	public void setSessionDel(ISession sess);
	
	public void setSessionAuto(ISession sess);

	public void setServerUrl(String serverUrl);

	public String getDocbaseName();

	public void setDocbaseName(String docbaseName);

	public String getServerUrl();

	public ILoginInfo getIdentity(String docbase);

	public boolean authenticate(String docbaseName);

	public void clearIdentity(String docbase);
}
