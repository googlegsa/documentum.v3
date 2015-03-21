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

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.spi.AuthenticationIdentity;
import com.google.enterprise.connector.spi.SimpleAuthenticationIdentity;

import junit.framework.TestCase;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

public class IdentityUtilTest extends TestCase {

  private String getFirstDomainFromDN(String dn)
      throws InvalidNameException {
    return IdentityUtil.getFirstDomainFromDN(
        IdentityUtil.getDomainComponents(dn));
  }

  public void testgetFirstDomainFromDN() throws InvalidNameException {
    try {
      IdentityUtil.getFirstDomainFromDN(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
    }

    assertEquals(null, getFirstDomainFromDN(""));

    String dnString = "CN=Test User1,CN=users";
    assertEquals(null, getFirstDomainFromDN(dnString));

    dnString = "CN=Test User1,cn=users,dc=corp,dc=example,dc=com";
    assertEquals("corp", getFirstDomainFromDN(dnString));

    dnString = "CN=Test User1, CN=users, DC=corp, DC=example, DC=com";
    assertEquals("corp", getFirstDomainFromDN(dnString));

    dnString = "CN=Test User1 , CN=users , DC=corp , DC=example , DC=com";
    assertEquals("corp", getFirstDomainFromDN(dnString));

    dnString = "CN=Test User1,CN=users,DC=North America,DC=example,DC=com";
    assertEquals("North America", getFirstDomainFromDN(dnString));

    dnString =
        "CN = Test User1,CN = users,DC = North America,DC = example,DC = com";
    assertEquals("North America", getFirstDomainFromDN(dnString));

    dnString = "CN=Test User1,CN=users,dC=North America,DC=example,DC=com";
    assertEquals("North America", getFirstDomainFromDN(dnString));
  }

  public void testgetDomainComponents() throws InvalidNameException {
    try {
      IdentityUtil.getDomainComponents(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
    }

    assertEquals(new LdapName(""), IdentityUtil.getDomainComponents(""));

    String dnString = "CN=Test User1,CN=users";
    assertEquals(new LdapName(""),
        IdentityUtil.getDomainComponents(dnString));

    dnString = "CN=Test User1,cn=users,dc=corp,dc=example,dc=com";
    assertEquals(new LdapName("dc=corp,dc=example,dc=com"),
        IdentityUtil.getDomainComponents(dnString));

    dnString = "CN=Test User1, CN=users, DC=corp, DC=example, DC=com";
    assertEquals(new LdapName("dc=corp,dc=example,dc=com"),
        IdentityUtil.getDomainComponents(dnString));

    dnString = "CN=Test User1 , CN=users , DC=corp , DC=example , DC=com";
    assertEquals(new LdapName("dc=corp,dc=example,dc=com"),
        IdentityUtil.getDomainComponents(dnString));

    dnString = "CN=Test User1,CN=users,DC=North America,DC=example,DC=com";
    assertEquals(new LdapName("dc=North America,dc=example,dc=com"),
        IdentityUtil.getDomainComponents(dnString));

    dnString =
        "CN = Test User1,CN = users,DC = North America,DC = example,DC = com";
    assertEquals(new LdapName("dc=North America,dc=example,dc=com"),
        IdentityUtil.getDomainComponents(dnString));

    dnString = "CN=Test User1,CN=users,dC=North America,DC=example,DC=com";
    assertEquals(new LdapName("dc=North America,dc=example,dc=com"),
        IdentityUtil.getDomainComponents(dnString));

    try {
      dnString = ",,,DC=corp,,DC=example,DC=com";
      IdentityUtil.getDomainComponents(dnString);
      fail("Expected InvalidNameException");
    } catch (InvalidNameException expected) {
    }

    try {
      dnString = "CN=Test User1,,,DC=example,DC=com";
      IdentityUtil.getDomainComponents(dnString);
      fail("Expected InvalidNameException");
    } catch (InvalidNameException expected) {
    }
  }

  public void testGetCanonicalUsername() {
    AuthenticationIdentity identity =
        new SimpleAuthenticationIdentity("", DmInitialize.DM_PWD_OK1);
    String result = IdentityUtil.getCanonicalUsername(identity);
    assertEquals(null, result);

    identity =
        new SimpleAuthenticationIdentity(null, DmInitialize.DM_PWD_OK1);
    result = IdentityUtil.getCanonicalUsername(identity);
    assertEquals(null, result);

    identity =
        new SimpleAuthenticationIdentity("Joe@corp", DmInitialize.DM_PWD_OK1);
    result = IdentityUtil.getCanonicalUsername(identity);
    assertEquals("Joe", result);

    identity =
        new SimpleAuthenticationIdentity("Mary@corp.example.com",
            DmInitialize.DM_PWD_OK1);
    result = IdentityUtil.getCanonicalUsername(identity);
    assertEquals("Mary", result);
  }

  public void testGetDomain() {
    AuthenticationIdentity identity =
        new SimpleAuthenticationIdentity("", DmInitialize.DM_PWD_OK1);
    String result = IdentityUtil.getDomain(identity);
    assertEquals(null, result);

    identity =
        new SimpleAuthenticationIdentity("Joe@corp", DmInitialize.DM_PWD_OK1);
    result = IdentityUtil.getDomain(identity);
    assertEquals("corp", result);

    identity =
        new SimpleAuthenticationIdentity("Mary@entops.example.com",
            DmInitialize.DM_PWD_OK1);
    result = IdentityUtil.getDomain(identity);
    assertEquals("entops", result);

    identity =
        new SimpleAuthenticationIdentity("Mary@entops.example.com",
            DmInitialize.DM_PWD_OK1, "");
    result = IdentityUtil.getDomain(identity);
    assertEquals("entops", result);

    identity =
        new SimpleAuthenticationIdentity("Mary@entops.example.com",
            DmInitialize.DM_PWD_OK1, "eng");
    result = IdentityUtil.getDomain(identity);
    assertEquals("eng", result);
  }
}
