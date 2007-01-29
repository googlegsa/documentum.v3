package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;



public class DctmAuthenticationManager implements AuthenticationManager {
	ISessionManager sessionManager;
	IClientX clientX;
	ILoginInfo loginInfo;
	/**
	 * @param args
	 */
	public DctmAuthenticationManager(){
		
	}
	
	
	public DctmAuthenticationManager(IClientX clientX){
//		setSession(session);
		setClientX(clientX);
		sessionManager = clientX.getSessionManager();
	}
	
	
	
	public boolean authenticate(String username, String password){
		setLoginInfo(username,password);
		sessionManager.clearIdentity(sessionManager.getDocbaseName());
		try {
			sessionManager.setIdentity(sessionManager.getDocbaseName(),loginInfo);
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean authenticate = false;
		
		///authenticate = client.authenticate (sessionManager.getDocbaseName(),getLoginInfo());
		authenticate = sessionManager.authenticate(sessionManager.getDocbaseName());
		
		System.out.println("DCTMAuthenticate method authenticate " + authenticate);
		return authenticate;
	}
	
	
	public void setLoginInfo(String username,String password){
		loginInfo = clientX.getLoginInfo();
		loginInfo.setUser(username);
		loginInfo.setPassword(password);
	}
	
	public ILoginInfo getLoginInfo(){
		return loginInfo;
	}



	public IClientX getClientX() {
		return clientX;
	}


	public void setClientX(IClientX clientX) {
		this.clientX = clientX;
	}
	
	
	/*
	public IClient getClient() {
		return client;
	}


	public void setClient(IClientX clientX) {
		try {
			this.client=clientX.getLocalClient();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	

}
