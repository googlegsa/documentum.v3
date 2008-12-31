package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfDocbaseMap;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.IDocbaseMap;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmDocbaseMap implements IDocbaseMap {

	IDfDocbaseMap docbaseMap = null;

	public DmDocbaseMap(IDfDocbaseMap docbaseMap) {
		this.docbaseMap = docbaseMap;
	}

	public int getDocbaseCount() throws RepositoryException {

		try {
			return docbaseMap.getDocbaseCount();
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

	public String getDocbaseName(int i) throws RepositoryException {
		try {
			return docbaseMap.getDocbaseName(i);
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

}
