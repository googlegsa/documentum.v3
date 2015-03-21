// Copyright 2006 Google Inc.
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

import com.google.common.annotations.VisibleForTesting;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.AuthenticationIdentity;
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.AuthorizationResponse;
import com.google.enterprise.connector.spi.RepositoryException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DctmAuthorizationManager implements AuthorizationManager {
  private static Logger logger =
      Logger.getLogger(DctmAuthorizationManager.class.getName());

  private static final String QUERY_STRING =
      "select for read i_chronicle_id from dm_sysobject "
      + "where i_chronicle_id in (";

  @VisibleForTesting
  static final String QUERY_STRING_OR = ") or i_chronicle_id in (";

  private final IClientX clientX;

  private final ISessionManager sessionManager;

  private final String docbase;

  public DctmAuthorizationManager(IClientX clientX,
      ISessionManager sessionManager, String docbase) {
    this.clientX = clientX;
    this.sessionManager = sessionManager;
    this.docbase = docbase;
  }

  @Override
  public Collection<AuthorizationResponse> authorizeDocids(
      Collection<String> docids, AuthenticationIdentity identity)
      throws RepositoryException {
    String username = getCanonicalUsername(identity);
    logger.info("authorisation for: " + username + "; docbase: " + docbase);

    IQuery query = buildQuery(docids);

    List<AuthorizationResponse> authorized;
    ISessionManager sessionManagerUser = getSessionManagerUser(username);
    ISession sessionUser = sessionManagerUser.getSession(docbase);
    try {
      authorized = getAuthorizedDocids(docids, query, sessionUser);
    } finally {
      sessionManagerUser.release(sessionUser);
      logger.finest("user session released");
    }
    return authorized;
  }

  private String getCanonicalUsername(AuthenticationIdentity identity) {
    String username = identity.getUsername();
    if (logger.isLoggable(Level.FINE))
      logger.fine("username: " + username);

    /// Makes the connector handle the patterns username@domain,
    /// domain\\username and username.
    int index = username.indexOf('@');
    if (index != -1) {
      username = username.substring(0, index);
      if (logger.isLoggable(Level.FINE))
        logger.fine("username contains @ and is now: " + username);
    }
    index = username.indexOf('\\');
    if (index != -1) {
      username = username.substring(index + 1);
      if (logger.isLoggable(Level.FINE))
        logger.fine("username contains \\ and is now: " + username);
    }

    return username;
  }

  @VisibleForTesting
  String buildQueryString(Collection<String> docidList) {
    StringBuilder queryString = new StringBuilder();
    queryString.append(QUERY_STRING);
    int i = 0;
    for (String docid : docidList) {
      queryString.append('\'');
      queryString.append(docid);
      queryString.append('\'');

      // Check for YACC stack overflow: The DQL parser has a stack size of
      // 500 and each IN entry counts toward that limit. Be conservative
      // and split the IN conditions after 400 entries. Note the size check
      // to make sure we're not on the last ID in the list.
      i++;
      if (i == 400 && i < docidList.size()) {
        i = 0;
        queryString.append(QUERY_STRING_OR);
      } else {
        queryString.append(',');
      }
    }
    queryString.setCharAt(queryString.length() - 1, ')');
    return queryString.toString();
  }

  private IQuery buildQuery(Collection<String> docidList) {
    String queryString = buildQueryString(docidList);

    IQuery query = clientX.getQuery();
    if (logger.isLoggable(Level.FINE))
      logger.fine("dql: " + queryString);
    query.setDQL(queryString);
    return query;
  }

  /**
   * Gets a session manager for the given user.
   *
   * @param username a user name
   * @return a session manager for the given user
   */
  private ISessionManager getSessionManagerUser(String username)
      throws RepositoryException {
    // Login tickets fail for superusers if restrict_su_ticket_login
    // is set to T in the server config object. This code at least
    // allows the configured superuser to perform searches.
    ISessionManager sessionManagerUser;
    String currentUsername = sessionManager.getIdentity(docbase).getUser();
    if (username.equals(currentUsername)) {
      if (logger.isLoggable(Level.FINE))
        logger.fine("Using current session manager for " + username);
      sessionManagerUser = sessionManager;
    } else {
      if (logger.isLoggable(Level.FINE))
        logger.fine("Creating new session manager for " + username);
      String ticket;
      ISession session = sessionManager.getSession(docbase);
      try {
        ticket = session.getLoginTicketEx(username, "docbase", 0, false, null);
      } finally {
        sessionManager.release(session);
      }

      sessionManagerUser = clientX.getLocalClient().newSessionManager();
      ILoginInfo loginInfo = clientX.getLoginInfo();
      loginInfo.setUser(username);
      loginInfo.setPassword(ticket);
      sessionManagerUser.setIdentity(docbase, loginInfo);
    }
    return sessionManagerUser;
  }

  private List<AuthorizationResponse> getAuthorizedDocids(
      Collection<String> docids, IQuery query, ISession sessionUser)
      throws RepositoryException {
    List<AuthorizationResponse> authorized;
    ICollection collec = query.execute(sessionUser, IQuery.READ_QUERY);
    try {
      ArrayList<String> object_id = new ArrayList<String>(docids.size());
      while (collec.next()) {
        object_id.add(collec.getString("i_chronicle_id"));
      }
      authorized = new ArrayList<AuthorizationResponse>(docids.size());
      for (String id : docids) {
        boolean isAuthorized = object_id.contains(id);
        logger.info("id " + id + " hasRight? " + isAuthorized);
        authorized.add(new AuthorizationResponse(isAuthorized, id));
      }
    } finally {
      collec.close();
      logger.finest("after collec.close");
    }
    return authorized;
  }
}
