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
		return 0;
	}
	public void setDQL(String dqlStatement){
		this.Query=dqlStatement;
	}
}
