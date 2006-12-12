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

public class IDctmSessionManager implements ISessionManager{
	IDfSessionManager DfSessionManager;
	
	public IDctmSessionManager (IDfSessionManager DfSessionManager){
		this.DfSessionManager=DfSessionManager;
	}
	
	public ISession getSession(String docbase){
		IDfSession DfSession=null;
		try{
			DfSession=DfSessionManager.getSession(docbase);
		}catch(DfIdentityException di){
			di.getMessage();
		}catch(DfAuthenticationException da){
			da.getMessage();
		}catch(DfPrincipalException dp){
			dp.getMessage();
		}catch(DfServiceException ds){
			ds.getMessage();
		}
		return new IDctmSession(DfSession);
	}
	
	public void setIdentity(String docbase,ILoginInfo identity){
		if (!(identity instanceof IDctmLoginInfo)) {
			throw new IllegalArgumentException();
		}
		IDctmLoginInfo dctmLoginInfo = (IDctmLoginInfo) identity;
		IDfLoginInfo idfLoginInfo=dctmLoginInfo.getIdfLoginInfo();
		try{
			DfSessionManager.setIdentity(docbase,idfLoginInfo);
		}catch(DfServiceException dse){
			dse.getMessage();
		}
	}
	
	public ISession newSession(String docbase){
		IDfSession DfSession=null;
		try{
			DfSession=DfSessionManager.newSession(docbase);
		}catch(DfIdentityException di){
			di.getMessage();
		}catch(DfAuthenticationException da){
			da.getMessage();
		}catch(DfPrincipalException dp){
			dp.getMessage();
		}catch(DfServiceException ds){
			ds.getMessage();
		}
		return new IDctmSession(DfSession);
	}
	
}
