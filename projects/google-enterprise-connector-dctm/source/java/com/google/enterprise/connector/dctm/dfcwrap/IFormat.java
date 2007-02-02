package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;

public interface IFormat {
	public boolean canIndex() throws RepositoryException;

	public String getMIMEType();
}
