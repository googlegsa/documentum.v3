// Copyright 2013 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.dctm;

import com.google.common.collect.ImmutableSet;
import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmAcl;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmQuery;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmSessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.IAcl;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.AuthenticationResponse;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.Principal;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.SimpleAuthenticationIdentity;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.SpiConstants.AclInheritanceType;
import com.google.enterprise.connector.spi.SpiConstants.CaseSensitivityType;
import com.google.enterprise.connector.spi.SpiConstants.PrincipalType;
import com.google.enterprise.connector.spi.Value;
import com.google.enterprise.connector.spiimpl.PrincipalValue;

import junit.framework.TestCase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DctmMockAclListTest extends TestCase {
  private DctmConnector connector = null;
  private DctmTraversalManager qtm = null;

  private DctmSession dctmSession = null;

  private ISessionManager sessionManager;
  /** A releaseable session for the DctmAclList objects under test. */
  private ISession session;

  private DctmAclList aclList = null;
  private Map<String, List<Value>> aclValues;

  private final JdbcFixture jdbcFixture = new JdbcFixture();

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    connector = new DctmConnector();
    connector.setLogin(DmInitialize.DM_LOGIN_OK1);
    connector.setPassword(DmInitialize.DM_PWD_OK1);
    connector.setDocbase(DmInitialize.DM_DOCBASE);
    connector.setClientX(DmInitialize.DM_CLIENTX);
    connector.setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    connector.setIs_public("false");
    connector.setIncluded_object_type(DmInitialize.DM_INCLUDED_OBJECT_TYPE);
    connector.setIncluded_meta(DmInitialize.DM_INCLUDED_META);
    connector.setGoogleLocalNamespace(DmInitialize.DM_LOCAL_NAMESPACE);
    connector.setGoogleGlobalNamespace(DmInitialize.DM_GLOBAL_NAMESPACE);
    dctmSession = connector.login();

    qtm = dctmSession.getTraversalManager();
    qtm.setBatchHint(2);

    sessionManager = qtm.getSessionManager();
    session = sessionManager.getSession(DmInitialize.DM_DOCBASE);

    aclValues = new HashMap<String, List<Value>>();
    aclList = getAclListForTest();

    jdbcFixture.setUp();
  }

  protected void tearDown() throws SQLException {
    try {
      sessionManager.release(session);
      MockDmSessionManager.tearDown();
    } finally {
      jdbcFixture.tearDown();
    }
  }

  private DctmAclList getAclListForTest()
      throws RepositoryLoginException, RepositoryException {
    List<String> whereClauselist = new ArrayList<String>();
    Checkpoint checkpoint = new Checkpoint(whereClauselist);
    return new DctmAclList(qtm, session, null, null, checkpoint);
  }

  private DctmAclList getAclListForTest(String query)
      throws RepositoryLoginException, RepositoryException {
    List<String> whereClauselist = new ArrayList<String>();
    Checkpoint checkpoint = new Checkpoint(whereClauselist);
    return new DctmAclList(qtm, session, new MockDmQuery().executeQuery(query),
        null, checkpoint);
  }

  private void addAllowUserToAcl(MockDmAcl aclObj, String name) {
    aclObj.addAccessor(name, IAcl.DF_PERMIT_READ,
        IAcl.DF_PERMIT_TYPE_ACCESS_PERMIT, false);
  }

  private void addAllowGroupToAcl(MockDmAcl aclObj, String name) {
    aclObj.addAccessor(name, IAcl.DF_PERMIT_READ,
        IAcl.DF_PERMIT_TYPE_ACCESS_PERMIT, true);
  }

  private void addDenyUserToAcl(MockDmAcl aclObj, String name, int permit) {
    aclObj.addAccessor(name, permit, IAcl.DF_PERMIT_TYPE_ACCESS_RESTRICTION,
        false);
  }

  private void addDenyGroupToAcl(MockDmAcl aclObj, String name, int permit) {
    aclObj.addAccessor(name, permit, IAcl.DF_PERMIT_TYPE_ACCESS_RESTRICTION,
        true);
  }

  private void addRequiredGroupToAcl(MockDmAcl aclObj, String name) {
    aclObj.addAccessor(name, 0, IAcl.DF_PERMIT_TYPE_REQUIRED_GROUP, true);
  }

  private void addRequiredGroupSetToAcl(MockDmAcl aclObj, String... names) {
    for (String name : names) {
      aclObj.addAccessor(name, 0, IAcl.DF_PERMIT_TYPE_REQUIRED_GROUP_SET, true);
    }
  }

  /**
   * Asserts that the principal names in the named ACL property match
   * the expected set. Identical ordering is not required but the two
   * collections must be the same size and contain the same names.
   *
   * @param expected the expected principal names
   * @param propertyName the name of an ACL property containing PrincipalValues
   */
  private void assertAclEquals(Set<?> expected, String propertyName) {
    List<Value> values = aclValues.get(propertyName);
    assertEquals(values.toString(), expected.size(), values.size());
    for (Value listvalue : values) {
      assertTrue(values.toString(), expected.contains(
              ((PrincipalValue) listvalue).getPrincipal().getName()));
    }
  }

  private void insertUsers(String... names) throws SQLException {
    for (String name : names) {
      jdbcFixture.executeUpdate(String.format(
          "insert into dm_user(user_name, user_login_name) values('%s', '%s')",
          name, name));
    }
  }

  private void insertLdapUsers(String commonName, String domain,
      String... names) throws SQLException {
    for (String name : names) {
      jdbcFixture.executeUpdate(String.format(
          "insert into dm_user(user_name, user_login_name, user_source, "
          + "user_ldap_dn) "
          + "values('%s', '%s', 'LDAP', '%s%s,%s')",
          name, name, commonName, name, domain));
    }
  }

  /** Inserts LDAP users with no user_ldap_dn attribute. */
  private void insertNullDnLdapUsers(String... names) throws SQLException {
    for (String name : names) {
      jdbcFixture.executeUpdate(String.format(
          "insert into dm_user(user_name, user_login_name, user_source) "
          + "values('%s', '%s', 'LDAP')",
          name, name));
    }
  }

  private void insertGroup(String groupName, String... members)
      throws SQLException {
    jdbcFixture.executeUpdate(String.format(
        "insert into dm_user(user_name, r_is_group) values('%s', TRUE)",
        groupName));
    for (String user : members) {
      jdbcFixture.executeUpdate(String.format(
          "insert into dm_group(group_name, i_all_users_names) "
              + "values('%s', '%s')", groupName, user));
    }
  }

  private void disableUsers(String... names) throws SQLException {
    for (String name : names) {
      jdbcFixture.executeUpdate(String.format(
          "UPDATE dm_user SET user_state = 1 WHERE user_name = '%s'", name));
    }
  }

  private void insertAcls(MockDmAcl... acls)
      throws RepositoryException, SQLException {
    for (MockDmAcl acl : acls) {
      jdbcFixture.executeUpdate(acl.getSqlInsert());
    }
  }

  public void testAllowAcl() throws Exception {
    insertUsers("user1");
    insertGroup("group1", "user2", "user3");

    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowUserToAcl(aclObj, "user1");
    addAllowGroupToAcl(aclObj, "group1");

    aclList.processAcl(aclObj, aclValues);

    assertAclEquals(ImmutableSet.of("user1"), SpiConstants.PROPNAME_ACLUSERS);
    assertAclEquals(ImmutableSet.of("group1"), SpiConstants.PROPNAME_ACLGROUPS);

    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYUSERS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYGROUPS);
  }

  public void testAllowBrowseAcl() throws Exception {
    insertUsers("user1", "user2");
    insertGroup("group1");

    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowUserToAcl(aclObj, "user1");
    // add browse permission to user2 and group1
    aclObj.addAccessor("user2", IAcl.DF_PERMIT_BROWSE,
        IAcl.DF_PERMIT_TYPE_ACCESS_PERMIT, false);
    aclObj.addAccessor("group1", IAcl.DF_PERMIT_BROWSE,
        IAcl.DF_PERMIT_TYPE_ACCESS_PERMIT, true);

    aclList.processAcl(aclObj, aclValues);

    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLGROUPS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYGROUPS);

    assertAclEquals(ImmutableSet.of("user1"), SpiConstants.PROPNAME_ACLUSERS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLGROUPS);
  }

  public void testDenyAcl() throws Exception {
    insertUsers("user1", "user2", "user3", "user4");

    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowUserToAcl(aclObj, "user1");
    addDenyUserToAcl(aclObj, "user2", IAcl.DF_PERMIT_READ);
    addDenyUserToAcl(aclObj, "user3", IAcl.DF_PERMIT_WRITE);
    addDenyUserToAcl(aclObj, "user4", IAcl.DF_PERMIT_BROWSE);

    aclList.processAcl(aclObj, aclValues);

    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLGROUPS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYGROUPS);

    // user3 is not in allowed or denied users.
    assertAclEquals(ImmutableSet.of("user1"), SpiConstants.PROPNAME_ACLUSERS);
    assertAclEquals(ImmutableSet.of("user2", "user4"),
        SpiConstants.PROPNAME_ACLDENYUSERS);
  }

  public void testGroupAcl() throws Exception {
    insertGroup("engineering", "john");
    insertGroup("sales");
    insertGroup("marketing");

    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowGroupToAcl(aclObj, "engineering");
    addDenyGroupToAcl(aclObj, "sales", IAcl.DF_PERMIT_READ);
    addDenyGroupToAcl(aclObj, "marketing", IAcl.DF_PERMIT_VERSION);

    aclList.processAcl(aclObj, aclValues);

    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLUSERS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYUSERS);

    assertAclEquals(ImmutableSet.of("engineering"),
        SpiConstants.PROPNAME_ACLGROUPS);
    assertAclEquals(ImmutableSet.of("sales"),
        SpiConstants.PROPNAME_ACLDENYGROUPS);
  }

  public void testGroupDmWorldAcl() throws Exception {
    insertUsers(DmInitialize.DM_LOGIN_OK1);
    insertGroup("grp1", "joseph", "user3", "user4");

    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowUserToAcl(aclObj, DmInitialize.DM_LOGIN_OK1);
    addAllowGroupToAcl(aclObj, "dm_world");

    aclList.processAcl(aclObj, aclValues);

    assertAclEquals(ImmutableSet.of(DmInitialize.DM_LOGIN_OK1),
        SpiConstants.PROPNAME_ACLUSERS);
    assertAclEquals(ImmutableSet.of("dm_world"),
        SpiConstants.PROPNAME_ACLGROUPS);

    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYUSERS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYGROUPS);
  }

  public void testDmWorldPrincipal() throws Exception {
    insertUsers(DmInitialize.DM_LOGIN_OK1);

    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowUserToAcl(aclObj, DmInitialize.DM_LOGIN_OK1);
    addAllowGroupToAcl(aclObj, "dm_world");

    aclList.processAcl(aclObj, aclValues);
    DctmAuthenticationManager authentManager =
        dctmSession.getAuthenticationManager();
    AuthenticationResponse result =
        authentManager.authenticate(new SimpleAuthenticationIdentity(
            DmInitialize.DM_LOGIN_OK1, null));
    assertTrue(result.isValid());

    Principal expectedPrincipal = new Principal(PrincipalType.UNKNOWN,
        connector.getGoogleLocalNamespace(), "dm_world",
        CaseSensitivityType.EVERYTHING_CASE_SENSITIVE);

    // get dm_world principal from ACL
    Principal aclPrincipal = null;
    List<Value> values = aclValues.get(SpiConstants.PROPNAME_ACLGROUPS);
    for (Value listvalue : values) {
      if (((PrincipalValue) listvalue).getPrincipal().getName()
          .equalsIgnoreCase("dm_world")) {
        aclPrincipal = ((PrincipalValue) listvalue).getPrincipal();
        break;
      }
    }
    assertEquals(expectedPrincipal, aclPrincipal);

    // get dm_world principal from group lookup
    Principal groupLookupPrincipal = null;
    @SuppressWarnings("unchecked") Collection<Principal> groups =
        (Collection<Principal>) result.getGroups();
    for (Principal groupPrincipal : groups) {
      if (groupPrincipal.getName().equalsIgnoreCase("dm_world")) {
        groupLookupPrincipal = groupPrincipal;
        break;
      }
    }
    assertEquals(expectedPrincipal, groupLookupPrincipal);
  }

  public void testDisabledUserAcl() throws Exception {
    insertUsers("user1", "user2");
    disableUsers("user1");

    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowUserToAcl(aclObj, "user1");
    addAllowUserToAcl(aclObj, "user2");

    aclList.processAcl(aclObj, aclValues);

    assertAclEquals(ImmutableSet.of("user1", "user2"),
        SpiConstants.PROPNAME_ACLUSERS);
   assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLGROUPS);

    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYUSERS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYGROUPS);
  }

  public void testDisabledUserDenyAcl() throws Exception {
    insertUsers("user1", "user2");
    disableUsers("user1");

    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addDenyUserToAcl(aclObj, "user1", IAcl.DF_PERMIT_READ);
    addDenyUserToAcl(aclObj, "user2", IAcl.DF_PERMIT_READ);

    aclList.processAcl(aclObj, aclValues);

    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLUSERS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLGROUPS);

    assertAclEquals(ImmutableSet.of("user1", "user2"),
        SpiConstants.PROPNAME_ACLDENYUSERS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYGROUPS);
  }

  public void testDisabledGroupAcl() throws Exception {
    insertGroup("group1", "user1", "user2");
    insertGroup("group2", "user2", "user3");
    disableUsers("group2");

    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowGroupToAcl(aclObj, "group1");
    addAllowGroupToAcl(aclObj, "group2");

    aclList.processAcl(aclObj, aclValues);

    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLUSERS);
    assertAclEquals(ImmutableSet.of("group1", "group2"),
        SpiConstants.PROPNAME_ACLGROUPS);

    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYUSERS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYGROUPS);
  }

  public void testDisabledGroupDenyAcl() throws Exception {
    insertGroup("group1", "user1", "user2");
    insertGroup("group2", "user2", "user3");
    disableUsers("group2");

    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addDenyGroupToAcl(aclObj, "group1", IAcl.DF_PERMIT_READ);
    addDenyGroupToAcl(aclObj, "group2", IAcl.DF_PERMIT_READ);

    aclList.processAcl(aclObj, aclValues);

    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLUSERS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLGROUPS);

    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYUSERS);
    assertAclEquals(ImmutableSet.of("group1", "group2"),
        SpiConstants.PROPNAME_ACLDENYGROUPS);
  }

  public void testNonExistentAccessorsAcl() throws Exception {
    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowUserToAcl(aclObj, "nope-user");
    addAllowGroupToAcl(aclObj, "nope-group");
    addDenyUserToAcl(aclObj, "not-here-user", IAcl.DF_PERMIT_READ);
    addDenyGroupToAcl(aclObj, "not-here-group", IAcl.DF_PERMIT_READ);

    aclList.processAcl(aclObj, aclValues);

    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLUSERS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLGROUPS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYUSERS);
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLDENYGROUPS);
  }

  public void testRequiredGroups() throws Exception {
    insertGroup("Jedi Order");
    insertGroup("Rebellion");
    insertGroup("Return of the Jedi");
    insertGroup("The Force Awakens");

    MockDmAcl aclObj = new MockDmAcl("45opensesame", "opensesame");
    addRequiredGroupToAcl(aclObj, "Jedi Order");
    addRequiredGroupToAcl(aclObj, "Rebellion");
    addRequiredGroupSetToAcl(aclObj, "Return of the Jedi", "The Force Awakens");
    insertAcls(aclObj);

    aclList = getAclListForTest("select r_object_id from dm_acl");
    Document aclDocument;

    assertNotNull(aclDocument = aclList.nextDocument());
    assertPrincipalEquals(ImmutableSet.of(),
        aclDocument, SpiConstants.PROPNAME_ACLGROUPS);
    assertInheritanceTypeEquals(AclInheritanceType.PARENT_OVERRIDES,
        aclDocument);

    assertNotNull(aclDocument = aclList.nextDocument());
    assertPrincipalEquals(
        ImmutableSet.of("Return of the Jedi", "The Force Awakens"),
        aclDocument, SpiConstants.PROPNAME_ACLGROUPS);
    assertInheritanceTypeEquals(AclInheritanceType.AND_BOTH_PERMIT,
        aclDocument);

    assertNotNull(aclDocument = aclList.nextDocument());
    assertPrincipalEquals(ImmutableSet.of("Rebellion"),
        aclDocument, SpiConstants.PROPNAME_ACLGROUPS);
    assertInheritanceTypeEquals(AclInheritanceType.AND_BOTH_PERMIT,
        aclDocument);

    assertNotNull(aclDocument = aclList.nextDocument());
    assertPrincipalEquals(ImmutableSet.of("Jedi Order"),
        aclDocument, SpiConstants.PROPNAME_ACLGROUPS);
    assertInheritanceTypeEquals(AclInheritanceType.AND_BOTH_PERMIT,
        aclDocument);

    assertNull(aclList.nextDocument());
  }

  /** Tests an ACL with READ permission but not ACCESS_PERMIT. */
  public void testEvilAcl() throws Exception {
    insertGroup("Jedi");
    insertGroup("Sith");

    MockDmAcl aclObj = new MockDmAcl("45opensesame", "opensesame");
    addAllowGroupToAcl(aclObj, "Jedi");
    aclObj.addAccessor("Sith", IAcl.DF_PERMIT_READ,
        IAcl.DF_PERMIT_TYPE_APPLICATION_PERMIT, true);
    insertAcls(aclObj);

    aclList = getAclListForTest("select r_object_id from dm_acl");
    Document aclDocument;

    assertNotNull(aclDocument = aclList.nextDocument());
    assertPrincipalEquals(ImmutableSet.of("Jedi"),
        aclDocument, SpiConstants.PROPNAME_ACLGROUPS);
    assertInheritanceTypeEquals(AclInheritanceType.PARENT_OVERRIDES,
        aclDocument);

    assertNull(aclList.nextDocument());
  }

  private void assertPrincipalEquals(Set<?> expectedNames,
      Document document, String propertyName) throws RepositoryException {
    Set<String> actualNames = new HashSet<String>();
    Property property = document.findProperty(propertyName);
    if (property != null) {
      PrincipalValue value;
      while ((value = (PrincipalValue) property.nextValue()) != null) {
        actualNames.add(value.getPrincipal().getName());
      }
    }
    assertEquals(expectedNames, actualNames);
  }

  private void assertInheritanceTypeEquals(AclInheritanceType expected,
      Document document) throws RepositoryException {
    assertEquals(expected.toString(),
        Value.getSingleValueString(document,
            SpiConstants.PROPNAME_ACLINHERITANCETYPE));
  }

  private void testLdapSetup(String commonName, String domain)
      throws Exception {
    insertLdapUsers(commonName, domain, "user1");
    insertGroup("group1", "user2", "user3");

    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowUserToAcl(aclObj, "user1");
    addAllowGroupToAcl(aclObj, "group1");

    aclList.processAcl(aclObj, aclValues);
  }

  public void testLdapForAclUser() throws Exception {
    testLdapSetup("CN=My name is", "dc=ajax");
    assertAclEquals(ImmutableSet.of("ajax\\user1"),
        SpiConstants.PROPNAME_ACLUSERS);
  }

  public void testDnsLdapForAclUser() throws Exception {
    testLdapSetup("CN=My name is", "dc=ajax,dc=example,dc=com");
    assertAclEquals(ImmutableSet.of("ajax\\user1"),
        SpiConstants.PROPNAME_ACLUSERS);
  }

  public void testInvalidDnLdapForAclUser() throws Exception {
    testLdapSetup("=", ",=,");
    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLUSERS);
  }

  public void testNullDnLdapForAclUser() throws Exception {
    insertNullDnLdapUsers("user1");

    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowUserToAcl(aclObj, "user1");

    aclList.processAcl(aclObj, aclValues);

    assertAclEquals(ImmutableSet.of(), SpiConstants.PROPNAME_ACLUSERS);
  }

  public void testNoCnLdapForAclUser() throws Exception {
    testLdapSetup("uid=n", "dc=ajax,dc=example,dc=com");
    assertAclEquals(ImmutableSet.of("ajax\\user1"),
        SpiConstants.PROPNAME_ACLUSERS);
  }

  public void testNoDcLdapForAclUser() throws Exception {
    connector.setWindows_domain("acme");
    dctmSession = connector.login();
    qtm = dctmSession.getTraversalManager();
    qtm.setBatchHint(2);
    aclList = getAclListForTest();

    testLdapSetup("uid=n", "o=ajax,o=example,o=local");
    assertAclEquals(ImmutableSet.of("acme\\user1"),
        SpiConstants.PROPNAME_ACLUSERS);
  }

  public void testLdapForAclGroup() throws Exception {
    testLdapSetup("CN=My name is", "dc=ajax");
    assertAclEquals(ImmutableSet.of("group1"), SpiConstants.PROPNAME_ACLGROUPS);
  }

  private void testDomainSetup(String domain) throws Exception {
    connector.setWindows_domain(domain);
    dctmSession = connector.login();
    qtm = dctmSession.getTraversalManager();
    qtm.setBatchHint(2);
    aclList = getAclListForTest();

    insertUsers("user1");
    insertGroup("group1", "user2", "user3");

    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowUserToAcl(aclObj, "user1");
    addAllowGroupToAcl(aclObj, "group1");

    aclList.processAcl(aclObj, aclValues);
  }

  public void testDomainForAclUser() throws Exception {
    testDomainSetup("ajax");
    assertAclEquals(ImmutableSet.of("ajax\\user1"),
        SpiConstants.PROPNAME_ACLUSERS);
  }

  public void testDnsDomainForAclUser() throws Exception {
    testDomainSetup("ajax.example.com");
    assertAclEquals(ImmutableSet.of("ajax.example.com\\user1"),
        SpiConstants.PROPNAME_ACLUSERS);
  }

  public void testDomainForAclGroup() throws Exception {
    testDomainSetup("ajax");
    assertAclEquals(ImmutableSet.of("group1"), SpiConstants.PROPNAME_ACLGROUPS);
  }
}
