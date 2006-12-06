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

public class DctmSession implements Session{
	  /**
	   * Gets a QueryTraversalManager to implement query-based traversal.  
	   * @return    a QueryTraversalManager - should not be null
	   * @throws RepositoryException
	   */
	IDfSession idfsession;
	IDfClient client;
	IDfSessionManager sMgr;
	IDfLoginInfo loginInfo;
	String docbase;
	String username;
	
	  public DctmSession(String username, String password, String docbase)throws DfServiceException, DfAuthenticationException{
		  	setDocbase(docbase);
		  	setUsername(username);
		  	
		  	
		  	client = DfClient.getLocalClientEx();

		  	//.getClientNetworkLocations
			sMgr = client.newSessionManager(); 
			
			loginInfo = new DfLoginInfo();
			loginInfo.setUser(username);
			loginInfo.setPassword(password);
			sMgr.setIdentity( docbase, loginInfo );
			
			setLogininfo(loginInfo);
			idfsession = sMgr.getSession( docbase );
	  }
	
	  
	  
	  /*
	  public void setClient(){
		  client = DfClient.getLocalClientEx();
	  }
	  
	  public void setSessionManager(){
		  sMgr = client.newSessionManager(); 
	  }
	  
	  public void setLoginInfo(String username, String password){
		  loginInfo = new DfLoginInfo();
		  loginInfo.setUser(username);
		  loginInfo.setPassword(password);
	  }
	  */
	  
	  public IDfLoginInfo getLoginInfo(){
		  return loginInfo;
	  }
	  
	  public String getDocbase(){
		  return docbase;
	  }
	  
	  public String getUsername(){
		  return username;
	  }
	  
	  public void setLogininfo(IDfLoginInfo loginInfo){
		  this.loginInfo=loginInfo;
	  }
	  
	  public void setDocbase(String  docbase){
		  this.docbase=docbase;
	  }
	  
	  public void setUsername(String  username){
		  this.username=username;
	  }
	  
	  
	  public IDfSession getSession(){
		  return idfsession;
	  }
	  
	  public QueryTraversalManager getQueryTraversalManager(){
		  QueryTraversalManager DctmQtm=new DctmQueryTraversalManager(idfsession);
		  return DctmQtm;
	  }
	  
	  /**
	   * Gets an AuthenticationManager.  It is permissible to return null.  
	   * A null return means that this implementation does not support an
	   * Authentication Manager.  This may be for one of these reasons:
	   * <ul>
	   * <li> Authentication is not needed for this data source
	   * <li> Authentication is handled through another GSA-supported mechanism,
	   * such as LDAP
	   * </ul>
	   * @return    a AuthenticationManager - may be null
	   * @throws RepositoryException
	   */
	  public AuthenticationManager getAuthenticationManager() {
		  AuthenticationManager DctmAm=new DctmAuthenticationManager(idfsession);
		  return DctmAm;
	  }
	  
	  /**
	   * Gets an AuthorizationManager.  It is permissible to return null.  
	   * A null return means that this implementation does not support an
	   * Authorization Manager.  This may be for one of these reasons:
	   * <ul>
	   * <li> Authorization is not needed for this data source - all documents are
	   * public
	   * <li> Authorization is handled through another GSA-supported mechanism,
	   * such as NTLM or Basic Auth
	   * </ul>
	   * @return    a AuthorizationManager - may be null
	   * @throws RepositoryException
	   */
	  public AuthorizationManager getAuthorizationManager(){
		  AuthorizationManager DctmAzm=new DctmAuthorizationManager(idfsession);
		  return DctmAzm;
	  }

}
