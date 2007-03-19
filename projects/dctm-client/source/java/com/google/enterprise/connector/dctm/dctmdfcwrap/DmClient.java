package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

import com.google.enterprise.connector.dctm.dfcwrap.IQuery;

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
