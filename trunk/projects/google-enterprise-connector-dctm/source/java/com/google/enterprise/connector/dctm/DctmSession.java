package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

public class DctmSession implements Session {
	IClientX clientX;

	IClient client;

	ISessionManager sessionManager;

	ISession session;

	protected String webtopServerUrl;

	String docbase;

	/**
	 * 
	 * @param client
	 * @param login
	 * @param password
	 * @param docbase
	 * @throws RepositoryException
	 */

	public DctmSession(String clientX, String login, String password,
			String docbase, String wsu) throws RepositoryException {
		if (DebugFinalData.debugInEclipse) {
			System.out.println("--- DctmSession constructor with arguments---");
		}
		ILoginInfo dctmLoginInfo = null;

		if (DebugFinalData.debugInTomcat) {
			OutputPerformances.setPerfFlag("a", "- builds an IClient", null);
		}
		setClientX(clientX);
		if (DebugFinalData.debugInTomcat) {
			OutputPerformances.endFlag("a", "");
		}

		if (DebugFinalData.debugInTomcat) {
			OutputPerformances.setPerfFlag("a", "- builds an ILocalClient",
					null);
		}
		client = this.clientX.getLocalClient();
		if (DebugFinalData.debugInTomcat) {
			OutputPerformances.endFlag("a", "");
		}

		if (DebugFinalData.debugInTomcat) {
			OutputPerformances.setPerfFlag("a", "- builds an ISessionManager",
					null);
		}
		sessionManager = this.client.newSessionManager();
		if (DebugFinalData.debugInTomcat) {
			OutputPerformances.endFlag("a", "");
		}

		if (DebugFinalData.debugInTomcat) {
			OutputPerformances.setPerfFlag("a", "- builds credential objects",
					null);
		}
		dctmLoginInfo = this.clientX.getLoginInfo();
		dctmLoginInfo.setUser(login);
		dctmLoginInfo.setPassword(password);
		sessionManager.setIdentity(docbase, dctmLoginInfo);
		if (DebugFinalData.debugInTomcat) {
			OutputPerformances.endFlag("a", "");
		}

		if (DebugFinalData.debugInTomcat) {
			OutputPerformances.setPerfFlag("a",
					"- opens an authenticated ISession", null);
		}
		session = sessionManager.newSession(docbase);
		this.clientX.setSessionManager(sessionManager);
		sessionManager.release(session);
		if (DebugFinalData.debugInEclipse) {
			System.out.println("--- DctmSession avant setSessionManager ---");
		}
		if (DebugFinalData.debugInTomcat) {
			OutputPerformances.endFlag("a", "");
		}

		webtopServerUrl = wsu;
		sessionManager.setDocbaseName(docbase);
		sessionManager.setServerUrl(wsu);
	}

	public QueryTraversalManager getQueryTraversalManager()
			throws RepositoryException {
		if (DebugFinalData.debugInEclipse) {
			System.out.println("--- DctmSession getQueryTraversalManager---");
		}

		DctmQueryTraversalManager dctmQtm = null;

		if (DebugFinalData.debugInTomcat)
			OutputPerformances.setPerfFlag("a",
					"DctmQueryTraversalManager's instantiation", null);
		{

			dctmQtm = new DctmQueryTraversalManager(clientX, webtopServerUrl);

		}

		dctmQtm = new DctmQueryTraversalManager(clientX, webtopServerUrl);
		if (DebugFinalData.debugInTomcat) {
			OutputPerformances.endFlag("a",
					"DctmQueryTraversalManager's instantiation");
		}
		if (DebugFinalData.debugInEclipse) {
			System.out
					.println("--- DctmSession getQueryTraversalManager client vaut"
							+ client.getClass() + "---");
		}

		return dctmQtm;
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
		boolean repoExcep = false;
		Throwable rootCause = null;
		String message = "";
		StackTraceElement[] stack = null;
		IClientX cl = null;
		try {
			cl = (IClientX) Class.forName(clientX).newInstance();
		} catch (InstantiationException e) {
			repoExcep = true;
			rootCause = e.getCause();
			message = e.getMessage();
			stack = e.getStackTrace();
		} catch (IllegalAccessException e) {
			repoExcep = true;
			rootCause = e.getCause();
			message = e.getMessage();
			stack = e.getStackTrace();
		} catch (ClassNotFoundException e) {
			repoExcep = true;
			rootCause = e.getCause();
			message = e.getMessage();
			stack = e.getStackTrace();
		}
		if (repoExcep) {
			RepositoryException re = new RepositoryException(message, rootCause);
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
