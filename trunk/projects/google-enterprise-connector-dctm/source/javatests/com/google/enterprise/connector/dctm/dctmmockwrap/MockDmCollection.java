package com.google.enterprise.connector.dctm.dctmmockwrap;


import javax.jcr.NodeIterator;
import javax.jcr.query.QueryResult;

import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.jcradaptor.SpiResultSetFromJcr;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;

public class MockDmCollection implements ICollection {
	private NodeIterator collection;
//	private Node currentNode;
	public MockDmCollection(QueryResult mjQueryResult) throws RepositoryException{
			try {
				collection = mjQueryResult.getNodes();
			} catch (javax.jcr.RepositoryException e) {
				throw new RepositoryException(e);
			}
	}
	
	//Needed as next() is called in DctmQTM. Will no longer be needed soon:
	//buildResultSet() method intends to avoid the necessity of parsing the collection
	//in a class common to DFC and Mock but rather in the Collection object (this)
	public boolean next(){
		if (collection.hasNext()){
//			currentNode = 
				collection.nextNode();
			return true;
		}
		return false;
	}
	
	//Needed as getValue() is called in DctmQTM. Will no longer be needed soon:
	//BuildResSet method intends to avoid the necessity of parsing the collection
	//in a class common to DFC and Mock but rather in the Collection object (this)
	//Here we are, no longer needed
	public IValue getValue(String attrName) throws RepositoryException{
		/*if (currentNode==null){
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
		}*/
		return null;
	}

	/**
	 * delete in interface
//	 */
//	public IId getObjectId() {
//		
//		return null;
//	}

	public String getString(String colName) {
		
		return null;
	}

	public ResultSet buildResulSetFromCollection(ISessionManager sessionManager) throws RepositoryException {
		SpiResultSetFromJcr test = new SpiResultSetFromJcr(collection);
		return test;
	}
	
}