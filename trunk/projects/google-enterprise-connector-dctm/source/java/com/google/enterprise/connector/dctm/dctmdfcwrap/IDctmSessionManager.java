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
import com.google.enterprise.connector.spi.RepositoryException;

public class IDctmSessionManager implements ISessionManager{
	IDfSessionManager dfSessionManager;
	
	public IDctmSessionManager (IDfSessionManager DfSessionManager){
		this.dfSessionManager=DfSessionManager;
	}
	
	public ISession getSession(String docbase){
		IDfSession DfSession=null;
		try{
			DfSession=dfSessionManager.getSession(docbase);
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
			dfSessionManager.setIdentity(docbase,idfLoginInfo);
		}catch(DfServiceException dse){
			dse.getMessage();
		}
	}
	
	public ISession newSession(String docbase){
		IDfSession idfSession=null;
		String error=null;
		try{
			idfSession=dfSessionManager.newSession(docbase);	
		}catch(DfIdentityException di){
			error=di.getMessage();
		}catch(DfAuthenticationException da){
			error=da.getMessage();
		}catch(DfPrincipalException dp){
			error=dp.getMessage();
		}catch(DfServiceException ds){
			error=ds.getMessage();
		}
		if (error!=null) {
			System.out.println(error);
			return null;
		}
		return new IDctmSession(idfSession);
	}
	
	
	
}
