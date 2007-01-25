package com.google.enterprise.connector.dctm.dctmmockwrap;

import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.mock.jcr.MockJcrQueryManager;
import com.google.enterprise.connector.spi.RepositoryException;

public class MockDmQuery implements IQuery {
	String query;
	
	public MockDmQuery(){
		query="";
	}
	
	public ICollection execute(ISessionManager sessionManager, int queryType) throws RepositoryException{
		try{
			MockRepositoryDocumentStore a = null;
			a=((MockDmSession)sessionManager.getSession(sessionManager.getDocbaseName())).getStore();
			MockJcrQueryManager mrQueryMger = new MockJcrQueryManager(a);
			Query q = mrQueryMger.createQuery(this.query,"xpath");
			QueryResult qr = q.execute();
			MockDmCollection co = new MockDmCollection(qr);
			return co;
		}catch (javax.jcr.RepositoryException e){
			throw new RepositoryException(e);
		}
	}

	public void setDQL(String dqlStatement){
		this.query=dqlStatement;
	}
}
