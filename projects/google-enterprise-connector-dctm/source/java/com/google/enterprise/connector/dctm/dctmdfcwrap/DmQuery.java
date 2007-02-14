package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.DctmResultSet;
import com.google.enterprise.connector.dctm.DctmSysobjectPropertyMap;
import com.google.enterprise.connector.dctm.DebugFinalData;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;

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

	///public ICollection execute(ISessionManager sessionManager, int queryType)
	public ResultSet execute(ISessionManager sessionManager, int queryType,  IClientX clientX)
			throws RepositoryException {
		if (DebugFinalData.debugInEclipse) {
			System.out.println("--- DmQuery execute ---");
		}

		if (!(sessionManager instanceof DmSessionManager)) {
			throw new IllegalArgumentException();
		}

		DmSession idctmsession = null;
		if (DebugFinalData.debugInEclipse) {
			System.out.println("--- docbase vaut "
					+ sessionManager.getDocbaseName());
		}
		idctmsession = (DmSession) sessionManager.getSession(sessionManager
				.getDocbaseName());

		IDfSession idfSession = idctmsession.getDfSession();
		IDfCollection DfCollection = null;
		if (DebugFinalData.debugInEclipse) {
			System.out.println("--- IdfQuery vaut " + idfQuery.getDQL());
		}
		try {
			DfCollection = idfQuery.execute(idfSession, queryType);
		} catch (DfException de) {

			RepositoryException re = new RepositoryException(de);
			re.setStackTrace(de.getStackTrace());
			throw re;
		}
		
		///return new DmCollection(DfCollection);
		ICollection col = new DmCollection(DfCollection);
		String crID = null;
		int counter = 0;
		DctmSysobjectPropertyMap pm = null;
		DctmResultSet resu = new DctmResultSet();
		if (DebugFinalData.debugInEclipse) {
			System.out.println("--- docbasename vaut "
					+ sessionManager.getDocbaseName() + " ---");
		}
		DmSession session = (DmSession) sessionManager
				.getSession(sessionManager.getDocbaseName());

		while (col.next()) {

			crID = col.getValue("r_object_id").asString();

			pm = new DctmSysobjectPropertyMap(crID, sessionManager, clientX);
			counter++;
			resu.add(pm);
		}
		sessionManager.release(session);
		if (DebugFinalData.debugInEclipse) {
			System.out
					.println("--- DmQuery execute END---");
		}
		return resu;
		
	}

}
