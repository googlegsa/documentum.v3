package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

import com.google.enterprise.connector.dctm.dfcwrap.ISession;

import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;


public class IDctmClient implements IClient{
	IDfClient idfClient;
	IDfClientX idfClientX;
	
	IDctmSessionManager idctmSessionManager = null;
	
	public IDctmClient() throws RepositoryException{
		try {
			idfClientX = new DfClientX();
			idfClient = idfClientX.getLocalClient();
		} catch (DfException de) {
			RepositoryException re = new LoginException(de.getMessage(),de.getCause());
			re.setStackTrace(de.getStackTrace());
			throw re;
		}
	}
	
	public ILocalClient getLocalClientEx(){
		System.out.println("--- IDctmClient getLocalClientEx ---");
		IDfClient idfClient=null;
		idfClient=DfClient.getLocalClientEx();
		return new IDctmLocalClient(idfClient);
	}
	
		
//	public String getSessionForUser(String userName){
//		String ticket = null;
//		try {
//			ticket = idfSession.getLoginTicketForUser(userName);
//			
//		} catch (DfException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return ticket;
//	}
	
	

	public IQuery getQuery(){
		return new IDctmQuery(new DfQuery());
	}


	public ISession newSession(String docbase, ILoginInfo logInfo) throws RepositoryException {
		System.out.println("--- IDctmClient newSession ---");
		IDfSession sessionUser = null;
		IDfLoginInfo idfLogInfo= new DfLoginInfo();
		idfLogInfo.setUser(logInfo.getUser());
		idfLogInfo.setPassword(logInfo.getPassword());
		try {
			sessionUser = idfClient.newSession(docbase,idfLogInfo);
		} catch (DfException de) {
			LoginException re = new LoginException(de.getMessage(),de.getCause());
			re.setStackTrace(de.getStackTrace());
			throw re;
		}
		return new IDctmSession(sessionUser);
	}

	public boolean authenticate(String docbaseName, ILoginInfo loginInfo) {
		if (!(loginInfo instanceof IDctmLoginInfo)) {
			throw new IllegalArgumentException();
		}
		IDctmLoginInfo dctmLoginInfo = (IDctmLoginInfo) loginInfo;
		IDfLoginInfo idfLoginInfo=dctmLoginInfo.getIdfLoginInfo();
		try {
			this.idfClient.authenticate(docbaseName, idfLoginInfo);
			
		} catch (DfException e) {
			return false;
//			try {
//				throw new LoginException(e.getMessage());
//			} catch (LoginException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}
		return true;
		
	}

	public ILoginInfo getLoginInfo() {
		return new IDctmLoginInfo(idfClientX.getLoginInfo());
	}

	public IId getId(String value) {	
		
		return new IDctmId(this.idfClientX.getId(value));
	}

	public ISessionManager getSessionManager() {
		return idctmSessionManager;//new IDctmSessionManager(idfSessionManager);
	}

	public void setSessionManager(ISessionManager sessionManager) {		
		idctmSessionManager = (IDctmSessionManager)sessionManager;
	}

	
	

}
