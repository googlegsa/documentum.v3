package com.google.enterprise.connector.dctm;

import java.util.ArrayList;
import java.util.Collection;
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
import com.google.enterprise.connector.spi.RepositoryLoginException;

public class DctmAuthorizationManager implements AuthorizationManager {

	IClientX clientX;

	ISessionManager sessionManager;

	private String attributeName = "i_chronicle_id";

	private String queryStringAuthoriseDefault = "select i_chronicle_id from dm_sysobject where i_chronicle_id in (";

	private static Logger logger = null;

	static {
		logger = Logger.getLogger(DctmAuthorizationManager.class.getName());
	}

	public DctmAuthorizationManager(IClientX clientX) {

		setClientX(clientX);
		setSessionManager(this.clientX.getSessionManager());

	}

	public Collection authorizeDocids(Collection docids,
			AuthenticationIdentity authenticationIdentity) throws RepositoryException{
		String username = authenticationIdentity.getUsername();
		logger.info("username :" + username);
		ICollection collec =null;
		
		ISessionManager sessionManagerUser = null;
		ISession session = null;
		ISession sessionUser = null;
		DctmDocumentList dctmDocumentList = new DctmDocumentList();
		
		IQuery query = clientX.getQuery();
		String dqlQuery = "";
		List docidList = new ArrayList(docids);
		
		try{
			session = sessionManager.getSession(sessionManager
				.getDocbaseName());
		
			logger.info("docbase :" + sessionManager
				.getDocbaseName());
		
		
			sessionManagerUser = clientX.getLocalClient()
					.newSessionManager();
			
			
			///makes the connector handle the patterns username@domain, domain\\username and username

			
			logger.info("username :" + username);
			
			if (username.matches(".*@.*")){
				logger.info("username contains @");
				username=username.substring(0,username.indexOf('@'));
				logger.info("username contains @ and is now :"+username);
			}
			
			if (username.matches(".*\\.*")){
				logger.info("username contains \\");
				username=username.substring(username.indexOf("\\")+1,username.length());
				logger.info("username contains \\ and is now :"+username);
			}
			
			String ticket = session.getLoginTicketForUser(username);
			logger.info("ticket :" + ticket);
			ILoginInfo logInfo = clientX.getLoginInfo();
			logInfo.setUser(username);
			logInfo.setPassword(ticket);

			logger.log(Level.INFO, "authorisation for " + username);

			sessionManagerUser.setIdentity(sessionManager.getDocbaseName(),
					logInfo);
			sessionManagerUser.setDocbaseName(sessionManager.getDocbaseName());

			dqlQuery = buildQuery(docidList);

			logger.log(Level.INFO, "dql " + dqlQuery);

			query.setDQL(dqlQuery);

			
			
			///collec = query.execute(sessionManagerUser,IQuery.READ_QUERY);
			
			sessionUser = sessionManagerUser.getSession(sessionManager.getDocbaseName());
			logger.info("set the SessionAuto for the sessionManagerUser");
			sessionManagerUser.setSessionAuto(sessionUser);
			
			collec = query.execute(sessionUser,IQuery.READ_QUERY);
			

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
					logger.info("id " + id);
					logger.info("hasRight?  " + true);

					authorizationResponse = new AuthorizationResponse(true, id);
				} else {
					logger.info("id " + id);
					logger.info("hasRight?  " + false);

					authorizationResponse = new AuthorizationResponse(false, id);
				}
				dctmDocumentList.add(authorizationResponse);
				logger.info("after dctmDocumentList.add");
			}
			collec.close();
			logger.info("after collec.close");
		}finally {
			logger.info("in finally");
			if(collec.getSession() != null ){
				logger.info("collec getSession not null");
				///sessionManagerUser.release(collec.getSession());
				sessionManagerUser.releaseSessionAuto();
				logger.info("session of sessionManagerUser released");
			}
			if (session != null) {
				logger.info("session not null");
				sessionManager.release(session);
				logger.info("session of sessionManager released");
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
