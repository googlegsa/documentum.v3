// Copyright 2013 Google Inc. All Rights Reserved.
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

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.enterprise.connector.spi.AuthenticationIdentity;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

public class IdentityUtil {
  private static final Logger LOGGER = 
      Logger.getLogger(IdentityUtil.class.getName());

  /**
   * Gets the leftmost DC from an {@code LdapName} of DC RDNs.
   * For example, given
   * <pre>
   * new LdapName("uid=xyz,ou=engineer,dc=corp.example,dc=com")
   * </pre>
   * it will return "corp".
   *
   * @param domain the domain name
   * @return the first domain component, or {@code null} if the DN
   *     does not contain a DC attribute
   * @throws InvalidNameException if the input is invalid
   */
  public static String getFirstDomainFromDN(LdapName domain) {
    if (domain.isEmpty()) {
      return null;
    } else {
      // RDNs are numbered right-to-left.
      return domain.getRdn(domain.size() - 1).getValue().toString();
    }
  }

  /**
   * Extracts the DC attributes in a DN string as an {@code LdapName}.
   *
   * @param userDn the Documentum user LDAP DN
   */
  public static LdapName getDomainComponents(String userDn)
      throws InvalidNameException{
    LdapName userName = new LdapName(userDn);
    ArrayList<Rdn> userDnDomain = new ArrayList<Rdn>(userName.size());
    for (Rdn rdn : userName.getRdns()) {
      if (rdn.getType().equalsIgnoreCase("dc")) {
        userDnDomain.add(rdn);
      }
    }
    return new LdapName(userDnDomain);
  }

  /**
   * Returns canonical user name.
   * 
   * @param identity AuthenticationIdentity for the user
   * @return canonical user name
   */
  public static String getCanonicalUsername(AuthenticationIdentity identity) {
    String username = identity.getUsername();
    if (!Strings.isNullOrEmpty(username)) {
      int index = username.indexOf('@');
      if (index != -1) {
        username = username.substring(0, index);
        if (LOGGER.isLoggable(Level.FINE)) {
          LOGGER.fine("username contains @ and is now: " + username);
        }
      } 
    } else {
      return null;
    }
    return username;
  }

  /**
   * Returns the first domain for the user. If the username is
   * of the form user@corp.example.com, this method returns 'corp'.
   * 
   * @param identity AuthenticationIdentity for the user
   * @return first domain
   */
  public static String getDomain(AuthenticationIdentity identity) {
    String username = identity.getUsername();
    String identityDomain = identity.getDomain();

    String domain = null;
    if (Strings.isNullOrEmpty(identityDomain)) {
      if (!Strings.isNullOrEmpty(username)) {
        int index = username.indexOf('@');
        if (index != -1) {
          domain = username.substring(index + 1);
          index = domain.indexOf('.');
          if (index != -1) {
            domain = domain.substring(0, index);
          }
          if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("username contains @ and domain is: " + username);
          }
        }
      }
    } else {
      domain = identityDomain;
    }
    return domain;
  }
}
