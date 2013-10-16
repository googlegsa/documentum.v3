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

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmAcl;
import com.google.enterprise.connector.dctm.dfcwrap.IAcl;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.TraversalManager;
import com.google.enterprise.connector.spi.Value;
import com.google.enterprise.connector.spiimpl.PrincipalValue;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DctmMockAclListTest extends TestCase {
  private TraversalManager qtm = null;

  private DctmSession dctmSession = null;

  private DctmAclList aclList = null;
  private Map<String, List<Value>> aclValues;

  public void setUp() throws Exception {
    super.setUp();

    DctmConnector connector = new DctmConnector();
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

  private boolean containsPrincipal(String value, String propertyName) {
    boolean found = false;
    List<Value> values = aclValues.get(propertyName);
    for (Value listvalue : values) {
      if (value.equals(((PrincipalValue) listvalue).
          getPrincipal().getName())) {
        found = true;
        break;
      }
    }
    return found;
  }

  private void assertContains(String value, String propertyName) {
    assertTrue(containsPrincipal(value, propertyName));
  }

  private void assertNotContains(String value, String propertyName) {
    assertFalse(containsPrincipal(value, propertyName));
  }

  private void assertSize(int size, String propertyName) {
    assertEquals(size, aclValues.get(propertyName).size());
  }

  private void assertEmpty(String propertyName) {
    assertEquals(0, aclValues.get(propertyName).size());
  }

  public void testAllowAcl() throws RepositoryDocumentException,
      RepositoryException {
    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowUserToAcl(aclObj, "user1");
    addAllowGroupToAcl(aclObj, "group1");

    aclList.processAcl(aclObj, aclValues);
    // verify allow users
    assertSize(1, SpiConstants.PROPNAME_ACLUSERS);
    assertContains("user1", SpiConstants.PROPNAME_ACLUSERS);
    // verify allow groups
    assertSize(1, SpiConstants.PROPNAME_ACLGROUPS);
    assertContains("group1", SpiConstants.PROPNAME_ACLGROUPS);
    // verify no deny users and no deny groups
    assertEmpty(SpiConstants.PROPNAME_ACLDENYUSERS);
    assertEmpty(SpiConstants.PROPNAME_ACLDENYGROUPS);
  }

  public void testDenyAcl() throws RepositoryDocumentException,
      RepositoryException {
    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowUserToAcl(aclObj, "user1");
    addDenyUserToAcl(aclObj, "user2", IAcl.DF_PERMIT_READ);
    addDenyUserToAcl(aclObj, "user3", IAcl.DF_PERMIT_WRITE);
    addDenyUserToAcl(aclObj, "user4", IAcl.DF_PERMIT_BROWSE);

    aclList.processAcl(aclObj, aclValues);

    // verify no entries in groups
    assertEmpty(SpiConstants.PROPNAME_ACLGROUPS);
    assertEmpty(SpiConstants.PROPNAME_ACLDENYGROUPS);

    assertSize(1, SpiConstants.PROPNAME_ACLUSERS);
    assertContains("user1", SpiConstants.PROPNAME_ACLUSERS);
    // verify user3 is not in allow users
    assertNotContains("user3", SpiConstants.PROPNAME_ACLUSERS);

    assertSize(2, SpiConstants.PROPNAME_ACLDENYUSERS);
    assertContains("user2", SpiConstants.PROPNAME_ACLDENYUSERS);
    // verify user3 is not restricted, i.e, not in deny users
    assertNotContains("user3", SpiConstants.PROPNAME_ACLDENYUSERS);
    assertContains("user4", SpiConstants.PROPNAME_ACLDENYUSERS);
  }

  public void testGroupAcl() throws RepositoryDocumentException,
      RepositoryException {
    MockDmAcl aclObj = new MockDmAcl(123, "testAcl123");
    addAllowGroupToAcl(aclObj, "engineering");
    addDenyGroupToAcl(aclObj, "sales", IAcl.DF_PERMIT_READ);
    addDenyGroupToAcl(aclObj, "marketing", IAcl.DF_PERMIT_VERSION);

    aclList.processAcl(aclObj, aclValues);

    assertEmpty(SpiConstants.PROPNAME_ACLUSERS);
    assertEmpty(SpiConstants.PROPNAME_ACLDENYUSERS);

    assertSize(1, SpiConstants.PROPNAME_ACLGROUPS);
    assertContains("engineering", SpiConstants.PROPNAME_ACLGROUPS);

    assertSize(1, SpiConstants.PROPNAME_ACLDENYGROUPS);
    assertNotContains("marketing", SpiConstants.PROPNAME_ACLDENYGROUPS);
    assertContains("sales", SpiConstants.PROPNAME_ACLDENYGROUPS);
  }
}
