package com.google.enterprise.connector.dctm.dctmdfcwrap;



import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

import com.google.enterprise.connector.dctm.dfcwrap.ISession;

import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.spi.LoginException;

public class DmClient implements IClient {
	IDfClient idfClient;

	IDfClientX idfClientX;

	DmSessionManager dmSessionManager = null;

	public DmClient(IDfClient idfClient) {
		this.idfClient = idfClient;
	}

	public IQuery getQuery() {
		return new DmQuery(new DfQuery());
	}

	public ISession newSession(String docbase, ILoginInfo logInfo)
			throws LoginException {
		IDfSession sessionUser = null;
		IDfLoginInfo idfLogInfo = new DfLoginInfo();
		idfLogInfo.setUser(logInfo.getUser());
		idfLogInfo.setPassword(logInfo.getPassword());
		try {
			sessionUser = idfClient.newSession(docbase, idfLogInfo);
		} catch (DfException de) {
			LoginException re = new LoginException(de);
			throw re;
		}
		return new DmSession(sessionUser);
	}

	public ISessionManager getSessionManager() {
		return dmSessionManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
			dmSessionManager = (DmSessionManager) sessionManager;

	}

	public ISessionManager newSessionManager() {
		IDfSessionManager newSessionManager = idfClient.newSessionManager();
		DmSessionManager dctmSessionManager = new DmSessionManager(
				newSessionManager);
		return dctmSessionManager;
	}
}