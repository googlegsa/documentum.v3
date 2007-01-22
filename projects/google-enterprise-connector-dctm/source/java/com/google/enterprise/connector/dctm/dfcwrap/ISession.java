package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;


public interface ISession{
	
	public String getSessionId() throws RepositoryException;

	public String getLoginTicketForUser(String username) throws RepositoryException;

	public String getDocbaseName() throws RepositoryException;

}
