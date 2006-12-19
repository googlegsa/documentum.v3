package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmClient;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLocalClient;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLoginInfo;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSession;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

public class DctmSession implements Session{
	IClient dctmClient;
	ILocalClient dctmLocalClient;
	ISessionManager dctmsessionmanager;
	ISession dctmsession;
	String docbase;
	
	  public DctmSession(){
		  	IDctmLoginInfo dctmLoginInfo=null;
		  	docbase="gdoc";
		  	
		  	
		  	dctmClient=new IDctmClient();
		  	dctmLocalClient=dctmClient.getLocalClientEx();
		  	dctmsessionmanager=dctmLocalClient.newSessionManager(); 
		  	dctmLoginInfo=new IDctmLoginInfo();
		  	dctmLoginInfo.setUser("emilie");
		  	dctmLoginInfo.setPassword("emilie2");
		  	dctmsessionmanager.setIdentity(docbase,dctmLoginInfo);
		  	dctmsession=dctmsessionmanager.newSession(docbase);
	  }
	
	
	  public QueryTraversalManager getQueryTraversalManager(){
		  DctmQueryTraversalManager DctmQtm=new DctmQueryTraversalManager();
		  DctmQtm.setIDctmSession((IDctmSession)dctmsession);
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


	public IClient getIClient() {
		return dctmClient;
	}


	public void setIClient(IDctmClient dctmClient) {
		this.dctmClient = dctmClient;
	}


	public ILocalClient getDctmLocalClient() {
		return dctmLocalClient;
	}


	public void setILocalClient(IDctmLocalClient dctmLocalClient) {
		this.dctmLocalClient = dctmLocalClient;
	}


	public ISessionManager getISessionmanager() {
		return dctmsessionmanager;
	}

	public ISession getISession() {
		return dctmsession;
	}
	
	public void setISessionmanager(IDctmSessionManager dctmsessionmanager) {
		this.dctmsessionmanager = dctmsessionmanager;
	}
}
