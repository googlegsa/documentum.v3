package com.google.enterprise.connector.dctm;

import java.util.List;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.*;

public class DctmAuthorizationManager implements AuthorizationManager {
	ISessionManager sessionManager;

	IClient client;

	private String ATTRIBUTE_NAME;

	private String QUERY_STRING_AUTHORISE_DEFAULT;

	/**
	 * @param args
	 */
	public DctmAuthorizationManager() {

	}

	public DctmAuthorizationManager(IClient client, String qsad, String attrName) {
		// setSession(session);
		setClient(client);
		/* set */QUERY_STRING_AUTHORISE_DEFAULT = qsad;
		/* set */ATTRIBUTE_NAME = attrName;
		sessionManager = client.getSessionManager();
	}

	/*
	 * public void setAttributeName(String an){ ATTRIBUTE_NAME=an; } public void
	 * setQUERY_STRING_AUTHORISE_DEFAULT(String qsad){
	 * QUERY_STRING_AUTHORISE_DEFAULT=qsad; }
	 */

	public ResultSet authorizeDocids(List docidList, String username)
			throws RepositoryException {
		System.out.println("DCTMAuthorize method authorizeDocids");
		int i = 0;
		DctmResultSet resultSet = null;
		SimplePropertyMap docmap = null;
		IQuery query = this.getClient().getQuery();
		String dqlQuery = "";
		ICollection collec = null;
		ISession session;
		
			session = sessionManager
					.getSession(sessionManager.getDocbaseName());

			ISessionManager sessionManagerUser = client.getLocalClientEx()
					.newSessionManager();
			String ticket = session.getLoginTicketForUser(username);
			ILoginInfo logInfo = client.getLoginInfo();
			logInfo.setUser(username);
			logInfo.setPassword(ticket);
			sessionManagerUser.setIdentity(sessionManager.getDocbaseName(),
					logInfo);
			sessionManagerUser.setDocbaseName(sessionManager.getDocbaseName());
			// ISession sessionUser =
			// client.newSession(session.getDocbaseName(),
			// logInfo);
			
			dqlQuery = buildQuery(docidList, dqlQuery);
			System.out.println("dql " + dqlQuery);
			query.setDQL(dqlQuery);
			collec = (ICollection) query.execute(sessionManagerUser,
					IQuery.DF_READ_QUERY);
			String ids = "";
			while (collec != null && collec.next()) {
				ids += collec.getString(/* DctmInstantiator. */ATTRIBUTE_NAME)
						+ " ";
			}
			System.out.println("hasRight?  " + docidList.size());
			resultSet = new DctmResultSet();
			for (i = 0; i < docidList.size(); i++) {
				docmap = new SimplePropertyMap();
				docmap.putProperty(new DctmSimpleProperty(
						SpiConstants.PROPNAME_DOCID, docidList.get(i)
								.toString()));
				docmap.putProperty(new DctmSimpleProperty(
						SpiConstants.PROPNAME_AUTH_VIEWPERMIT, (ids
								.indexOf(docidList.get(i).toString()) != -1)));
				System.out.println("hasRight?  "
						+ (ids.indexOf(docidList.get(i).toString()) != -1));
				resultSet.add(docmap);
			}
		
		return resultSet;

	}

	private String buildQuery(List docidList, String dqlQuery) {
		int i;
		dqlQuery = /* DctmInstantiator. */QUERY_STRING_AUTHORISE_DEFAULT;
		System.out.println(dqlQuery);
		for (i = 0; i < docidList.size() - 1; i++) {
			dqlQuery += "'" + docidList.get(i).toString() + "', ";

		}
		dqlQuery += "'" + docidList.get(i).toString() + "')";
		return dqlQuery;
	}

	public ResultSet authorizeTokens(List tokenList, String username)
			throws RepositoryException {
		ResultSet responses = null;
		return responses;
	}

	// public void setSession(ISession session) {
	// this.session = session;
	// }

	public IClient getClient() {
		return client;
	}

	public void setClient(IClient client) {
		this.client = client;
	}

}
