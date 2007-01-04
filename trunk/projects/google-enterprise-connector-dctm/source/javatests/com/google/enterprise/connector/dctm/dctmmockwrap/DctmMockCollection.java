package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.jcradaptor.SpiResultSetFromJcr;
import com.google.enterprise.connector.spi.ResultSet;

import javax.jcr.query.QueryResult;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

public class DctmMockCollection implements ICollection {
	private NodeIterator collection;
	private Node currentNode;
	public DctmMockCollection(QueryResult mjQueryResult){
		try {
			collection = mjQueryResult.getNodes();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public IValue getValue(String attrName){
		if (currentNode==null){
			return null;
		}else {
			Property py=null;
			try {
				py = currentNode.getProperty(attrName);
			} catch (PathNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new DctmMockValue(py);
		}
	}
	
	public ResultSet buildResulSetFromCollection(ISession session) {
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
	
}