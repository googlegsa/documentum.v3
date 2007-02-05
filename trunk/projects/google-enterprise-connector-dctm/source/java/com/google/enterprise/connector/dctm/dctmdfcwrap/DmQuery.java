package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmQuery implements IQuery {

	IDfQuery idfQuery;

	public static int DF_READ_QUERY = IDfQuery.DF_READ_QUERY;

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
		System.out.println("--- DmQuery execute ---");

		if (!(sessionManager instanceof DmSessionManager)) {
			throw new IllegalArgumentException();
		}

		DmSession idctmsession = null;
		System.out.println("--- docbase vaut "
				+ sessionManager.getDocbaseName());
		idctmsession = (DmSession) sessionManager.getSession(sessionManager
				.getDocbaseName());

		IDfSession idfSession = idctmsession.getDfSession();
		IDfCollection DfCollection = null;
		System.out.println("--- IdfQuery vaut " + idfQuery.getDQL());
		try {
			DfCollection = idfQuery.execute(idfSession, queryType);
		} catch (DfException de) {

			RepositoryException re = new RepositoryException(de);
			re.setStackTrace(de.getStackTrace());
			throw re;
		}
		return new DmCollection(DfCollection);
	}

}
