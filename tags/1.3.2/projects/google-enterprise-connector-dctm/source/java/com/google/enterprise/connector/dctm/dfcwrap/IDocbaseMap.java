package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;

public interface IDocbaseMap {

	public int getDocbaseCount() throws RepositoryException;

	public String getDocbaseName(int i) throws RepositoryException;

}
