package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;

public interface ILocalClient {
	public ISessionManager newSessionManager();
	public ISession findSession(String dfcSessionId) throws RepositoryException;
}
