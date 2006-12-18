package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.mock.jcr.*;

import javax.jcr.query.QueryResult;
import javax.jcr.RepositoryException;

public class DctmMockQueryResult implements ICollection {
	private QueryResult mjcrQuery;
	public DctmMockQueryResult(QueryResult mjQueryResult){
		mjcrQuery = mjQueryResult;
	}
	
	public boolean next(){
		return true;
	}
	
	public ITypedObject getTypedObject(){
		return null;
	}

	public IValue getValue(String attrName){
		return null;
	}
}