package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;

public class IDctmQuery implements IQuery{
	IDfQuery idfQuery;
	public static int DF_READ_QUERY = IDfQuery.DF_READ_QUERY; 
	
	public IDctmQuery(IDfQuery idfQuery){
		this.idfQuery=idfQuery;
	}
	
	public IDctmQuery(){
		this.idfQuery=new DfQuery();
	}
	
//	public ICollection execute(ISession session, int queryType) throws RepositoryException{	
//		if (!(session instanceof IDctmSession)) {
//			throw new IllegalArgumentException();
//		}
//		IDctmSession idctmsession = (IDctmSession) session;
//		IDfSession idfSession=idctmsession.getDfSession();
//		IDfCollection DfCollection=null;
//		
//		try{
//			DfCollection=idfQuery.execute(idfSession,queryType);
//		}catch(DfException de){
//			RepositoryException re = new LoginException(de.getMessage(),de.getCause());
//			re.setStackTrace(de.getStackTrace());
//			throw re;
//		}
//		return new IDctmCollection(DfCollection);
//	}
	
	public void setDQL(String dqlStatement){
		idfQuery.setDQL(dqlStatement);
	}
	
	public int getDF_READ_QUERY() {
		return DF_READ_QUERY;
	}

	public void setDF_READ_QUERY(int df_read_query){
		DF_READ_QUERY = df_read_query;
	}

	public ICollection execute(ISessionManager sessionManager, int queryType) throws RepositoryException {
		System.out.println("--- IDctmQuery execute ---");
		if (!(sessionManager instanceof IDctmSessionManager)) {
			throw new IllegalArgumentException();
		}
		IDctmSession idctmsession =  (IDctmSession)sessionManager.getSession(sessionManager.getDocbaseName());
		IDfSession idfSession=idctmsession.getDfSession();
		IDfCollection DfCollection=null;
		
		try{
			DfCollection=idfQuery.execute(idfSession,queryType);
		}catch(DfException de){
			System.out.println("--- IDctmQuery Exception  ---");
			RepositoryException re = new LoginException(de.getMessage(),de.getCause());
			re.setStackTrace(de.getStackTrace());
			throw re;
		}
		return new IDctmCollection(DfCollection);
	}

}
