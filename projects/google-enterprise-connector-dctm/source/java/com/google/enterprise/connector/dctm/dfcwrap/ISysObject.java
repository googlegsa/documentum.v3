package com.google.enterprise.connector.dctm.dfcwrap;

import java.io.ByteArrayInputStream;

import com.google.enterprise.connector.spi.RepositoryException;

public interface ISysObject{
	public IFormat getFormat() throws RepositoryException;
	public long getContentSize() throws RepositoryException;
	public ByteArrayInputStream getContent() throws RepositoryException;
	public int getPermitEx(String name) throws RepositoryException;
	public String getACLDomain() throws RepositoryException;
	public String getACLName() throws RepositoryException;
}
