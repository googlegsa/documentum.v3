// Copyright 2007 Google Inc. All Rights Reserved.
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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmSessionManager;
import com.google.enterprise.connector.spi.AuthenticationResponse;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.Principal;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SimpleAuthenticationIdentity;

import junit.framework.TestCase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class DctmMockAuthenticationManagerTest extends TestCase {
  DctmAuthenticationManager authentManager;

  private Connector connector;
  private final JdbcFixture jdbcFixture = new JdbcFixture();

  protected Connector setupConnectorConfig()
      throws RepositoryException, SQLException {
    connector = new DctmConnector();
    ((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
    ((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
    ((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
    ((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
    ((DctmConnector) connector).setGoogleGlobalNamespace("global");
    ((DctmConnector) connector).setGoogleLocalNamespace("local");
    ((DctmConnector) connector)
        .setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    ((DctmConnector) connector).setIs_public("true");

    return connector;
  }

  @Override
  protected void setUp() throws RepositoryException, SQLException {
    setupConnectorConfig();
    Session sess = (DctmSession) connector.login();
    authentManager =
        (DctmAuthenticationManager) sess.getAuthenticationManager();

    jdbcFixture.setUp();
  }

  protected void tearDown() throws SQLException {
    try {
      MockDmSessionManager.tearDown();
    } finally {
      jdbcFixture.tearDown();
    }
  }

  public void testAuthenticate() throws RepositoryException {
    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK1,
            DmInitialize.DM_PWD_OK1)).isValid());
    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2,
            DmInitialize.DM_PWD_KO)).isValid());
    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2,
            DmInitialize.DM_PWD_OK2)).isValid());
    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2,
            DmInitialize.DM_PWD_KO)).isValid());
    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_KO,
            DmInitialize.DM_PWD_KO)).isValid());
    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(null, DmInitialize.DM_PWD_OK1))
        .isValid());
    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(null, null)).isValid());

    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK3,
            DmInitialize.DM_PWD_OK3)).isValid());
    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK1,
            DmInitialize.DM_PWD_OK1)).isValid());
    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK5,
            DmInitialize.DM_PWD_OK5)).isValid());

    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2_DOMAIN,
            DmInitialize.DM_PWD_KO)).isValid());
    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2_DNS_DOMAIN,
            DmInitialize.DM_PWD_OK2_DNS_DOMAIN)).isValid());
  }

  private void insertUser(String name, String login, String source,
      String dn) throws SQLException {
    jdbcFixture.executeUpdate(
        String.format("insert into dm_user"
            + "(user_name, user_login_name, user_source, user_ldap_dn) "
            + "values('%s', '%s', '%s', '%s')",
            name, login, source, dn));
  }

  private void insertGroup(String group, String... members)
      throws SQLException {
    for (String user : members) {
      jdbcFixture.executeUpdate(
          String.format("insert into dm_group(group_name, i_all_users_names) "
              + "values('%s', '%s')",
              group, user));
    }
  }

  private void groupSetUp() throws SQLException {
    assertFalse("joseph".equals(DmInitialize.DM_LOGIN_OK1));
    assertFalse("margaret".equals(DmInitialize.DM_LOGIN_OK2));

    insertUser("joseph", DmInitialize.DM_LOGIN_OK1, "", "");
    insertUser("margaret", DmInitialize.DM_LOGIN_OK2, "", "");
    insertGroup("grp1", "joseph", "user3", "user4");
    insertGroup("grp2", "user1", "joseph", "user2");
    insertGroup("grp3", "user2", "user3", "joseph");
    insertGroup("grp3", "user3", "user4");
  }

  public void testGroupLookup_only() throws Exception {
    groupSetUp();

    AuthenticationResponse result =
        authentManager.authenticate(new SimpleAuthenticationIdentity(
            DmInitialize.DM_LOGIN_OK1, null));
    assertTrue(result.isValid());
    Collection<?> groups = result.getGroups();
    assertEquals(ImmutableList.of("grp1", "grp2", "grp3", "dm_world"),
        toStrings(groups));
  }

  public void testGroupLookup_badUser() throws Exception {
    groupSetUp();

    AuthenticationResponse result =
        authentManager.authenticate(new SimpleAuthenticationIdentity(
            DmInitialize.DM_LOGIN_KO, null));
    assertFalse(result.isValid());
  }

  public void testGroupLookup_authentication() throws Exception {
    groupSetUp();

    AuthenticationResponse result =
        authentManager.authenticate(new SimpleAuthenticationIdentity(
            DmInitialize.DM_LOGIN_OK1, DmInitialize.DM_PWD_OK1));
    assertTrue(result.isValid());
    Collection<?> groups = result.getGroups();
    assertEquals(ImmutableList.of("grp1", "grp2", "grp3", "dm_world"),
        toStrings(groups));
  }

  public void testGroupLookup_onlyWorld() throws Exception {
    groupSetUp();

    AuthenticationResponse result =
        authentManager.authenticate(new SimpleAuthenticationIdentity(
            DmInitialize.DM_LOGIN_OK2, DmInitialize.DM_PWD_OK2));
    assertTrue(result.isValid());
    Collection<?> groups = result.getGroups();
    assertEquals(ImmutableList.of("dm_world"), toStrings(groups));
  }

  /** Tests for a leaked session when a query throws an exception. */
  public void testGroupLookup_exception() throws Exception {
    // This username is impossible, but it triggers an exception in
    // the group lookup query, so we can test the exception handling.
    insertUser("d''oh!", "homer", "", "");

    try {
      authentManager.authenticate(
          new SimpleAuthenticationIdentity("homer", null));
      fail("Expected an exception");
    } catch (RepositoryException expected) {
      if (!expected.getMessage().startsWith("Database error")) {
        throw expected;
      }
    }
  }

  private void domainSetUp() throws SQLException {
    insertUser("localuser", "localuser", "", "");
    insertUser("otheruser", "someuser", "", "");
    insertUser("acmeuser", "ldapuser", "LDAP",
        "CN=LDAP User,dc=acme,dc=example,dc=com");
    insertUser("ajaxuser", "ldapuser", "LDAP",
        "CN=LDAP User,dc=ajax,dc=example,dc=com");
    insertUser("aussieuser", "ldapuser", "LDAP",
        "CN=LDAP User,dc=ajax,dc=example,dc=com,dc=au");

    insertGroup("localgroup", "localuser");
    insertGroup("localgroup", "otheruser");
    insertGroup("ldapgroup", "acmeuser");
    insertGroup("ldapgroup", "ajaxuser");
    insertGroup("invalids", "someuser");
    insertGroup("invalids", "ldapuser");
    insertGroup("invalids", "aussieuser");
  }

  private void testDomain(String user, String domain, String... expectedGroups)
      throws Exception {
    domainSetUp();

    testGroupLookup(user, domain, expectedGroups);
  }

  /**
   * Helper method tests group lookup only and verifies that the given
   * groups (along with dm_world) are returned for the user.
   */
  private void testGroupLookup(String user, String domain,
      String... expectedGroups) throws Exception {
    AuthenticationResponse result = authentManager.authenticate(
        new SimpleAuthenticationIdentity(user, null, domain));
    assertTrue(result.isValid());
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    assertEquals(builder.add(expectedGroups).add("dm_world").build(),
        toStrings(result.getGroups()));
  }

  private void testDomainFail(String user, String domain)
      throws Exception {
    domainSetUp();

    testGroupLookupFail(user, domain);
  }

  /** Helper method tests group lookup only and verifies that it fails. */
  private void testGroupLookupFail(String user, String domain)
      throws Exception {
    AuthenticationResponse result = authentManager.authenticate(
        new SimpleAuthenticationIdentity(user, null, domain));
    assertFalse(result.isValid());
  }

  public void testDomain_userLogin() throws Exception {
    testDomain("someuser", "", "localgroup");
  }

  public void testDomain_noDomain() throws Exception {
    testDomain("localuser", "", "localgroup");
  }

  public void testDomain_differentDomain() throws Exception {
    testDomainFail("ldapuser", "non-existent");
  }

  public void testDomain_windowsLowercase() throws Exception {
    testDomain("ldapuser", "acme", "ldapgroup");
  }

  public void testDomain_windowsUppercase() throws Exception {
    testDomain("ldapuser", "ACME", "ldapgroup");
  }

  public void testDomain_windowsSubstring() throws Exception {
    testDomainFail("ldapuser", "me");
  }

  public void testDomain_dnsLowercase() throws Exception {
    testDomain("ldapuser", "acme.example.com", "ldapgroup");
  }

  public void testDomain_dnsExtraTld() throws Exception {
    testDomain("ldapuser", "ajax.example.com", "ldapgroup");
  }

  public void testDomain_ambiguousNoDomain() throws Exception {
    testDomainFail("ldapuser", "");
  }

  public void testDomain_ambiguousDomain() throws Exception {
    testDomainFail("ldapuser", "ajax");
  }

  public void testLdapInjection_windowsDomain() throws Exception {
    testDomainFail("ldapuser", "acme,dc=example");
  }

  public void testLdapInjection_dnsDomain() throws Exception {
    testDomainFail("ldapuser", "acme,dc=example.com");
  }

  /**
   * Note: The H2 default escape character is disabled for the JDBC
   * connection, to match the DQL behavior.
   *
   * @see JdbcFixture#getSharedConnection
   */
  private void ldapSetUp() throws SQLException {
    insertUser("acmeuser", "ldapuser", "LDAP",
        "CN=LDAP User,dc=dc\\=acme,dc=example,dc=com");
    // Note ou, looking for false positives.
    insertUser("ajaxuser", "ldapuser", "LDAP",
        "CN=LDAP User,ou=dc\\=ajax,dc=example,dc=com");
    insertGroup("ldapgroup", "acmeuser");
    insertGroup("ldapgroup", "ajaxuser");
  }

  public void testLdapEscaping_windows() throws Exception {
    ldapSetUp();

    testGroupLookup("ldapuser", "dc=acme", "ldapgroup");
  }

  public void testLdapEscaping_dns() throws Exception {
    ldapSetUp();

    testGroupLookup("ldapuser", "dc=acme.example.com", "ldapgroup");
  }

  public void testLdapEscaping_partialFail() throws Exception {
    ldapSetUp();

    testGroupLookupFail("ldapuser", "ajax");
  }

  public void testLdapEscaping_fullFail() throws Exception {
    ldapSetUp();

    testGroupLookupFail("ldapuser", "dc=ajax");
  }

  public void testLdapEscaping_escapedFail() throws Exception {
    ldapSetUp();

    testGroupLookupFail("ldapuser", "dc\\=ajax");
  }

  public void testLdapEscaping_actualDomain() throws Exception {
    ldapSetUp();

    testGroupLookup("ldapuser", "example", "ldapgroup");
  }

  private void testParentDomainFail(String user, String domain)
      throws Exception {
    insertUser("aceuser", "ldapuser", "LDAP",
        "CN=LDAP User,dc=ace,dc=example,dc=com");

    AuthenticationResponse result = authentManager.authenticate(
        new SimpleAuthenticationIdentity(user, null, domain));
    assertFalse(result.isValid());
  }

  public void testDomain_windowsParent() throws Exception {
    testParentDomainFail("ldapuser", "example");
  }

  public void testDomain_dnsParent() throws Exception {
    testParentDomainFail("ldapuser", "example.com");
  }

  public void testSqlInjection_userNoDomain() throws Exception {
    testDomainFail("' or user_name = 'localuser", "");
  }

  public void testSqlInjection_userDomain() throws Exception {
    testDomainFail(
        "ldapuser' and user_ldap_dn like '%dc=ajax,dc=example,dc=com,dc=au' --",
        "ajax.example.com");
  }

  /** The post-processing using LdapName also blocks this. */
  public void testSqlInjection_domain() throws Exception {
    testDomainFail("ldapuser", "acme.example%");
  }

  protected void windowsDomainSetUp(String windowsDomain)
      throws RepositoryException, SQLException {
    ((DctmConnector) connector).setWindows_domain(windowsDomain);
    Session sess = (DctmSession) connector.login();
    authentManager =
        (DctmAuthenticationManager) sess.getAuthenticationManager();
  }

  /**
   * Test case with valid windows domain, no ldap domain, valid identity domain
   */
  public void testWindowsDomain_validUserDomain() throws Exception {
    windowsDomainSetUp("ajax");
    insertUser("localuser", "localuser", "", "");
    insertGroup("localgroup", "localuser");
    insertUser("ajaxuser", "ldapuser", "LDAP",
        "CN=LDAP User,dc=ajax,dc=example,dc=com");
    insertGroup("invalids", "ajaxuser");
    testGroupLookup("localuser", "ajax", "localgroup");
  }

  /**
   * Test case with valid windows domain, valid ldap domain and
   * valid identity domain
   */
  public void testWindowsDomain_validDnsDomain() throws Exception {
    windowsDomainSetUp("ajax");
    insertUser("localuser", "localuser", "", "");
    insertGroup("localgroup", "localuser");
    insertUser("ajaxuser", "ldapuser", "LDAP",
        "CN=LDAP User,dc=ajax,dc=example,dc=com");
    insertGroup("invalids", "ajaxuser");
    testGroupLookup("localuser", "ajax.example.com", "localgroup");
  }

  /**
   * Test case with valid windows domain, no ldap domain, empty identity domain
   */
  public void testWindowsDomain_emptyUserDomain() throws Exception {
    windowsDomainSetUp("ajax");
    insertUser("localuser", "localuser", "", "");
    insertUser("local user", "acmeuser", "", "");
    insertUser("ldapuser", "acmeuser", "LDAP",
        "CN=LDAP User,dc=ajax,dc=example,dc=com");
    insertGroup("localgroup", "localuser");
    insertGroup("localgroup", "acmeuser");
    insertGroup("ldapgroup", "ldapuser");
    testGroupLookup("localuser", "", "localgroup");
  }

  /**
   * Test case with valid windows domain, valid ldap domain, 
   * wrong identity domain
   */
  public void testWindowsDomain_wrongUserDomain() throws Exception {
    windowsDomainSetUp("acme");
    insertUser("localuser", "someuser", "", "");
    insertUser("ldapuser", "someuser",
        "LDAP", "CN=LDAP User,dc=acme,dc=example,dc=com");
    insertGroup("localgroup", "localuser");
    testGroupLookupFail("someuser", "ajax");
  }

  /** Test case for empty user source and unmatched dumbed down domain */
  public void testWindowsDomain_InvalidDumbeddownDomain() throws Exception {
    windowsDomainSetUp("acme");
    insertUser("localuser", "localuser", "", "");
    insertUser("local user", "someuser", "", "");
    insertGroup("localgroup", "localuser");
    testGroupLookupFail("localuser", "ajax.example.com");
  }

  /**
   * Test case with valid windows domain, ldap domain is valid and
   * valid identity domain
   */
  public void testWindowsDomain_validLdapDn_validUserDomain()
      throws Exception {
    windowsDomainSetUp("ajax");
    insertUser("ajax user", "ajaxuser", "LDAP",
        "CN=LDAP User,dc=ajax,dc=example,dc=com");
    insertUser("local user", "ajaxuser", "", "");
    insertGroup("ldapgroup", "ajax user");
    insertGroup("localgroup", "localuser");
    testGroupLookupFail("ajaxuser", "ajax");
  }

  /**
   * Test case with windows domain is valid, ldap domain is valid and
   *  identity domain doesn't match ldap domain
   */
  public void testWindowsDomain_validLdapDn_invalidUserDomain()
      throws Exception {
    windowsDomainSetUp("acme");
    insertUser("ajax user", "ajaxuser", "LDAP",
        "CN=LDAP User,dc=ajax,dc=example,dc=com");
    insertUser("localuser", "localuser", "", "");
    insertGroup("ldapgroup", "ajax user");
    insertGroup("localgroup", "localuser");
    testGroupLookupFail("ajaxuser", "acme");
  }

  /**
   * Test case with valid windows domain, valid ldap domain and
   * wrong identity domain
   */
  public void testWindowsDomain_validLdapDn_wrongUserDomain()
      throws Exception {
    windowsDomainSetUp("ajax");
    insertUser("ajax user", "ajaxuser", "LDAP",
        "CN=LDAP User,dc=ajax,dc=example,dc=com");
    insertGroup("ldapgroup", "ajax user");
    testGroupLookupFail("ajaxuser", "somedomain");
  }

  /**
   * Test case with empty windows domain, valid ldap domain and
   * valid identity domain
   */
  public void testWindowsDomain_emptyWindowsDomain_validLdapDn()
      throws Exception {
    windowsDomainSetUp("");
    insertUser("ajax user", "ajaxuser", "LDAP",
        "CN=LDAP User,dc=ajax,dc=example,dc=com");
    insertGroup("ldapgroup", "ajax user");
    testGroupLookup("ajaxuser", "ajax", "ldapgroup");
  }

  /** Test case with empty windows domain, for local use */
  public void testWindowsDomain_emptyWindowsDomain()
      throws Exception {
    windowsDomainSetUp("");
    insertUser("localuser", "localuser", "", "");
    insertGroup("localgroup", "localuser");
    testGroupLookup("localuser", "", "localgroup");
  }

  /** Test case for aussie user */
  public void testWindowsDomain_validAussieLdapDn() throws Exception {
    windowsDomainSetUp("ajax");
    insertUser("ldapuser", "ajaxuser", "", "");
    insertUser("aussie user", "ajaxuser", "LDAP",
        "CN=LDAP User,dc=ajax,dc=example,dc=com,dc=au");
    insertGroup("ldapgroup", "ldapuser");
    insertGroup("invalids", "aussie user");
    testGroupLookup("ajaxuser", "ajax.example.com", "ldapgroup");
  }

  /** Test case for checking nonexistent user */
  public void testWindowsDomain_InvalidUser() throws Exception {
    windowsDomainSetUp("ajax");
    AuthenticationResponse result = authentManager.authenticate(
        new SimpleAuthenticationIdentity("localuser", null, ""));
    assertFalse(result.isValid());
  }

  /**
   * Test case for valid non-empty user source and matching dumbed down domain
   */
  public void testWindowsDomain_unixUserSource() throws Exception {
    windowsDomainSetUp("ajax");
    insertUser("localuser", "localuser", "unixfirst", "");
    insertGroup("ldapgroup", "localuser");
    testGroupLookupFail("localuser", "ajax");
  }

  /** Test case for OR condition with invalid user and valid windows domain  */
  public void testWindowsDomain_validdomain_invalidUser() throws Exception {
    windowsDomainSetUp("ajax");
    insertUser("localuser", "localuser", "", "");
    insertGroup("invalids", "localuser");
    testGroupLookupFail("someuser", "ajax");
  }

  private Collection<String> toStrings(Collection<?> groups) {
    if (groups == null) {
      return null;
    }
    Collection<String> names = new ArrayList<String>(groups.size());
    for (Object obj : groups) {
      String name = (obj instanceof String) ? (String) obj : ((Principal)
          obj).getName();
      names.add(name);
    }
    return names;
  }

  /**
   * Compares the membership of two collections. Like set equality,
   * order is not important, but unlike set equality, duplicates are
   * consided distinct elements.
   */
  private void assertEquals(Collection<String> expected,
      Collection<String> actual) {
    assertTrue("expected:<" + expected + "> but was:<" + actual + ">",
        expected.size() == actual.size() && actual.containsAll(expected));
  }
}