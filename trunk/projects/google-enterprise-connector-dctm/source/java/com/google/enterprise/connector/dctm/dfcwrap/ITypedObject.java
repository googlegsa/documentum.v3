package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;

public interface ITypedObject{
	public IId getObjectId() throws RepositoryException;

}
