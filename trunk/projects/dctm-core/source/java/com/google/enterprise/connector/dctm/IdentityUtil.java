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

import java.util.logging.Level;
import java.util.logging.Logger;

public class IdentityUtil {
  private static final Logger LOGGER = 
      Logger.getLogger(IdentityUtil.class.getName());

  /**
   * Given a dn, it returns the first domain.
   * E.g., DN: uid=xyz,ou=engineer,dc=corp.example,dc=com
   * it will return corp
   * 
   * This method is derived from getDomainFromDN, and needs to be in sync with
   * google3/java/com/google/enterprise/secmgr/ldap/LDAPClient
   * 
   * @param dn the distinguished name
   * @return first domain, or null if the input was invalid or
   *    did not contain the domain attribute
   */
  public static String getFirstDomainFromDN(String dn) {
    if (Strings.isNullOrEmpty(dn)) {
      return null;
    }

    Iterable<String> str =
        Splitter.on(',').trimResults().omitEmptyStrings().split(dn);
    for (String substr : str) {
      if (substr.startsWith("dc") || substr.startsWith("DC")) {
        return substr.substring(3);
      }
    }
    return null;
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
