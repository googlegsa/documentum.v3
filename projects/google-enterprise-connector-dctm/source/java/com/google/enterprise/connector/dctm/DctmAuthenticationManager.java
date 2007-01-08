package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.*;



public class DctmAuthenticationManager implements AuthenticationManager {
	ISession session;
	IClient client;
	ISessionManager sMgr;
	ILoginInfo loginInfo;
	/**
	 * @param args
	 */
	public DctmAuthenticationManager(){
		
	}
	
	
	public DctmAuthenticationManager(ISession session,IClient client){
		setSession(session);
		setClient(client);
	}
	
	
	public ISession getSession(){
		return session;
	}
	
	
	public boolean authenticate(String username, String password)
    throws LoginException, RepositoryException{
		setLoginInfo(username,password);
		boolean authenticate = false;
		authenticate = client.authenticate (session.getDocbaseName(),getLoginInfo());
		return authenticate;
	}
	
	
	public void setSession(ISession session){
		this.session=session;
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
