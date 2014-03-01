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

  private final JdbcFixture jdbcFixture = new JdbcFixture();

  @Override
  protected void setUp() throws RepositoryException, SQLException {
    Connector connector = new DctmConnector();
    ((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
    ((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
    ((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
    ((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
    ((DctmConnector) connector).setGoogleGlobalNamespace("global");
    ((DctmConnector) connector).setGoogleLocalNamespace("local");
    ((DctmConnector) connector)
        .setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    ((DctmConnector) connector).setIs_public("true");
    Session sess = (DctmSession) connector.login();

    authentManager = (DctmAuthenticationManager) sess
        .getAuthenticationManager();

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
    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2, null))
        .isValid());
    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_KO, null))
        .isValid());
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

  private void insertUser(String name) throws SQLException {
    jdbcFixture.executeUpdate(
        String.format("insert into dm_user"
            + "(user_name, user_login_name, user_source, user_ldap_dn) "
            + "values('%s', '%s', '', '')",
            name, name));
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
    insertUser(DmInitialize.DM_LOGIN_OK1);
    insertUser(DmInitialize.DM_LOGIN_OK2);
    insertGroup("grp1", DmInitialize.DM_LOGIN_OK1, "user3", "user4");
    insertGroup("grp2", "user1", DmInitialize.DM_LOGIN_OK1, "user2");
    insertGroup("grp3", "user2", "user3", DmInitialize.DM_LOGIN_OK1);
    insertGroup("grp3", "user3", "user4");
  }

  public void testGroupLookup_only() throws Exception {
    groupSetUp();

    AuthenticationResponse result =
        authentManager.authenticate(new SimpleAuthenticationIdentity(
            DmInitialize.DM_LOGIN_OK1, null));
    assertTrue(result.isValid());
    Collection<?> groups = (Collection<?>) result.getGroups();
    assertEquals(ImmutableList.of("grp1", "grp2", "grp3", "dm_world"),
        toStrings(groups));
  }

  public void testGroupLookup_multipleGroups() throws Exception {
    groupSetUp();

    AuthenticationResponse result =
        authentManager.authenticate(new SimpleAuthenticationIdentity(
            DmInitialize.DM_LOGIN_OK1, DmInitialize.DM_PWD_OK1));
    assertTrue(result.isValid());
    Collection<?> groups = (Collection<?>) result.getGroups();
    assertEquals(ImmutableList.of("grp1", "grp2", "grp3", "dm_world"),
        toStrings(groups));
  }

  public void testGroupLookup_onlyWorld() throws Exception {
    groupSetUp();

    AuthenticationResponse result2 =
        authentManager.authenticate(new SimpleAuthenticationIdentity(
            DmInitialize.DM_LOGIN_OK2, DmInitialize.DM_PWD_OK2));
    assertTrue(result2.isValid());
    Collection<?> groups2 =
        (Collection<?>) result2.getGroups();
    assertEquals(ImmutableList.of("dm_world"), toStrings(groups2));
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
