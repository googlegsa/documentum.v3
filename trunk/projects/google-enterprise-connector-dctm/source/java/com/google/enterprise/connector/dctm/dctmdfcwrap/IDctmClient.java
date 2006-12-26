package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;

import com.google.enterprise.connector.dctm.dfcwrap.ISession;

import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.spi.LoginException;


public class IDctmClient implements IClient{
	IDfClient idfClient;
	
	IDfSession idfSession = null;
	
	public IDctmClient(){
		try {
			idfClient = new DfClient();
		} catch (DfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ILocalClient getLocalClientEx(){
		IDfClient idfClient=null;
		idfClient=DfClient.getLocalClientEx();
		return new IDctmLocalClient(idfClient);
	}
	
		
	public String getSessionForUser(String userName){
		String ticket = null;
		try {
			ticket = idfSession.getLoginTicketForUser(userName);
			
		} catch (DfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ticket;
	}
	
	

	public IQuery getQuery(){
		return new IDctmQuery(new DfQuery());
	}


	public ISession newSession(String docbase, ILoginInfo logInfo) {
		IDfSession sessionUser = null;
		IDfLoginInfo idfLogInfo= new DfLoginInfo();
		idfLogInfo.setUser(logInfo.getUser());
		idfLogInfo.setPassword(logInfo.getPassword());
		try {
			sessionUser = idfClient.newSession(docbase,idfLogInfo);
		} catch (DfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new IDctmSession(sessionUser);
	}

	public void authenticate(String docbaseName, ILoginInfo loginInfo) {
		if (!(loginInfo instanceof IDctmLoginInfo)) {
			throw new IllegalArgumentException();
		}
		IDctmLoginInfo dctmLoginInfo = (IDctmLoginInfo) loginInfo;
		IDfLoginInfo idfLoginInfo=dctmLoginInfo.getIdfLoginInfo();
		try {
			this.idfClient.authenticate(docbaseName, idfLoginInfo);
		} catch (DfException e) {
			try {
				throw new LoginException(e.getMessage());
			} catch (LoginException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	

}
