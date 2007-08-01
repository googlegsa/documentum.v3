package com.google.enterprise.connector.dctm.dfcwrap;

import java.io.ByteArrayInputStream;

import com.google.enterprise.connector.spi.RepositoryException;

public interface ISysObject {
	public long getContentSize() throws RepositoryException;

	public ByteArrayInputStream getContent() throws RepositoryException;

	public String getACLDomain() throws RepositoryException;

	public String getACLName() throws RepositoryException;

	public String getString(String name) throws RepositoryException;

	public int getInt(String name) throws RepositoryException;

	public ITime getTime(String name) throws RepositoryException;

	public double getDouble(String name) throws RepositoryException;

	public boolean getBoolean(String name) throws RepositoryException;

	public IId getId(String name) throws RepositoryException;

	public IFormat getFormat() throws RepositoryException;

	public int getAttrDataType(String name) throws RepositoryException;

	public int getAttrCount() throws RepositoryException;

	public IAttr getAttr(int attrIndex) throws RepositoryException;

	public void setSessionManager(ISessionManager sessionManager)
			throws RepositoryException;
	
	public IValue getRepeatingValue(String name, int index)
			throws RepositoryException;
	
	public int findAttrIndex(String name) throws RepositoryException;
	
	public int getValueCount(String name) throws RepositoryException;
	
		
}
