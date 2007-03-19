package com.google.enterprise.connector.dctm.dfcwrap;

public interface IClient {

	public IQuery getQuery();

	public ISessionManager newSessionManager();

}
