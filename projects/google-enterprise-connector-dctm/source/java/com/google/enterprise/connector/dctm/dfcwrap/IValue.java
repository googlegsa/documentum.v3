package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;

public interface IValue {
	
	public String asString() throws RepositoryException;

	public int getDataType();
}
