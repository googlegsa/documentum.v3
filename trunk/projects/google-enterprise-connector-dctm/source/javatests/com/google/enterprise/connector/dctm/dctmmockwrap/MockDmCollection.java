package com.google.enterprise.connector.dctm.dctmmockwrap;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.query.QueryResult;

import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.spi.RepositoryException;

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
			if (colName.equals("r_object_id") || colName.equals("i_chronicle_id")) {
				colName = "jcr:uuid";
			}
			
			Property tmp = currentNode.getProperty(colName);
			return tmp.getString();
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e);
		}
	}

	/*
	 * protected Value[] getAuthorizedUsers() throws RepositoryException { try {
	 * Property tmp = currentNode.getProperty("acl"); return tmp.getValues(); }
	 * catch (PathNotFoundException e) { throw new RepositoryException(e); }
	 * catch (javax.jcr.RepositoryException e) { throw new
	 * RepositoryException(e); } }
	 */

	public IValue getValue(String attrName) throws RepositoryException {
		Value val = null;
		if (attrName.equals("r_object_id")) {
			attrName = "jcr:uuid";
		} else if (attrName.equals("object_name")) {
			attrName = "name";
		} else if (attrName.equals("r_modify_date")) {
			attrName = "google:lastmodify";
		}

		try {
			val = currentNode.getProperty(attrName).getValue();
		} catch (ValueFormatException e) {
			throw new RepositoryException(e);
		} catch (PathNotFoundException e) {
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e);
		}
		return new MockDmValue(val);
	}

	public void close() throws RepositoryException {
		// TODO Auto-generated method stub
		
	}

}