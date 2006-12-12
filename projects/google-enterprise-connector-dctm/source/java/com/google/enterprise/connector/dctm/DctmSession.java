package com.google.enterprise.connector.dctm;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.common.DfLoginInfo;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmClient;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLocalClient;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLoginInfo;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSession;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSessionManager;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

public class DctmSession implements Session{
	IDctmClient dctmClient;
	IDctmLocalClient dctmLocalClient;
	IDctmSessionManager dctmsessionmanager;
	String docbase;
	
	  public DctmSession(){
		  	IDctmLoginInfo dctmLoginInfo=null;
		  	docbase="gdoc";
		  	
		  	dctmClient=new IDctmClient();
		  	dctmLocalClient=(IDctmLocalClient)dctmClient.getLocalClientEx();
		  	dctmsessionmanager=(IDctmSessionManager)dctmLocalClient.newSessionManager(); 
		  	dctmLoginInfo=new IDctmLoginInfo();
		  	dctmLoginInfo.setUser("emilie");
		  	dctmLoginInfo.setPassword("emilie2");
		  	dctmsessionmanager.setIdentity(docbase,dctmLoginInfo);
		  	
	  }
	
	
	  public QueryTraversalManager getQueryTraversalManager(){
		  DctmQueryTraversalManager DctmQtm=new DctmQueryTraversalManager();
		  DctmQtm.setIdctmses((IDctmSession)dctmsessionmanager.newSession(docbase));
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
		  AuthenticationManager DctmAm=new DctmAuthenticationManager();
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
		  AuthorizationManager DctmAzm=new DctmAuthorizationManager();
		  return DctmAzm;
	  }


	public IDctmClient getDctmClient() {
		return dctmClient;
	}


	public void setDctmClient(IDctmClient dctmClient) {
		this.dctmClient = dctmClient;
	}


	public IDctmLocalClient getDctmLocalClient() {
		return dctmLocalClient;
	}


	public void setDctmLocalClient(IDctmLocalClient dctmLocalClient) {
		this.dctmLocalClient = dctmLocalClient;
	}


	public IDctmSessionManager getDctmsessionmanager() {
		return dctmsessionmanager;
	}


	public void setDctmsessionmanager(IDctmSessionManager dctmsessionmanager) {
		this.dctmsessionmanager = dctmsessionmanager;
	}
}
