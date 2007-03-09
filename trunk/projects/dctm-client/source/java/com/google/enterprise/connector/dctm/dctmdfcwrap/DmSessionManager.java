package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.DfIdentityException;
import com.documentum.fc.client.DfPrincipalException;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import com.google.enterprise.connector.dctm.DctmConnector;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmSessionManager implements ISessionManager {

	IDfSessionManager dfSessionManager;

	private String docbaseName;

	private String serverUrl;
	
	private static Logger logger = null;

	static {
		logger = Logger.getLogger(DmSessionManager.class.getName());
		logger.setLevel(Level.ALL);
	}

	public DmSessionManager(IDfSessionManager DfSessionManager) {

		this.dfSessionManager = DfSessionManager;
	}

	public ISession getSession(String docbase) throws LoginException,
			RepositoryException {
		IDfSession DfSession = null;
		try {
			DfSession = dfSessionManager.getSession(docbase);
		} catch (DfIdentityException iE) {
			LoginException le = new LoginException(iE);
			throw le;
		} catch (DfAuthenticationException iE) {
			LoginException le = new LoginException(iE);
			throw le;
		} catch (DfPrincipalException iE) {
			LoginException le = new LoginException(iE);
			throw le;
		} catch (DfServiceException iE) {
			RepositoryException re = new RepositoryException(iE);
			throw re;
		}
		return new DmSession(DfSession);
	}

	public void setIdentity(String docbase, ILoginInfo identity)
			throws LoginException {
		if (!(identity instanceof DmLoginInfo)) {
			throw new IllegalArgumentException();
		}
		DmLoginInfo dctmLoginInfo = (DmLoginInfo) identity;
		IDfLoginInfo idfLoginInfo = dctmLoginInfo.getIdfLoginInfo();
		try {
			dfSessionManager.setIdentity(docbase, idfLoginInfo);
		} catch (DfServiceException iE) {
			LoginException le = new LoginException(iE);
			throw le;
		}
	}

	public ILoginInfo getIdentity(String docbase) {
		IDfLoginInfo idfLoginInfo = dfSessionManager.getIdentity(docbase);
		return new DmLoginInfo(idfLoginInfo);
	}

	public ISession newSession(String docbase) throws LoginException,
			RepositoryException {
		IDfSession idfSession = null;
		String error = null;
		try {
			idfSession = dfSessionManager.newSession(docbase);
		} catch (DfIdentityException iE) {
			LoginException le = new LoginException(iE);
			throw le;
		} catch (DfAuthenticationException iE) {
			LoginException le = new LoginException(iE);
			throw le;
		} catch (DfPrincipalException iE) {
			LoginException le = new LoginException(iE);
			throw le;
		} catch (DfServiceException iE) {
			RepositoryException re = new RepositoryException(iE);
			throw re;
		}
		if (error != null) {
			return null;
		}
		return new DmSession(idfSession);
	}

	public void release(ISession session) {
		this.dfSessionManager.release(((DmSession) session).getDfSession());

	}

	public IDfSessionManager getDfSessionManager() {
		return dfSessionManager;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
		

	}

	public String getDocbaseName() {

		return docbaseName;
	}

	public void setDocbaseName(String docbaseName) {
		this.docbaseName = docbaseName;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public boolean authenticate(String docbaseName) {
		boolean authent = false;
		try {
			this.dfSessionManager.authenticate(docbaseName);
			authent = true;
		} catch (DfException e) {
			if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL==1) {
				logger.info("trace DFException authenticate");
			}

			authent = false;
		}
		return authent;
	}

	public void clearIdentity(String docbase) {
		this.dfSessionManager.clearIdentity(docbase);
	}

}
