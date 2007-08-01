package com.google.enterprise.connector.dctm;

import java.util.HashSet;
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

import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.AuthenticationIdentity;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.AuthorizationResponse;

public class DctmAuthorizationManager implements AuthorizationManager {

	IClientX clientX;

	ISessionManager sessionManager;

	private String attributeName = "i_chronicle_id";

	private String queryStringAuthoriseDefault = "select i_chronicle_id from dm_sysobject where i_chronicle_id in (";

	private static Logger logger = null;

	static {
		logger = Logger.getLogger(DctmAuthorizationManager.class.getName());
		logger.setLevel(Level.ALL);
	}

	public DctmAuthorizationManager(IClientX clientX) {

		setClientX(clientX);
		setSessionManager(this.clientX.getSessionManager());

	}

	public List authorizeDocids(List docidList,
			AuthenticationIdentity authenticationIdentity)
			throws RepositoryException {
		String username = authenticationIdentity.getUsername();
		IQuery query = clientX.getQuery();
		String dqlQuery = "";
		ISession session = sessionManager.getSession(sessionManager
				.getDocbaseName());
		DctmDocumentList dctmDocumentList = new DctmDocumentList();
		HashSet dctmList = new HashSet();
		try {
			ISessionManager sessionManagerUser = clientX.getLocalClient()
					.newSessionManager();
			String ticket = session.getLoginTicketForUser(username);
			ILoginInfo logInfo = clientX.getLoginInfo();
			logInfo.setUser(username);
			logInfo.setPassword(ticket);
			if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 3) {
				logger.log(Level.INFO, "authorisation for " + username);
			}
			sessionManagerUser.setIdentity(sessionManager.getDocbaseName(),
					logInfo);
			sessionManagerUser.setDocbaseName(sessionManager.getDocbaseName());

			dqlQuery = buildQuery(docidList);
			if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 3) {
				logger.log(Level.INFO, "dql " + dqlQuery);
			}
			query.setDQL(dqlQuery);

			ICollection collec = query.execute(sessionManagerUser,
					IQuery.READ_QUERY);

			String id = "";
			AuthorizationResponse authorizationResponse;
			Iterator iterDocIdList = docidList.iterator();
			String object_id = "";
			while (collec.next()) {
				object_id += collec.getString("i_chronicle_id") + " ";
			}
			while (iterDocIdList.hasNext()) {
				id = (String) iterDocIdList.next();
				if (object_id.indexOf(id) != -1) {
					if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 3) {
						logger.info("id " + id);
						logger.info("hasRight?  " + true);
					}
					authorizationResponse = new AuthorizationResponse(true, id);
				} else {
					if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 3) {
						logger.info("id " + id);
						logger.info("hasRight?  " + false);
					}
					authorizationResponse = new AuthorizationResponse(false, id);
				}
				dctmDocumentList.add(authorizationResponse);
				dctmList.add(authorizationResponse);
			}
			collec.close();
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}
		
		return dctmDocumentList;
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

	public List authorizeTokens(List tokenList, String username)
			throws RepositoryException {
		List responses = null;
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
