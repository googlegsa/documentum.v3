package com.google.enterprise.connector.dctm;

import java.util.HashSet;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.TraversalManager;

public class DctmSession implements Session {
	IClientX clientX;

	IClient client;

	ISessionManager sessionManager;

	ISession session;

	protected String webtopServerUrl;

	protected String additionalWhereClause;

	String docbase;

	boolean isPublic = false;

	private HashSet included_meta;

	private HashSet excluded_meta;

	/**
	 * 
	 * @param client
	 * @param login
	 * @param password
	 * @param docbase
	 * @param excluded_meta
	 * @param included_meta
	 * @throws RepositoryException
	 */

	public DctmSession(String clientX, String login, String password,
			String docbase, String wsu, String additionalWhereClause,
			boolean isPublic, HashSet included_meta, HashSet excluded_meta)
			throws RepositoryException {
		try {
			
			ILoginInfo dctmLoginInfo = null;

			setClientX(clientX);

			client = this.clientX.getLocalClient();

			sessionManager = this.client.newSessionManager();

			dctmLoginInfo = this.clientX.getLoginInfo();
			dctmLoginInfo.setUser(login);
			dctmLoginInfo.setPassword(password);
			sessionManager.setIdentity(docbase, dctmLoginInfo);

			session = sessionManager.newSession(docbase);
			this.clientX.setSessionManager(sessionManager);

			webtopServerUrl = wsu;
			this.additionalWhereClause = additionalWhereClause;
			sessionManager.setDocbaseName(docbase);
			sessionManager.setServerUrl(wsu);

			this.isPublic = isPublic;
			this.included_meta = included_meta;
			this.excluded_meta = excluded_meta;
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}
	}

	public TraversalManager getTraversalManager() throws RepositoryException {

		DctmTraversalManager dctmTm = null;
		dctmTm = new DctmTraversalManager(clientX, webtopServerUrl,
				additionalWhereClause, isPublic, included_meta, excluded_meta);
		return dctmTm;
	}

	/**
	 * Gets an AuthenticationManager. It is permissible to return null. A null
	 * return means that this implementation does not support an Authentication
	 * Manager. This may be for one of these reasons:
	 * <ul>
	 * <li> Authentication is not needed for this data source
	 * <li> Authentication is handled through another GSA-supported mechanism,
	 * such as LDAP
	 * </ul>
	 * 
	 * @return a AuthenticationManager - may be null
	 * @throws RepositoryException
	 */
	public AuthenticationManager getAuthenticationManager() {
		AuthenticationManager DctmAm = new DctmAuthenticationManager(
				getClientX());
		return DctmAm;
	}

	/**
	 * Gets an AuthorizationManager. It is permissible to return null. A null
	 * return means that this implementation does not support an Authorization
	 * Manager. This may be for one of these reasons:
	 * <ul>
	 * <li> Authorization is not needed for this data source - all documents are
	 * public
	 * <li> Authorization is handled through another GSA-supported mechanism,
	 * such as NTLM or Basic Auth
	 * </ul>
	 * 
	 * @return a AuthorizationManager - may be null
	 * @throws RepositoryException
	 */
	public AuthorizationManager getAuthorizationManager() {
		AuthorizationManager DctmAzm = new DctmAuthorizationManager(
				getClientX());
		return DctmAzm;
	}

	public IClientX getClientX() {
		return clientX;
	}

	public void setClientX(String clientX) throws RepositoryException {
		IClientX cl = null;
		try {
			cl = (IClientX) Class.forName(clientX).newInstance();
		} catch (InstantiationException e) {
			throw new RepositoryException(e);
		} catch (IllegalAccessException e) {
			throw new RepositoryException(e);
		} catch (ClassNotFoundException e) {
			throw new RepositoryException(e);
		} catch (NoClassDefFoundError e) {
			throw new RepositoryException(e);
		}
		this.clientX = cl;
	}

	public String getDocbase() {
		return docbase;
	}

	public void setDocbase(String docbase) {
		this.docbase = docbase;
	}

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public void setClientX(IClientX clientX) {
		this.clientX = clientX;
	}

}
