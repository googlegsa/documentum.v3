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

import java.util.Set;

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
import com.google.enterprise.connector.spi.SpiConstants.ActionType;
import com.google.enterprise.connector.spi.Value;

import junit.framework.TestCase;

public class DctmMockSysobjectPropertyMapTest extends TestCase {
  IClientX dctmClientX = null;

  IClient localClient = null;

  ISessionManager sessionManager = null;

  DctmTraversalManager traversalManager = null;

  public void setUp() throws Exception {
    super.setUp();

    dctmClientX = new MockDmClient();

    localClient = dctmClientX.getLocalClient();

    sessionManager = localClient.newSessionManager();

    ISession session = null;

    ILoginInfo loginInfo = dctmClientX.getLoginInfo();
    loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
    loginInfo.setPassword(DmInitialize.DM_PWD_OK1);
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);
    sessionManager.setDocbaseName(DmInitialize.DM_DOCBASE);
    traversalManager = new DctmTraversalManager(dctmClientX,
        DmInitialize.DM_WEBTOP_SERVER_URL, DmInitialize.included_meta,
        sessionManager);

    try {
      session = sessionManager.newSession(DmInitialize.DM_DOCBASE);
    } finally {
      if (session != null) {
        sessionManager.release(session);
      }
    }
  }

  public void testGetPropertyNames() throws RepositoryException {
    ISession session = sessionManager.getSession(DmInitialize.DM_DOCBASE);
    IId id = dctmClientX.getId(DmInitialize.DM_ID1);

    ISysObject object = session.getObject(id);

    ITime lastModifDate = object.getTime("r_modify_date");

    object = session.getObject(id);

    DctmSysobjectDocument dctmSpm = new DctmSysobjectDocument(traversalManager,
        DmInitialize.DM_ID1, null, lastModifDate, ActionType.ADD, null);

    Set<String> names = dctmSpm.getPropertyNames();
    assertEquals(5, names.size());
    for (String name : names) {
      assertTrue(name, name.startsWith("google:"));
    }
  }

  public void testFindProperty() throws RepositoryException {
    ISession session = sessionManager.getSession(DmInitialize.DM_DOCBASE);
    IId id = dctmClientX.getId(DmInitialize.DM_ID1);

    ISysObject object = session.getObject(id);

    ITime lastModifDate = object.getTime("r_modify_date");

    object = session.getObject(id);

    DctmSysobjectDocument dctmSpm = new DctmSysobjectDocument(
        traversalManager, DmInitialize.DM_ID1, null, lastModifDate,
        ActionType.ADD, null);

    Property property = dctmSpm.findProperty("google:docid");
    assertNotNull(property);
    Value value = property.nextValue();
    assertNotNull(value);
    assertEquals(DmInitialize.DM_ID1, value.toString());
  }
}
