package com.google.enterprise.connector.dctm;

import java.util.List;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.spi.*;

public class DctmAuthorizationManager implements AuthorizationManager {
	ISession session;

	IClient client;

	/**
	 * @param args
	 */
	public DctmAuthorizationManager() {

	}

	public DctmAuthorizationManager(ISession session, IClient client) {
		setSession(session);
		setClient(client);
	}

	public ResultSet authorizeDocids(List docidList, String username)
			throws RepositoryException {
		int i = 0;
		DctmResultSet resultSet = null;
		SimplePropertyMap docmap = null;
		IQuery query = this.getClient().getQuery();
		String dqlQuery = ""; 
		ICollection collec = null;

		String ticket = session.getLoginTicketForUser(username);
		ILoginInfo logInfo = client.getLoginInfo();
		logInfo.setUser(username);
		logInfo.setPassword(ticket);
		ISession sessionUser = client.newSession(session.getDocbaseName(),
				logInfo);

		dqlQuery = buildQuery(docidList, dqlQuery);
		System.out.println(dqlQuery);
		query.setDQL(dqlQuery);
		collec = (ICollection) query.execute(sessionUser, IQuery.DF_READ_QUERY);
		String ids = "";
		while (collec != null && collec.next()) {
			ids += collec.getString(DctmInstantiator.ATTRIBUTE_NAME) + " ";
		}
		resultSet = new DctmResultSet();
		for (i = 0; i < docidList.size(); i++) {
			docmap = new SimplePropertyMap();
			docmap.putProperty(new SimpleProperty(SpiConstants.PROPNAME_DOCID, docidList
					.get(i).toString()));
			docmap.putProperty(new SimpleProperty(SpiConstants.PROPNAME_AUTH_VIEWPERMIT,
					(ids.indexOf(docidList.get(i).toString()) != -1)));
			resultSet.add(docmap);
		}
		
		return resultSet;

	}

	private String buildQuery(List docidList, String dqlQuery) {
		int i;
		dqlQuery = DctmInstantiator.QUERY_STRING_AUTHORISE_DEFAULT;
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

	public void setSession(ISession session) {
		this.session = session;
	}

	public IClient getClient() {
		return client;
	}

	public void setClient(IClient client) {
		this.client = client;
	}

}
