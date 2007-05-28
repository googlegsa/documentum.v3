package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;

public interface IClient {

	public IQuery getQuery();

	public ISessionManager newSessionManager();

	public IDocbaseMap getDocbaseMap() throws RepositoryException;

}
