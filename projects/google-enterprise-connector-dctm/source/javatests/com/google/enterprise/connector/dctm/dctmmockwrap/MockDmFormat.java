package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.spi.RepositoryException;

public class MockDmFormat implements IFormat {
	private String mimeType;
	private String dosExtension;

	public MockDmFormat(String mimetype) {
		this.mimeType = mimetype;
	}

	public boolean canIndex() throws RepositoryException {
		return true;
	}

	public String getMIMEType() {
		return mimeType;
	}
	
	public String getDOSExtension() {
		return dosExtension;
	}

}
