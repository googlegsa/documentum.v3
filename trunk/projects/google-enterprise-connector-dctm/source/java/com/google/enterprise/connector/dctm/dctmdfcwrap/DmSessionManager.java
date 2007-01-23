package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.DfIdentityException;
import com.documentum.fc.client.DfPrincipalException;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.IDfLoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.LoginException;

public class DmSessionManager implements ISessionManager{
	IDfSessionManager dfSessionManager;
	
	private String docbaseName;
	private String serverUrl;
	
	public DmSessionManager (IDfSessionManager DfSessionManager){
		
		this.dfSessionManager=DfSessionManager;
	}
	
	public ISession getSession(String docbase) throws LoginException{
//		System.out.println("--- DmSessionManager getSession ---");
		IDfSession DfSession=null;
		try{
			DfSession = dfSessionManager.getSession(docbase);
		}catch(DfIdentityException iE){
			LoginException le = new LoginException(iE.getMessage(),iE.getCause());
			le.setStackTrace(iE.getStackTrace());
			throw le;
		}catch(DfAuthenticationException iE){
			LoginException le = new LoginException(iE.getMessage(),iE.getCause());
			le.setStackTrace(iE.getStackTrace());
			throw le;
		}catch(DfPrincipalException iE){
			LoginException le = new LoginException(iE.getMessage(),iE.getCause());
			le.setStackTrace(iE.getStackTrace());
			throw le;
		}catch(DfServiceException iE){
			LoginException le = new LoginException(iE.getMessage(),iE.getCause());
			le.setStackTrace(iE.getStackTrace());
			throw le;
		}
		return new DmSession(DfSession);
	}
	
	public void setIdentity(String docbase,ILoginInfo identity) throws LoginException{
		if (!(identity instanceof DmLoginInfo)) {
			throw new IllegalArgumentException();
		}
		DmLoginInfo dctmLoginInfo = (DmLoginInfo) identity;
		IDfLoginInfo idfLoginInfo=dctmLoginInfo.getIdfLoginInfo();
		try{
			dfSessionManager.setIdentity(docbase,idfLoginInfo);
		}catch(DfServiceException iE){
			LoginException le = new LoginException(iE.getMessage(),iE.getCause());
			le.setStackTrace(iE.getStackTrace());
			throw le;
		}
	}
	
	public ISession newSession(String docbase) throws LoginException{
		IDfSession idfSession=null;
		String error=null;
		try{
			idfSession = dfSessionManager.newSession(docbase);
		}catch(DfIdentityException iE){
			LoginException le = new LoginException(iE.getMessage(),iE.getCause());
			le.setStackTrace(iE.getStackTrace());
			throw le;
		}catch(DfAuthenticationException iE){
			LoginException le = new LoginException(iE.getMessage(),iE.getCause());
			le.setStackTrace(iE.getStackTrace());
			throw le;
		}catch(DfPrincipalException iE){
			LoginException le = new LoginException(iE.getMessage(),iE.getCause());
			le.setStackTrace(iE.getStackTrace());
			throw le;
		}catch(DfServiceException iE){
			LoginException le = new LoginException(iE.getMessage(),iE.getCause());
			le.setStackTrace(iE.getStackTrace());
			throw le;
		}
		if (error!=null) {
			System.out.println(error);
			return null;
		}
		return new DmSession(idfSession);
	}

	public void release(ISession session) {
		this.dfSessionManager.release(((DmSession)session).getDfSession());
		
	}

	public IDfSessionManager getDfSessionManager() {
		return dfSessionManager;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
		
	}

	public String getDocbaseName() {
		
		return docbaseName;
	}
	
	public void setDocbaseName(String docbaseName){
		this.docbaseName = docbaseName;
	}

	public String getServerUrl() {
		return serverUrl;
	}
	
	
	
}
