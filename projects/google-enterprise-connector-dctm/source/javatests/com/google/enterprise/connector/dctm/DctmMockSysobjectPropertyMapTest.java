// Copyright 2007 Google Inc.
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

import java.util.Set;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClientX;
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

public class DctmMockSysobjectPropertyMapTest extends TestCase {
  IClientX dctmClientX = null;
  IClient localClient = null;
  ISessionManager sessionManager = null;
  DctmTraversalManager traversalManager = null;
  DctmSysobjectDocument document = null;

  private static final String PROPNAME_FEEDTYPE = "google:feedtype";
  public void setUp() throws Exception {
    super.setUp();

    dctmClientX = new MockDmClientX();
    localClient = dctmClientX.getLocalClient();
    sessionManager = localClient.newSessionManager();
    ILoginInfo loginInfo = dctmClientX.getLoginInfo();
    loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
    loginInfo.setPassword(DmInitialize.DM_PWD_OK1);
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);

    traversalManager = new DctmTraversalManager(dctmClientX,
        DmInitialize.DM_DOCBASE, DmInitialize.DM_WEBTOP_SERVER_URL,
        DmInitialize.included_meta, sessionManager);

    ISession session = null;
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
    ITime lastModifDate = object.getTime("r_modify_date");
    document = new DctmSysobjectDocument(traversalManager, session,
        DmInitialize.DM_ID1, null, lastModifDate, ActionType.ADD, null);
  }

  public void testGetPropertyNames() throws RepositoryException {
    Set<String> names = document.getPropertyNames();
    assertEquals(6, names.size());
    for (String name : names) {
      assertTrue(name, name.startsWith("google:"));
    }
  }

  public void testFindProperty() throws RepositoryException {
    Property property = document.findProperty(SpiConstants.PROPNAME_DOCID);
    assertNotNull(property);
    Value value = property.nextValue();
    assertNotNull(value);
    assertEquals(DmInitialize.DM_ID1, value.toString());
  }

  /**
   * Tests that all of the SPI property name constants match our
   * PROPNAME_REGEXP.
   */
  /* TODO: Remove this test for 3.0. */
  public void testPropnameRegexp() throws IllegalAccessException {
    Class c = SpiConstants.class;
    java.lang.reflect.Field[] fields = c.getFields();
    for (java.lang.reflect.Field field : fields) {
      String name = field.getName();
      if (name.startsWith("PROPNAME")) {
        String value = (String) field.get(null);
        assertTrue(name + " = " + value,
            value.matches(DctmSysobjectDocument.PROPNAME_REGEXP));
      }
    }
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
