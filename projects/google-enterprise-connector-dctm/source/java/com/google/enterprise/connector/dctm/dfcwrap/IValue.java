package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;

public interface IValue {

	public String asString() throws RepositoryException;

	public boolean asBoolean() throws RepositoryException;

	public double asDouble() throws RepositoryException;

	public long asInteger() throws RepositoryException;

	public ITime asTime() throws RepositoryException;
	
}
