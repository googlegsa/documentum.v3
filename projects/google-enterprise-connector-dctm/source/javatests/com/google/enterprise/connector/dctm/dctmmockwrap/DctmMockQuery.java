package com.google.enterprise.connector.dctm.dctmmockwrap;

import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.mock.jcr.MockJcrQueryManager;

public class DctmMockQuery implements IQuery {
	String query;
	private int DF_READ_QUERY;
	
	public DctmMockQuery(){
		query="";
	}
	
	public ICollection execute(ISession session, int queryType){
		try{
			MockRepositoryDocumentStore a = null;
			MockJcrQueryManager mrQueryMger = new MockJcrQueryManager(((DctmMockSession)session).getStore());
			Query q = mrQueryMger.createQuery(this.query,"mockQueryLanguage");
			QueryResult qr = q.execute();
			return new DctmMockCollection(qr);
		}catch (javax.jcr.RepositoryException re){
			//TODO exeptions for real
			return null;
		}
	}
	public int getDF_READ_QUERY(){
		return DF_READ_QUERY;
	}
	public void setDF_READ_QUERY(int i){
		DF_READ_QUERY=i;
	}
	public void setDQL(String dqlStatement){
		this.query=dqlStatement;
	}
}
