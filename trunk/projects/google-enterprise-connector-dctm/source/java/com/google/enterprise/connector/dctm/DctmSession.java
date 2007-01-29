package com.google.enterprise.connector.dctm;

//import com.google.enterprise.connector.dctm.dctmdfcwrap.DmClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

public class DctmSession implements Session{
	//IClient client;
	IClientX clientX;
	//ILocalClient localClient;
	IClient client;
	ISessionManager sessionManager;
	ISession session;
	private String queryStringUnboundedDefault,queryStringBoundedDefault,queryStringAuthoriseDefault,webtopServerUrl,attributeName;
	
	String docbase;
	
	
	//Constructor never called except for tests
	public DctmSession() throws RepositoryException{
		/*System.out.println("--- DctmSession constructor without arguments---");
		ILoginInfo dctmLoginInfo=null;
		client = new DmClient();
		docbase = "gsadctm";
		localClient = client.getLocalClientEx();
		sessionManager = localClient.newSessionManager(); 
		dctmLoginInfo = client.getLoginInfo();
		dctmLoginInfo.setUser("queryUser");
		dctmLoginInfo.setPassword("p@ssw0rd");
		sessionManager.setIdentity(docbase,dctmLoginInfo);
		session = sessionManager.newSession(docbase);
		sessionManager.release(session);*/
		//this.client.setSession(session);
	}
	
	/**
	 * 
	 * @param client
	 * @param login
	 * @param password
	 * @param docbase
	 * @throws RepositoryException
	 */
	public DctmSession(String clientX, String login, String password, String docbase, String qsud, 
			String qsbd, String qsad, String an, String wsu) throws RepositoryException{
		System.out.println("--- DctmSession constructor with arguments---");
		ILoginInfo dctmLoginInfo=null;
		
		if (DebugFinalData.debug){ OutputPerformances.setPerfFlag(this,"- builds an IClient");}
		
		setClientX(clientX);
		
		if (DebugFinalData.debug){ OutputPerformances.endFlag(this,"");}
		if (DebugFinalData.debug){ OutputPerformances.setPerfFlag(this,"- builds an ILocalClient");}
		
		client=this.clientX.getLocalClient();
		
		if (DebugFinalData.debug){ OutputPerformances.endFlag(this,"");}
		if (DebugFinalData.debug){ OutputPerformances.setPerfFlag(this,"- builds an ISessionManager");}
		
		sessionManager=this.client.newSessionManager();
		
		if (DebugFinalData.debug){ OutputPerformances.endFlag(this,"");}
		if (DebugFinalData.debug){ OutputPerformances.setPerfFlag(this,"- builds credential objects");}
		
		dctmLoginInfo = this.clientX.getLoginInfo();
		dctmLoginInfo.setUser(login);
		dctmLoginInfo.setPassword(password);
		sessionManager.setIdentity(docbase,dctmLoginInfo);
		
		if (DebugFinalData.debug){ OutputPerformances.endFlag(this,"");}
		if (DebugFinalData.debug){ OutputPerformances.setPerfFlag(this,"- opens an authenticated ISession");}
		session=sessionManager.newSession(docbase);
		
		this.clientX.setSessionManager(sessionManager);
		sessionManager.release(session);
		
		System.out.println("--- DctmSession avant setSessionManager ---");
		
		
		if (DebugFinalData.debug){ OutputPerformances.endFlag(this,"");}
		
		queryStringUnboundedDefault = qsud;
		queryStringBoundedDefault = qsbd;
		queryStringAuthoriseDefault = qsad;
		webtopServerUrl = wsu;
		attributeName = an;
		sessionManager.setDocbaseName(docbase);
		sessionManager.setServerUrl(wsu);
	}
	
	
	
	public QueryTraversalManager getQueryTraversalManager() throws RepositoryException{
		System.out.println("--- DctmSession getQueryTraversalManager---");
		DctmQueryTraversalManager dctmQtm = null;
//		session = sessionManager.getSession(docbase);
		if (DebugFinalData.debug) OutputPerformances.setPerfFlag(this,"DctmQueryTraversalManager's instantiation");{
			
			dctmQtm = new DctmQueryTraversalManager(clientX,
					queryStringUnboundedDefault,queryStringBoundedDefault,webtopServerUrl);
		}
		if (DebugFinalData.debug) OutputPerformances.endFlag(this,"DctmQueryTraversalManager's instantiation");{

		}
		
		System.out.println("--- DctmSession getQueryTraversalManager client vaut"+client.getClass()+"---");
		
		return dctmQtm;
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
		AuthenticationManager DctmAm = new DctmAuthenticationManager(getClientX());
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
		AuthorizationManager DctmAzm = new DctmAuthorizationManager(getClientX(),
				queryStringAuthoriseDefault,attributeName);
		return DctmAzm;
	}
	
	
	public IClientX getClientX() {
		return clientX;
	}
		
	public void setClientX(String clientX) throws RepositoryException {
	boolean repoExcep = false;
	Throwable rootCause=null;
	String message="";
	StackTraceElement[] stack = null;
	IClientX cl = null;
	try {
		cl = (IClientX) Class.forName(clientX).newInstance();
	} catch (InstantiationException e) {
		repoExcep=true;
		rootCause=e.getCause();
		message=e.getMessage();
		stack=e.getStackTrace();
	} catch (IllegalAccessException e) {
		repoExcep=true;
		rootCause=e.getCause();
		message=e.getMessage();
		stack=e.getStackTrace();
	} catch (ClassNotFoundException e) {
		repoExcep=true;
		rootCause=e.getCause();
		message=e.getMessage();
		stack=e.getStackTrace();
	}
	if (repoExcep) {
		RepositoryException re = new RepositoryException(message,rootCause);
		re.setStackTrace(stack);
		throw re;
	}
		this.clientX = cl;
	}


	public String getDocbase() {
		return docbase;
	}

	public void setDocbase(String docbase) {
		this.docbase = docbase;
	}
}
