package com.google.enterprise.connector.dctm;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.*;

public class DctmAuthorizationManager implements AuthorizationManager {

	IClientX clientX;

	ISessionManager sessionManager;

	private String attributeName = "r_object_id";

	private String queryStringAuthoriseDefault = "select r_object_id from dm_sysobject where r_object_id in (";

	private static Logger logger = null;

	static {
		logger = Logger.getLogger(DctmAuthorizationManager.class.getName());
		logger.setLevel(Level.ALL);
	}

	public DctmAuthorizationManager(IClientX clientX) {

		setClientX(clientX);
		setSessionManager(this.clientX.getSessionManager());

	}

	public ResultSet authorizeDocids(List docidList, String username)
			throws RepositoryException {

		IQuery query = clientX.getQuery();
		String dqlQuery = "";
		ISession session;

		session = sessionManager.getSession(sessionManager.getDocbaseName());

		ISessionManager sessionManagerUser = clientX.getLocalClient()
				.newSessionManager();
		String ticket = session.getLoginTicketForUser(username);
		ILoginInfo logInfo = clientX.getLoginInfo();
		logInfo.setUser(username);
		logInfo.setPassword(ticket);
		System.out.println("ticket vaut "+ticket);
		System.out.println("username vaut "+username);
		System.out.println("docbase vaut "+sessionManager.getDocbaseName());
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 3) {
			logger.log(Level.INFO, "authorisation for " + username);
		}
		sessionManagerUser
				.setIdentity(sessionManager.getDocbaseName(), logInfo);
		sessionManagerUser.setDocbaseName(sessionManager.getDocbaseName());

		dqlQuery = buildQuery(docidList);
		System.out.println("dqlQuery vaut "+dqlQuery);
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 3) {
			logger.log(Level.INFO, "dql " + dqlQuery);
		}
		query.setDQL(dqlQuery);

		ICollection collec = query.execute(sessionManagerUser,
				IQuery.READ_QUERY);

		SimpleResultSet simpleResultSet = new SimpleResultSet();

		String id = "";
		SimplePropertyMap simplePropertyMap;
		Iterator iterDocIdList = docidList.iterator();
		String object_id = "";
		while (collec.next()) {
			object_id += collec.getString("r_object_id") + " ";
		}
		System.out.println("object_id vaut "+object_id);
		while (iterDocIdList.hasNext()) {
			id = (String) iterDocIdList.next();
			simplePropertyMap = new SimplePropertyMap();
			simplePropertyMap.putProperty(new SimpleProperty(
					SpiConstants.PROPNAME_DOCID, new SimpleValue(
							ValueType.STRING, id)));
			if (object_id.indexOf(id) != -1) {
				if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 3) {
					logger.info("id " + id);
					logger.info("hasRight?  " + true);
				}
				simplePropertyMap.putProperty(new SimpleProperty(
						SpiConstants.PROPNAME_AUTH_VIEWPERMIT, new SimpleValue(
								ValueType.BOOLEAN, "true")));
			} else {
				if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 3) {
					logger.info("id " + id);
					logger.info("hasRight?  " + false);
				}
				simplePropertyMap.putProperty(new SimpleProperty(
						SpiConstants.PROPNAME_AUTH_VIEWPERMIT, new SimpleValue(
								ValueType.BOOLEAN, "false")));
			}
			simpleResultSet.add(simplePropertyMap);
		}

		return simpleResultSet;
	}

	private String buildQuery(List docidList) {
		int i;
		String queryString;

		queryString = queryStringAuthoriseDefault;
		for (i = 0; i < docidList.size() - 1; i++) {
			queryString += "'" + docidList.get(i).toString() + "', ";

		}
		queryString += "'" + docidList.get(i).toString() + "')";

		return queryString;
	}

	public ResultSet authorizeTokens(List tokenList, String username)
			throws RepositoryException {
		ResultSet responses = null;
		return responses;
	}

	public IClientX getClientX() {
		return clientX;
	}

	public void setClientX(IClientX clientX) {
		this.clientX = clientX;
	}

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	private void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	protected String getAttributeName() {
		return attributeName;
	}

	protected String getQueryStringAuthoriseDefault() {
		return queryStringAuthoriseDefault;
	}

	protected void setQueryStringAuthoriseDefault(
			String queryStringAuthoriseDefault) {
		this.queryStringAuthoriseDefault = queryStringAuthoriseDefault;
	}

}
