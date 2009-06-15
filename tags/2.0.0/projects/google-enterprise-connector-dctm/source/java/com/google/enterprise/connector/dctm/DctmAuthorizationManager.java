// Copyright (C) 2006-2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.dctm;

import java.util.ArrayList;
import java.util.Collection;
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

  private String queryStringAuthoriseDefault = "select for read i_chronicle_id from dm_sysobject where i_chronicle_id in (";

  private static Logger logger =
      Logger.getLogger(DctmAuthorizationManager.class.getName());

  public DctmAuthorizationManager(IClientX clientX) {
    setClientX(clientX);
    setSessionManager(this.clientX.getSessionManager());
  }

  public Collection<AuthorizationResponse> authorizeDocids(
      Collection<String> docids, AuthenticationIdentity authenticationIdentity)
      throws RepositoryException {
    String username = authenticationIdentity.getUsername();
    logger.info("username: " + username);

    IQuery query = clientX.getQuery();

    ISession session =
        sessionManager.getSession(sessionManager.getDocbaseName());
    logger.info("docbase: " + sessionManager.getDocbaseName());

    ISessionManager sessionManagerUser =
        clientX.getLocalClient().newSessionManager();

    ///makes the connector handle the patterns username@domain, domain\\username and username
    if (username.matches(".*@.*")) {
      username = username.substring(0, username.indexOf('@'));
      logger.info("username contains @ and is now: " + username);
    }

    if (username.matches(".*\\\\.*")) {
      username = username.substring(username.indexOf("\\") + 1, username.length());
      logger.info("username contains \\ and is now: " + username);
    }

    String ticket = session.getLoginTicketForUser(username);
    logger.info("ticket: " + ticket);
    ILoginInfo logInfo = clientX.getLoginInfo();
    logInfo.setUser(username);
    logInfo.setPassword(ticket);

    logger.log(Level.INFO, "authorisation for: " + username);

    sessionManagerUser.setIdentity(sessionManager.getDocbaseName(),
          logInfo);
    sessionManagerUser.setDocbaseName(sessionManager.getDocbaseName());

    String dqlQuery = buildQuery(docids);
    logger.info("dql: " + dqlQuery);
    query.setDQL(dqlQuery);

    List<AuthorizationResponse> authorized =
        new ArrayList<AuthorizationResponse>(docids.size());
    ICollection collec = null;
    try {
      ISession sessionUser =
          sessionManagerUser.getSession(sessionManager.getDocbaseName());
      logger.fine("set the SessionAuto for the sessionManagerUser");
      sessionManagerUser.setSessionAuto(sessionUser);

      collec = query.execute(sessionUser, IQuery.READ_QUERY);

      ArrayList<String> object_id = new ArrayList<String>(docids.size());
      while (collec.next()) {
        object_id.add(collec.getString("i_chronicle_id"));
      }
      for (String id : docids) {
        boolean isAuthorized = object_id.contains(id);
        logger.info("id " + id + " hasRight? " + isAuthorized);
        authorized.add(new AuthorizationResponse(isAuthorized, id));
      }

      collec.close();
      logger.fine("after collec.close");
    } finally {
      logger.fine("in finally");
      if (collec != null && collec.getSession() != null ) {
        logger.fine("collec getSession not null");
        sessionManagerUser.releaseSessionAuto();
        logger.fine("session of sessionManagerUser released");
      }
      if (session != null) {
        logger.fine("session not null");
        sessionManager.release(session);
        logger.fine("session of sessionManager released");
      }
    }
    return authorized;
  }

  private String buildQuery(Collection<String> docidList) {
    StringBuilder queryString = new StringBuilder();

    queryString.append(queryStringAuthoriseDefault);
    for (String docid : docidList) {
      queryString.append("'");
      queryString.append(docid);
      queryString.append("',");
    }
    queryString.setCharAt(queryString.length() - 1, ')');

    return queryString.toString();
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
