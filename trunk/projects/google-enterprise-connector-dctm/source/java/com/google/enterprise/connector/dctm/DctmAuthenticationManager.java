package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;



public class DctmAuthenticationManager implements AuthenticationManager {
	ISessionManager sessionManager;
	IClient client;
//	ISessionManager sMgr;
	ILoginInfo loginInfo;
	/**
	 * @param args
	 */
	public DctmAuthenticationManager(){
		
	}
	
	
	public DctmAuthenticationManager(IClient client){
//		setSession(session);
		setClient(client);
		sessionManager = client.getSessionManager();
	}
	
	
	
	public boolean authenticate(String username, String password)
    throws LoginException, RepositoryException{
		setLoginInfo(username,password);
		boolean authenticate = false;
		authenticate = client.authenticate (sessionManager.getDocbaseName(),getLoginInfo());
		System.out.println("DCTMAuthenticate method authenticate " + authenticate);
		return authenticate;
	}
	
	
	public void setLoginInfo(String username,String password){
		loginInfo = client.getLoginInfo();
		loginInfo.setUser(username);
		loginInfo.setPassword(password);
	}
	
	public ILoginInfo getLoginInfo(){
		return loginInfo;
	}



	public IClient getClient() {
		return client;
	}


	public void setClient(IClient client) {
		this.client = client;
	}
	

}
