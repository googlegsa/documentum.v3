package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmQuery implements IQuery{
	IDfQuery idfQuery;
	public static int DF_READ_QUERY = IDfQuery.DF_READ_QUERY; 
	
	public DmQuery(IDfQuery idfQuery){
		this.idfQuery=idfQuery;
	}
	
	public DmQuery(){
		this.idfQuery=new DfQuery();
	}
	
//	public ICollection execute(ISession session, int queryType) throws RepositoryException{	
//		if (!(session instanceof DmSession)) {
//			throw new IllegalArgumentException();
//		}
//		DmSession idctmsession = (DmSession) session;
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
//		return new DmCollection(DfCollection);
//	}
	
	public void setDQL(String dqlStatement){
		idfQuery.setDQL(dqlStatement);
	}
//	
//	public int getDF_READ_QUERY() {
//		return DF_READ_QUERY;
//	}

//	public void setDF_READ_QUERY(int df_read_query){
//		DF_READ_QUERY = df_read_query;
//	}

	public ICollection execute(ISessionManager sessionManager, int queryType) throws RepositoryException {
//		System.out.println("--- DmQuery execute ---");
		if (!(sessionManager instanceof DmSessionManager)) {
			throw new IllegalArgumentException();
		}
		DmSession idctmsession =  (DmSession)sessionManager.getSession(sessionManager.getDocbaseName());
		IDfSession idfSession=idctmsession.getDfSession();
		IDfCollection DfCollection=null;
		
		try{
			DfCollection=idfQuery.execute(idfSession,queryType);
		}catch(DfException de){
//			System.out.println("--- DmQuery Exception  ---");
			RepositoryException re = new LoginException(de.getMessage(),de.getCause());
			re.setStackTrace(de.getStackTrace());
			throw re;
		}
		return new DmCollection(DfCollection);
	}

}
