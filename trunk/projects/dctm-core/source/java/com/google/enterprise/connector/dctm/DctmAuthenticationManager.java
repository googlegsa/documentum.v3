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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IGroup;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.IUser;
import com.google.enterprise.connector.spi.AuthenticationIdentity;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthenticationResponse;
import com.google.enterprise.connector.spi.Principal;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.SpiConstants.CaseSensitivityType;
import com.google.enterprise.connector.spi.SpiConstants.PrincipalType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

public class DctmAuthenticationManager implements AuthenticationManager {
  private static final Logger LOGGER = 
      Logger.getLogger(DctmAuthenticationManager.class.getName());

  private final IClientX clientX;

  private final String docbase;

  private final DctmConnector connector;

  public DctmAuthenticationManager(DctmConnector connector, IClientX clientX,
      String docbase) {
    this.clientX = clientX;
    this.docbase = docbase;
    this.connector = connector;
  }

  @Override
  public AuthenticationResponse authenticate(
      AuthenticationIdentity authenticationIdentity)
      throws RepositoryLoginException, RepositoryException {
    String userLoginName =
        IdentityUtil.getCanonicalUsername(authenticationIdentity);
    if (userLoginName == null) {
      return new AuthenticationResponse(false, "");
    } else {
      String userDomain = IdentityUtil.getDomain(authenticationIdentity);
      String password = authenticationIdentity.getPassword();
      ISessionManager sessionManagerUser;
      boolean authenticate;
      String userName;
      try {
        if (Strings.isNullOrEmpty(password)) {
          sessionManagerUser =
              getSessionManager(connector.getLogin(), connector.getPassword());
          //check for user existence when null password
          userName = getUserName(sessionManagerUser, userLoginName, userDomain);
          authenticate = (userName != null);
        } else {
          // TODO(jlacey): We are using the raw username from the GSA
          // here because we always have and no bugs have been reported.
          sessionManagerUser =
              getSessionManager(authenticationIdentity.getUsername(), password);

          // Use getSession instead of authenticate, so we can get the
          // authenticated user name.
          ISession session = sessionManagerUser.getSession(docbase);
          try {
            userName = session.getLoginUserName();
          } finally {
            sessionManagerUser.release(session);
          }
          authenticate = true;
        }
      } catch (RepositoryLoginException e) {
        LOGGER.finer(e.getMessage());
        return new AuthenticationResponse(false, "");
      }

      if (authenticate) {
        return new AuthenticationResponse(authenticate, "", getAllGroupsForUser(
            sessionManagerUser, userName));
      } else {
        return new AuthenticationResponse(false, "");
      }
    }
  }

  private ISessionManager getSessionManager(String username, String password)
      throws RepositoryLoginException, RepositoryException {
    ILoginInfo loginInfo = clientX.getLoginInfo();
    loginInfo.setUser(username);
    loginInfo.setPassword(password);
    ISessionManager sessionManagerUser =
        clientX.getLocalClient().newSessionManager();
    sessionManagerUser.setIdentity(docbase, loginInfo);
    return sessionManagerUser;
  }

  private String getUserName(ISessionManager sessionManager,
      String userLoginName, String userDomain)
      throws RepositoryLoginException, RepositoryException {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(userLoginName),
        "Username must not be null or empty.");
    ISession session = sessionManager.getSession(docbase);
    try {
      StringBuilder queryBuff = new StringBuilder();
      queryBuff.append("select user_name from ");
      queryBuff.append("dm_user where user_login_name = '");
      queryBuff.append(userLoginName);
      if (!Strings.isNullOrEmpty(userDomain)) {
        queryBuff.append("' and user_source = 'LDAP'");
        queryBuff.append(" and LOWER(user_ldap_dn) like '%,dc=");
        if (userDomain.indexOf('.') != -1) {
          queryBuff.append(userDomain.toLowerCase().replace(".", ",dc="));
        } else {
          queryBuff.append(userDomain.toLowerCase());
          queryBuff.append(",%");
        }
      }
      queryBuff.append("'");

      IQuery query = clientX.getQuery();
      query.setDQL(queryBuff.toString());
      ICollection users = query.execute(session, IQuery.EXECUTE_READ_QUERY);
      try {
        if (users.next() && !users.hasNext()) {
          return users.getString("user_name");
        } else {
          return null; // No users or multiple users found.
        }
      } finally {
        users.close();
      }
    } finally {
      sessionManager.release(session);
    }
  }

  /**
   * Gets all groups the user belongs to.
   *
   * @param a session manager for the given user
   * @param username a user name
   * @return Collection of Principals
   */
  private Collection<Principal> getAllGroupsForUser(
      ISessionManager sessionManager, String username)
      throws RepositoryLoginException, RepositoryException {
    ArrayList<Principal> listGroups = new ArrayList<Principal>();
    ISession session = sessionManager.getSession(docbase);
    try {
      String queryStr =
          "select group_name from dm_group where any i_all_users_names = '"
          + username + "'";
      IQuery query = clientX.getQuery();
      query.setDQL(queryStr);
      ICollection collecGroups = query.execute(session,
          IQuery.EXECUTE_READ_QUERY);
      try {
        while (collecGroups.next()) {
          String groupName = collecGroups.getString("group_name");
          String groupNamespace = getGroupNamespace(session, groupName);
          if (groupNamespace != null) {
            listGroups.add(new Principal(PrincipalType.UNKNOWN, groupNamespace,
                    groupName, CaseSensitivityType.EVERYTHING_CASE_SENSITIVE));
          } else {
            LOGGER.warning("Skipping group " + groupName
                + " with null namespace");
          }
        }
        // process special group dm_world
        listGroups.add(new Principal(PrincipalType.UNKNOWN,
                connector.getGoogleLocalNamespace(), "dm_world",
                CaseSensitivityType.EVERYTHING_CASE_SENSITIVE));
      } finally {
        collecGroups.close();
      }
    } finally {
      sessionManager.release(session);
    }

    return listGroups;
  }

  //TODO (Srinivas): refactor this method with the one in DctmAclList
  private String getGroupNamespace(ISession session, String groupName)
      throws RepositoryDocumentException {
    String localNamespace = connector.getGoogleLocalNamespace();
    String globalNamespace = connector.getGoogleGlobalNamespace();
    String groupNamespace = null;
    try {
      IGroup groupObj = (IGroup) session.getObjectByQualification(
          "dm_group where group_name = '" + groupName + "'");
      if (groupObj != null) {
        if (Strings.isNullOrEmpty(groupObj.getUserSource())) {
          LOGGER.fine("local namespace for group " + groupName);
          groupNamespace = localNamespace;
        } else {
          LOGGER.fine("global namespace for group " + groupName);
          groupNamespace = globalNamespace;
        }
      } else {
        LOGGER.fine("namespace for group is null");
        return null;
      }
    } catch (RepositoryDocumentException e) {
      LOGGER.fine("Exception in getNameSpace " + e.getMessage());
      throw e;
    }
    return groupNamespace;
  }
}
