package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
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
	IClient client;
	ILocalClient localClient;
	ISessionManager sessionManager;
	ISession session;
	private String QUERY_STRING_UNBOUNDED_DEFAULT,QUERY_STRING_BOUNDED_DEFAULT,QUERY_STRING_AUTHORISE_DEFAULT,WEBTOP_SERVER_URL,ATTRIBUTE_NAME;
	
	String docbase;
	
	
	//Constructor never called except for tests
	public DctmSession() throws RepositoryException{
		ILoginInfo dctmLoginInfo=null;
		client = new IDctmClient();
		docbase = "gsadctm";
		localClient = client.getLocalClientEx();
		sessionManager = localClient.newSessionManager(); 
		dctmLoginInfo = client.getLoginInfo();
		dctmLoginInfo.setUser("queryUser");
		dctmLoginInfo.setPassword("p@ssw0rd");
		sessionManager.setIdentity(docbase,dctmLoginInfo);
		session = sessionManager.newSession(docbase);
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
	public DctmSession(String client, String login, String password, String docbase, String qsud, 
			String qsbd, String qsad, String an, String wsu) throws RepositoryException{
		ILoginInfo dctmLoginInfo=null;
		setClient(client);
		localClient=this.client.getLocalClientEx();
		sessionManager=localClient.newSessionManager(); 
		dctmLoginInfo = this.client.getLoginInfo();
		dctmLoginInfo.setUser(login);
		dctmLoginInfo.setPassword(password);
		sessionManager.setIdentity(docbase,dctmLoginInfo);
		session=sessionManager.newSession(docbase);
		QUERY_STRING_UNBOUNDED_DEFAULT = qsud;
		QUERY_STRING_BOUNDED_DEFAULT = qsbd;
		QUERY_STRING_AUTHORISE_DEFAULT = qsad;
		WEBTOP_SERVER_URL = wsu;
		ATTRIBUTE_NAME = an;
		//this.client.setSession(session);
	}
	
	
	
	public QueryTraversalManager getQueryTraversalManager() throws RepositoryException{
		DctmQueryTraversalManager DctmQtm = new DctmQueryTraversalManager(client,session.getSessionId(),
				QUERY_STRING_UNBOUNDED_DEFAULT,QUERY_STRING_BOUNDED_DEFAULT,WEBTOP_SERVER_URL);
		client.setSession(session);

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
		AuthenticationManager DctmAm = new DctmAuthenticationManager(getSession(),getClient());
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
		AuthorizationManager DctmAzm = new DctmAuthorizationManager(getSession(),getClient(),
				QUERY_STRING_AUTHORISE_DEFAULT,ATTRIBUTE_NAME);
		return DctmAzm;
	}
	
	
	public IClient getClient() {
		return client;
	}
	
	
	public void setClient(String client) throws RepositoryException {
	boolean repoExcep = false;
	Throwable rootCause=null;
	String message="";
	StackTraceElement[] stack = null;
	IClient cl = null;
	try {
		cl = (IClient) Class.forName(client).newInstance();
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
		this.client = cl;
	}
	
	public ISession getSession() {
		return session;
	}
	
	public void setSession(ISession session) {
		this.session = session;
	}

	public String getDocbase() {
		return docbase;
	}

	public void setDocbase(String docbase) {
		this.docbase = docbase;
	}
}
