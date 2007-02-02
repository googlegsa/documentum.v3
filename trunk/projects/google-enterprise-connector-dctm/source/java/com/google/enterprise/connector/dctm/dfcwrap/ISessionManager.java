package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;

public interface ISessionManager {
	public ISession getSession(String docbase) throws LoginException,
			RepositoryException;

	public ISession newSession(String docbase) throws LoginException,
			RepositoryException;

	public void setIdentity(String docbase, ILoginInfo identity)
			throws LoginException;

	public void release(ISession session);

	public void setServerUrl(String serverUrl);

	public String getDocbaseName();

	public void setDocbaseName(String docbaseName);

	public String getServerUrl();

	public ILoginInfo getIdentity(String docbase);

	public boolean authenticate(String docbaseName);

	public void clearIdentity(String docbase);
}
