package com.google.enterprise.connector.dctm.dctmmockwrap;

import javax.jcr.NodeIterator;
import javax.jcr.query.QueryResult;

import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.jcradaptor.SpiResultSetFromJcr;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;

public class MockDmCollection implements ICollection {
	private NodeIterator collection;

	protected MockDmCollection(QueryResult mjQueryResult)
			throws RepositoryException {
		try {
			collection = mjQueryResult.getNodes();
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e);
		}
	}

	public boolean next() {
		if (collection.hasNext()) {
			collection.nextNode();
			return true;
		}
		return false;
	}

	public ResultSet buildResulSetFromCollection(ISessionManager sessionManager)
			throws RepositoryException {
		SpiResultSetFromJcr test = new SpiResultSetFromJcr(collection);
		return test;
	}

	public IValue getValue(String attrName) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(String colName) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

}