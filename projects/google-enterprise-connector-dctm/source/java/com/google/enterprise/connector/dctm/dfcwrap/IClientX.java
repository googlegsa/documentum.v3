package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;

public interface IClientX {
	IId getId(String id);
	IClient getLocalClient() throws RepositoryException;
}
