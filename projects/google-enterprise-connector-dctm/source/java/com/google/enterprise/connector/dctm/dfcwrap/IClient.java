package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;

public interface IClient {

	public IQuery getQuery();

	public ISession newSession(String string, ILoginInfo logInfo)
			throws RepositoryException;

	//public boolean authenticate(String docbaseName, ILoginInfo loginInfo)throws RepositoryException;

	public ISessionManager newSessionManager();

}
