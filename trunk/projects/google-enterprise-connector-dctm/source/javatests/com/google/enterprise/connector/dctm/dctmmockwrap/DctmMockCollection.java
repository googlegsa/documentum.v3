package com.google.enterprise.connector.dctm.dctmmockwrap;


import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.jcradaptor.SpiResultSetFromJcr;
import com.google.enterprise.connector.spi.ResultSet;

import javax.jcr.query.QueryResult;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
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
	
	public ITypedObject getTypedObject(){
		return null;
	}

	//Needed as getValue() is called in DctmQTM. Will no longer be needed soon:
	//BuildResSet method intends to avoid the necessity of parsing the collection
	//in a class common to DFC and Mock but rather in the Collection object (this)
	public IValue getValue(String attrName){
		/*if (currentNode==null){
			return null;
		}else {
			String mockArg = "";
			//DFC calls a determined number of values.
			//The mapping between DFC properties names and MockRepository ones has to be done
			//An exhaustive listing has been chosen.
			if (attrName.equals("")){
				mockArg="";
			}else if (attrName.equals("")){
				mockArg="";
			}else if (attrName.equals("")){
				mockArg="";
			}else if (attrName.equals("")){
				mockArg="";
			}
			
			try {
				return new DctmMockValue(currentNode.getProperty(mockArg));
			} catch (PathNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			return null;
		//}
	}


	public IId getObjectId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(String colName) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet buildResulSetFromCollection(ISession session) {
		return new SpiResultSetFromJcr(collection);
	}

}