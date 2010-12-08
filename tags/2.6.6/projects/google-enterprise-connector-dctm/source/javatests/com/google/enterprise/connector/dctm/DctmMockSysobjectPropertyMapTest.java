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

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.SpiConstants.ActionType;
import com.google.enterprise.connector.spi.Value;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.Set;

public class DctmMockSysobjectPropertyMapTest extends TestCase {
  IClientX dctmClientX = null;
  IClient localClient = null;
  ISessionManager sessionManager = null;
  ISession session;
  DctmTraversalManager traversalManager = null;
  DctmSysobjectDocument document = null;
  ITime lastModifDate;

  private static final String PROPNAME_FEEDTYPE = "google:feedtype";
  public void setUp() throws Exception {
    super.setUp();

    dctmClientX = new MockDmClient();
    localClient = dctmClientX.getLocalClient();
    sessionManager = localClient.newSessionManager();
    ILoginInfo loginInfo = dctmClientX.getLoginInfo();
    loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
    loginInfo.setPassword(DmInitialize.DM_PWD_OK1);
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);

    traversalManager = new DctmTraversalManager(dctmClientX,
        DmInitialize.DM_DOCBASE, DmInitialize.DM_WEBTOP_SERVER_URL,
        DmInitialize.included_meta, sessionManager);

    session = null;
    try {
      session = sessionManager.newSession(DmInitialize.DM_DOCBASE);
    } finally {
      if (session != null) {
        sessionManager.release(session);
      }
    }

    session = sessionManager.getSession(DmInitialize.DM_DOCBASE);
    IId id = dctmClientX.getId(DmInitialize.DM_ID1);
    ISysObject object = session.getObject(id);
    lastModifDate = object.getTime("r_modify_date");
    document = new DctmSysobjectDocument(traversalManager, session,
        DmInitialize.DM_ID1, null, lastModifDate, ActionType.ADD, null);
  }

  private DctmSysobjectDocument getDocument(Set<String> includedMeta)
      throws RepositoryException {
    DctmTraversalManager traversalManager = new DctmTraversalManager(
        dctmClientX, DmInitialize.DM_DOCBASE,
        DmInitialize.DM_WEBTOP_SERVER_URL, includedMeta, sessionManager);
    return new DctmSysobjectDocument(traversalManager, session,
        DmInitialize.DM_ID1, null, lastModifDate, ActionType.ADD, null);
  }

  public void testGetPropertyNames_empty() throws RepositoryException {
    Set<String> names = document.getPropertyNames();
    assertEquals(5, names.size());
    for (String name : names) {
      assertTrue(name, name.startsWith("google:"));
    }
  }

  public void testGetPropertyNames_folder() throws RepositoryException {
    document = getDocument(
        Collections.singleton(SpiConstants.PROPNAME_FOLDER));
    Set<String> names = document.getPropertyNames();
    assertEquals(6, names.size());
    for (String name : names) {
      assertTrue(name, name.startsWith("google:"));
    }
  }

  public void testGetPropertyNames_objectId() throws RepositoryException {
    document = getDocument(
        Collections.singleton(DctmSysobjectDocument.OBJECT_ID_NAME));
    Set<String> names = document.getPropertyNames();
    assertEquals(6, names.size());
    for (String name : names) {
      assertTrue(name,
          name.startsWith("google:") || name.equals("r_object_id"));
    }
  }

  public void testFindProperty_docid() throws RepositoryException {
    Property property = document.findProperty(SpiConstants.PROPNAME_DOCID);
    assertNotNull(property);
    Value value = property.nextValue();
    assertNotNull(value);
    assertEquals(DmInitialize.DM_ID1, value.toString());
  }

  public void testFindProperty_folder() throws RepositoryException {
    // This does not work because the mock implementation does not
    // support retrieving r_folder path from the parent folders, but
    // it does check that findProperty does not return null.
    Property property;
    try {
      property = document.findProperty(SpiConstants.PROPNAME_FOLDER);
    } catch (Exception e) {
      return;
    }
    assertNotNull(property);
    Value value = property.nextValue();
    assertNotNull(value);
  }

  public void testFindProperty_objectId() throws RepositoryException {
    Property property =
        document.findProperty(DctmSysobjectDocument.OBJECT_ID_NAME);
    assertNotNull(property);
    Value value = property.nextValue();
    assertNotNull(value);
    assertEquals(DmInitialize.DM_ID1, value.toString());
  }

  /**
   * Tests that the unsupported SPI properties are ignored, and only
   * handled once per property name.
   */
  public void testIgnoredSpiProperties() throws RepositoryException {
    Property property;

    assertTrue(document.UNSUPPORTED_PROPNAMES.toString(),
        document.UNSUPPORTED_PROPNAMES.isEmpty());

    property = document.findProperty(SpiConstants.PROPNAME_DOCID);
    assertNotNull(SpiConstants.PROPNAME_DOCID, property);

    assertTrue(document.UNSUPPORTED_PROPNAMES.toString(),
        document.UNSUPPORTED_PROPNAMES.isEmpty());

    // Retrieve these twice to ensure that the set doesn't grow.
    for (int i = 0; i < 2; i++) {
      property = document.findProperty(SpiConstants.PROPNAME_SEARCHURL);
      assertNull(SpiConstants.PROPNAME_SEARCHURL, property);
      property = document.findProperty(SpiConstants.PROPNAME_FEEDTYPE);
      assertNull(SpiConstants.PROPNAME_FEEDTYPE, property);

      assertEquals(document.UNSUPPORTED_PROPNAMES.toString(),
          2, document.UNSUPPORTED_PROPNAMES.size());
      assertTrue(document.UNSUPPORTED_PROPNAMES.toString(),
          document.UNSUPPORTED_PROPNAMES.contains(
              SpiConstants.PROPNAME_SEARCHURL));
      assertTrue(document.UNSUPPORTED_PROPNAMES.toString(),
          document.UNSUPPORTED_PROPNAMES.contains(
              SpiConstants.PROPNAME_FEEDTYPE));
    }
  }
}
