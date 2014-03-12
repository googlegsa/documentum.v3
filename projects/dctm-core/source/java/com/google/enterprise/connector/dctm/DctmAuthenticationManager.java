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
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

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

    // Construct a DN for the domain, which is used in both the query
    // and the results post-processing. Note this works for both
    // NetBIOS and DNS domains.
    LdapName domainName = toLdapName(userDomain);

    ISession session = sessionManager.getSession(docbase);
    try {
      StringBuilder queryBuff = new StringBuilder();
      queryBuff.append("select user_name, user_ldap_dn from ");
      queryBuff.append("dm_user where user_login_name = '");
      queryBuff.append(userLoginName);
      if (!domainName.isEmpty()) {
        queryBuff.append("' and user_source = 'LDAP'");
        queryBuff.append(" and LOWER(user_ldap_dn) like '%,");
        queryBuff.append(domainName);
        if (domainName.size() == 1) { // NetBIOS domain
          queryBuff.append(",%");
        }
      }
      queryBuff.append("'");

      IQuery query = clientX.getQuery();
      query.setDQL(queryBuff.toString());
      ICollection users = query.execute(session, IQuery.EXECUTE_READ_QUERY);
      try {
        // The DQL query can only confirm partial matches, and not
        // exact matches. For brevity, we loop over all the users in
        // case we want to log them, and we check domainName.isEmpty()
        // on each iteration.
        ArrayList<String> matches = new ArrayList<String>();
        while (users.next()) {
          String userLdapDn = users.getString("user_ldap_dn");
          if (domainName.isEmpty()
              || domainMatchesUser(domainName, userLdapDn)) {
            matches.add(users.getString("user_name"));
          } else if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("Rejecting non-matching domain " + domainName
                + " for user " + userLdapDn);
          }
        }
        if (matches.size() == 1) {
          return matches.get(0);
        } else {
          LOGGER.log(Level.FINER, "No users or multiple users found: {0}",
              matches);
          return null;
        }
      } finally {
        users.close();
      }
    } finally {
      sessionManager.release(session);
    }
  }

  /**
   * Converts a NetBIOS or DNS domain string to an LDAP distinguished
   * name including just the DC attributes, all in lowercase.
   */
  private LdapName toLdapName(String userDomain)
      throws RepositoryLoginException{
    String[] domainComponents = (Strings.isNullOrEmpty(userDomain))
        ? new String[0] : userDomain.toLowerCase().split("\\.");

    ArrayList<Rdn> domainRdns = new ArrayList<Rdn>(domainComponents.length);
    try {
      for (String dc : domainComponents) {
        domainRdns.add(new Rdn("dc", dc));
      }
    } catch (InvalidNameException e) {
      throw new RepositoryLoginException("Invalid domain " + userDomain, e);
    }
    Collections.reverse(domainRdns); // RDN lists are in reverse order.
    return new LdapName(domainRdns);
  }

  /**
   * Gets whether the given domain matches the domain in the user DN.
   *
   * @param domainName the parsed GSA identity domain DN
   * @param userDn the Documentum user LDAP DN
   */
  private boolean domainMatchesUser(LdapName domainName, String userDn) {
    try {
      // Extract the DC RDNs from the userDn.
      LdapName userName = new LdapName(userDn);
      ArrayList<Rdn> userDnDomain = new ArrayList<Rdn>(userName.size());
      for (Rdn rdn : userName.getRdns()) {
        if (rdn.getType().equalsIgnoreCase("dc")) {
          userDnDomain.add(rdn);
        }
      }

      // LDAP numbers RDNs from right to left, and we want a match on
      // the left (starting with the subdomain), that is, on the end.
      return new LdapName(userDnDomain).endsWith(domainName);
    } catch (InvalidNameException e) {
      LOGGER.log(Level.WARNING,
          "Error matching domain " + domainName + " for user " + userDn, e);
      return false;
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
