
package com.google.enterprise.connector.dctm;
import com.google.enterprise.connector.spi.*;
import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfIdentityException;
import com.documentum.fc.client.DfPrincipalException;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;


public class DctmAuthenticationManager implements AuthenticationManager {
	IDfSession session;
	IDfClient client;
	IDfSessionManager sMgr;
	IDfLoginInfo loginInfo;
	/**
	 * @param args
	 */
	public DctmAuthenticationManager(){
		
	}
	
	
	public DctmAuthenticationManager(IDfSession session){
		setSession(session);
	}
	
	
	public IDfSession getSession(){
		return session;
	}
	
	
	public boolean authenticate(String username, String password)
    throws LoginException, RepositoryException{
		return true;
	}
	
	public boolean authenticate(String username, String password, String docbase){
		boolean authOK=false;
		//try{
			setLoginInfo(username,password);
			System.out.println("après setlogininfo : username = "+username+" password = "+password);
			try{
				session.authenticate(loginInfo);
				authOK=true;
			}catch(DfException De){
				RepositoryException Re=new RepositoryException("erreur d'authentification");
				System.out.println(Re.getMessage());
			}
			//session = connection(username,password,docbase);
			//authOK=true;
		//}catch(DfAuthenticationException Dae){
			//throw new LoginException();
			//System.out.println("msg d'erreur vaut "+Dae);
			//return false;
		//}catch(DfServiceException Dse){
			//System.out.println("msg d'erreur vaut "+Dse);
			//return false;
			//throw new RepositoryException();
			
		//}
		return authOK;
	}
	
	public void setSession(IDfSession session){
		this.session=session;
	}
	
	public void setLoginInfo(String username,String password){
		loginInfo = new DfLoginInfo();
		loginInfo.setUser(username);
		System.out.println("logininfo:"+username);
		loginInfo.setPassword(password);
		System.out.println("logininfo:"+password);
	}
	
	public IDfLoginInfo getLoginInfo(){
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
	

}
