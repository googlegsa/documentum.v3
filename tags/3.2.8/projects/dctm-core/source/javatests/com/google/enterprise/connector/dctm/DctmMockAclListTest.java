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
import com.google.enterprise.connector.dctm.dfcwrap.IAcl;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.spi.AuthenticationResponse;
import com.google.enterprise.connector.spi.Principal;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.SimpleAuthenticationIdentity;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.TraversalManager;
import com.google.enterprise.connector.spi.Value;
import com.google.enterprise.connector.spi.SpiConstants.CaseSensitivityType;
import com.google.enterprise.connector.spi.SpiConstants.PrincipalType;
import com.google.enterprise.connector.spiimpl.PrincipalValue;

import junit.framework.TestCase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DctmMockAclListTest extends TestCase {
  private DctmConnector connector = null;
  private TraversalManager qtm = null;

  private DctmSession dctmSession = null;

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
    dctmSession = (DctmSession) connector.login();

    qtm = (DctmTraversalManager) dctmSession.getTraversalManager();
    qtm.setBatchHint(2);

    aclValues =
        new HashMap<String, List<Value>>();
    aclList = getAclListForTest();

    jdbcFixture.setUp();
  }

  protected void tearDown() throws SQLException {
    jdbcFixture.tearDown();
  }

  private DctmAclList getAclListForTest()
      throws RepositoryLoginException, RepositoryException {
    List<String> whereClauselist = new ArrayList<String>();
    Checkpoint checkpoint = new Checkpoint(whereClauselist);
    ISession session =
        ((DctmTraversalManager) qtm).getSessionManager().getSession(
            DmInitialize.DM_DOCBASE);
    DctmAclList aclList =
        new DctmAclList((DctmTraversalManager) qtm, session, null, null,
            checkpoint);
    return aclList;
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

  private void insertGroup(String groupName, String... members)
      throws SQLException {
    jdbcFixture.executeUpdate(String.format(
        "insert into dm_user(user_name) values('%s')", groupName));
    for (String user : members) {
      jdbcFixture.executeUpdate(String.format(
          "insert into dm_group(group_name, i_all_users_names) "
              + "values('%s', '%s')", groupName, user));
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
        (DctmAuthenticationManager) dctmSession.getAuthenticationManager();
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
    Collection<Principal> groups = (Collection<Principal>) result.getGroups();
    for (Principal groupPrincipal : groups) {
      if (groupPrincipal.getName().equalsIgnoreCase("dm_world")) {
        groupLookupPrincipal = groupPrincipal;
        break;
      }
    }
    assertEquals(expectedPrincipal, groupLookupPrincipal);
  }
}
