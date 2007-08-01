package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.DctmConnector;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmQuery implements IQuery {

	IDfQuery idfQuery;


	private static Logger logger = null;

	static {
		logger = Logger.getLogger(DmQuery.class.getName());
		logger.setLevel(Level.ALL);
	}

	public DmQuery(IDfQuery idfQuery) {
		this.idfQuery = idfQuery;
	}

	public DmQuery() {
		this.idfQuery = new DfQuery();
	}

	public void setDQL(String dqlStatement) {

		idfQuery.setDQL(dqlStatement);
	}

	public ICollection execute(ISessionManager sessionManager, int queryType)
			throws RepositoryException {
		if (!(sessionManager instanceof DmSessionManager)) {
			throw new IllegalArgumentException();
		}

		DmSessionManager dmSessionManager = (DmSessionManager) sessionManager;
		IDfSessionManager idfSessionmanager = dmSessionManager.getDfSessionManager();

		
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
			logger.info("value of IdfQuery " + idfQuery.getDQL());
		}
		IDfSession idfSession = null;
		IDfCollection dfCollection = null;
		try {
			idfSession = idfSessionmanager.getSession(sessionManager.getDocbaseName());
			dfCollection = idfQuery.execute(idfSession, queryType);
			
		} catch (DfException de) {
			throw new RepositoryException(de);
		} 
		return new DmCollection(dfCollection);

	}

}
