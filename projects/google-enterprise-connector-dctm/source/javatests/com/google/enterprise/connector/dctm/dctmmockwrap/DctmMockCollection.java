package com.google.enterprise.connector.dctm.dctmmockwrap;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.query.QueryResult;

import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.jcradaptor.SpiResultSetFromJcr;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;

public class DctmMockCollection implements ICollection {
	private NodeIterator collection;
	private Node currentNode;
	public DctmMockCollection(QueryResult mjQueryResult) throws RepositoryException{
			try {
				collection = mjQueryResult.getNodes();
			} catch (javax.jcr.RepositoryException e) {
				RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
				re.setStackTrace(e.getStackTrace());
				throw re;
			}
	}
	
	//Needed as next() is called in DctmQTM. Will no longer be needed soon:
	//buildResultSet() method intends to avoid the necessity of parsing the collection
	//in a class common to DFC and Mock but rather in the Collection object (this)
	public boolean next(){
		if (collection.hasNext()){
			currentNode = collection.nextNode();
			return true;
		}
		return false;
	}
	
	//Needed as getValue() is called in DctmQTM. Will no longer be needed soon:
	//BuildResSet method intends to avoid the necessity of parsing the collection
	//in a class common to DFC and Mock but rather in the Collection object (this)
	public IValue getValue(String attrName) throws RepositoryException{
		if (currentNode==null){
			return null;
		}else {
			javax.jcr.Property py=null;
					try {
						py = currentNode.getProperty(attrName);
					} catch (PathNotFoundException e) {
						RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
						re.setStackTrace(e.getStackTrace());
						throw re;
					} catch (javax.jcr.RepositoryException e) {
						RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
						re.setStackTrace(e.getStackTrace());
						throw re;
					}
			return new DctmMockValue(py);
		}
	}
	
	public ResultSet buildResulSetFromCollection(ISession session) throws RepositoryException {
		SpiResultSetFromJcr test = new SpiResultSetFromJcr(collection);
		return test;
	}

	public ITypedObject getTypedObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public IId getObjectId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(String colName) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet buildResulSetFromCollection(ISessionManager sessionManager) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}
	
}