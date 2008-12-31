package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;

public interface ISession {

	public ISysObject getObject(IId objectId) throws RepositoryDocumentException;

	public String getLoginTicketForUser(String username)
			throws RepositoryException;

	public IType getType(String typeName) throws RepositoryException; 
	
	public ISessionManager getSessionManager() throws RepositoryException; 
	
}
