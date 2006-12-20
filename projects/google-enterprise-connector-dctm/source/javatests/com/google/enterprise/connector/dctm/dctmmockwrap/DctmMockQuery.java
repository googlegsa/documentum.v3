package com.google.enterprise.connector.dctm.dctmmockwrap;

import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.mock.jcr.MockJcrQuery;
import com.google.enterprise.connector.mock.jcr.MockJcrQueryManager;

public class DctmMockQuery implements IQuery {
	MockJcrQuery queryStr;
	String Query;
	private int DF_READ_QUERY;
	
	public ICollection execute(ISession session, int queryType){
		try{
			MockJcrQueryManager mrQueryMger = new MockJcrQueryManager(((DctmMockSession)session).getStore());
			Query q = mrQueryMger.createQuery(this.Query,"mockQueryLanguage");
			QueryResult qr = q.execute();
			return new DctmMockQueryResult(qr);
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
		this.Query=dqlStatement;
	}
}
