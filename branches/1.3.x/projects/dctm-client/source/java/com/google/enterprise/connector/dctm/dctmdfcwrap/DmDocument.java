package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.IDocument;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmDocument extends DmSysObject implements IDocument {
	IDfDocument idfDocument;

	public DmDocument(IDfDocument idfDocument) {
		super((IDfSysObject) idfDocument);
		this.idfDocument = idfDocument;
	}

	public void setFileEx(String fileName, String formatName)
			throws RepositoryException {
		try {
			idfDocument.setFileEx(fileName, formatName, 0, null);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
	}

	public void setObjectName(String name) throws RepositoryException {
		try {
			idfDocument.setObjectName(name);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
	}

	public void setContentType(String contentType) throws RepositoryException {
		try {
			idfDocument.setContentType(contentType);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
	}

	public void save() throws RepositoryException {
		try {
			idfDocument.save();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
	}

	public void destroyAllVersions() throws RepositoryException {
		try {
			idfDocument.destroyAllVersions();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
	}
}
