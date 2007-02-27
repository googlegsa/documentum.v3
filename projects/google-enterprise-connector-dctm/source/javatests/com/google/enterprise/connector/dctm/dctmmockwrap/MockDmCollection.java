package com.google.enterprise.connector.dctm.dctmmockwrap;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.query.QueryResult;

import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.jcradaptor.SpiResultSetFromJcr;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;

public class MockDmCollection implements ICollection {
	private NodeIterator collection;

	private Node currentNode;

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

			currentNode = collection.nextNode();
			return true;
		}
		return false;
	}

	public String getString(String colName) throws RepositoryException {
		try {
			Property tmp = currentNode.getProperty(colName);
			return tmp.getString();
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e);
		}
	}

	protected Value[] getAuthorizedUsers() throws RepositoryException {
		try {
			Property tmp = currentNode.getProperty("acl");
			return tmp.getValues();
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e);
		}
	}

	public ResultSet buildResulSetFromCollection(
			ISessionManager sessionManager, IClientX clientX)
			throws RepositoryException {
		SpiResultSetFromJcr test = new SpiResultSetFromJcr(collection);
		return test;
	}

	public IValue getValue(String attrName) throws RepositoryException {
		if (attrName.equals("r_object_id")) {
			Value tmp;
			try {
				tmp = currentNode.getProperty("docid").getValue();
			} catch (ValueFormatException e) {
				throw new RepositoryException(e);
			} catch (PathNotFoundException e) {
				throw new RepositoryException(e);
			} catch (javax.jcr.RepositoryException e) {
				throw new RepositoryException(e);
			}
			return new MockDmValue(tmp);
		}
		throw new RepositoryException(
				"ICollection.getValue() is not implemented for " + attrName);
	}

}