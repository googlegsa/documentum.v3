package com.google.enterprise.connector.dctm.dctmmockwrap;

import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.mock.jcr.MockJcrQueryManager;
import com.google.enterprise.connector.spi.RepositoryException;

public class DctmMockQuery implements IQuery {
	String query;
	private int DF_READ_QUERY;
	
	public DctmMockQuery(){
		query="";
	}
	
	public ICollection execute(ISession session, int queryType) throws RepositoryException{
		try{
			MockRepositoryDocumentStore a = null;
			a=((DctmMockSession)session).getStore();
			MockJcrQueryManager mrQueryMger = new MockJcrQueryManager(a);
			Query q = mrQueryMger.createQuery(this.query,"xpath");
			QueryResult qr = q.execute();
			DctmMockCollection co = new DctmMockCollection(qr);
			return co;
		}catch (javax.jcr.RepositoryException e){
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
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
