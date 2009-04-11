package com.google.enterprise.connector.dctm.dfcwrap;

import java.io.ByteArrayInputStream;

import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;

public interface ISysObject {
	public String getObjectName() throws RepositoryDocumentException;

	public long getContentSize() throws RepositoryDocumentException;

	public ByteArrayInputStream getContent() throws RepositoryDocumentException;

	public String getACLDomain() throws RepositoryDocumentException;

	public String getACLName() throws RepositoryDocumentException;

	public String getString(String name) throws RepositoryDocumentException;

	public int getInt(String name) throws RepositoryDocumentException;

	public ITime getTime(String name) throws RepositoryDocumentException;

	public double getDouble(String name) throws RepositoryDocumentException;

	public boolean getBoolean(String name) throws RepositoryDocumentException;

	public IId getId(String name) throws RepositoryDocumentException;

	public IFormat getFormat() throws RepositoryDocumentException;

	public int getAttrDataType(String name) throws RepositoryDocumentException;

	public int getAttrCount() throws RepositoryDocumentException;

	public IAttr getAttr(int attrIndex) throws RepositoryDocumentException;

	public void setSessionManager(ISessionManager sessionManager)
			throws RepositoryDocumentException;

	public IValue getRepeatingValue(String name, int index)
			throws RepositoryDocumentException;

	public int findAttrIndex(String name) throws RepositoryDocumentException;

	public int getValueCount(String name) throws RepositoryDocumentException;

}
