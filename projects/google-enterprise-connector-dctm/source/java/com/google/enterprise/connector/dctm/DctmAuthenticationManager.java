package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLoginInfo;
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
		System.out.println("docbaseName "  + session.getDocbaseName());
		client.authenticate (session.getDocbaseName(),getLoginInfo());
		
//		setLoginInfo(username,password);
//		session.authenticate(getLoginInfo());
		return true;
	}
	
//	public boolean authenticate(String username, String password, String docbase){
//		boolean authOK=false;
//		//try{
//			setLoginInfo(username,password);
//			System.out.println("après setlogininfo : username = "+username+" password = "+password);
//			
//			session.authenticate(loginInfo);
//			authOK=true;
//			
//			RepositoryException Re=new RepositoryException("erreur d'authentification");
//			System.out.println(Re.getMessage());
//			
//			//session = connection(username,password,docbase);
//			//authOK=true;
//		//}catch(DfAuthenticationException Dae){
//			//throw new LoginException();
//			//System.out.println("msg d'erreur vaut "+Dae);
//			//return false;
//		//}catch(DfServiceException Dse){
//			//System.out.println("msg d'erreur vaut "+Dse);
//			//return false;
//			//throw new RepositoryException();
//			
//		//}
//		return authOK;
//	}
	
	public void setSession(ISession session){
		this.session=session;
	}
	
	public void setLoginInfo(String username,String password){
		loginInfo = new IDctmLoginInfo();/*todo jey*/
		loginInfo.setUser(username);
		System.out.println("logininfo:"+username);
		loginInfo.setPassword(password);
		System.out.println("logininfo:"+password);
		
	}
	
	public ILoginInfo getLoginInfo(){
		return loginInfo;
	}
	/*
	public IDfSession connection(String loginName, String loginPassword, String docbase) throws DfServiceException, DfIdentityException, DfAuthenticationException, DfPrincipalException {
		client = DfClient.getLocalClientEx();
		sMgr = client.newSessionManager(); 
		loginInfo = new DfLoginInfo();
		loginInfo.setUser(loginName);
		loginInfo.setPassword(loginPassword);
		sMgr.setIdentity( docbase, loginInfo );
		session = sMgr.getSession( docbase );
		return session;
	}
	*/
	/*
	public static void main(String[] args){
		boolean rep;
		 //System.out.println("Classpath:"+System.getProperty("java.class.path"));
		 //System.out.println("Classpath:"+System.getProperty("path"));
		 //System.setProperty("java.class.path",System.getProperty("java.class.path")+";\"C:\\Program Files\\Documentum\\Shared");
		 //System.out.println("Classpath:"+System.getProperty("java.class.path"));
		 
		try{
			 rep=authenticate("emilie","emilie","gdoc");
			 System.out.println("rep vaut "+rep);
		}catch(LoginException Le){
			System.out.println("login exception vaut "+Le);
		}catch(RepositoryException Re){
			System.out.println("login exception vaut "+Re);
		}
		
		// TODO Auto-generated method stub

	}
	*/


	public IClient getClient() {
		return client;
	}


	public void setClient(IClient client) {
		this.client = client;
	}
	

}
