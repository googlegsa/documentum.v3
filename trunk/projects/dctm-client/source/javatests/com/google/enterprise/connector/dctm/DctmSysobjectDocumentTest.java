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

import java.util.Iterator;

import junit.framework.TestCase;

import com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX;
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
import com.google.enterprise.connector.spi.Value;

public class DctmSysobjectDocumentTest extends TestCase {
  IClientX dctmClientX = null;
  IClient localClient = null;
  ISessionManager sessionManager = null;
  DctmTraversalManager traversalManager = null;

  public void setUp() throws Exception {
    super.setUp();
    dctmClientX = new DmClientX();
    localClient = dctmClientX.getLocalClient();
    sessionManager = localClient.newSessionManager();

    ILoginInfo loginInfo = dctmClientX.getLoginInfo();
    loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
    loginInfo.setPassword(DmInitialize.DM_PWD_OK1);
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);

    traversalManager = new DctmTraversalManager(dctmClientX,
        DmInitialize.DM_DOCBASE, "", DmInitialize.included_meta,
        sessionManager);
  }

  public void testGetPropertyNames() throws RepositoryException {
    ISession session = sessionManager.getSession(DmInitialize.DM_DOCBASE);
    IId id = dctmClientX.getId(DmInitialize.DM_ID1);

    ISysObject object = session.getObject(id);

    ITime lastModifDate = object.getTime("r_modify_date");

    object = session.getObject(id);

    DctmSysobjectDocument dctmSpm = new DctmSysobjectDocument(
        traversalManager, session, DmInitialize.DM_ID1, null, lastModifDate,
        SpiConstants.ActionType.ADD, null);

    assertEquals(8, dctmSpm.getPropertyNames().size());
  }

  public void testFindProperty() throws RepositoryException {
    ISession session = sessionManager.getSession(DmInitialize.DM_DOCBASE);
    IId id = dctmClientX.getId(DmInitialize.DM_ID2);
    ISysObject object = session.getObject(id);
    ITime lastModifDate = object.getTime("r_modify_date");

    DctmSysobjectDocument dctmSpm = new DctmSysobjectDocument(
        traversalManager, session, DmInitialize.DM_ID2, null, lastModifDate,
        SpiConstants.ActionType.ADD, null);

    Property property = dctmSpm.findProperty("keywords");
    Value val = null;

    while ((val = property.nextValue()) != null) {
      // TOOD: compare val.toString() to expected keywords.
    }

    property = dctmSpm.findProperty("r_object_id");
    assertEquals(DmInitialize.DM_ID2, property.nextValue().toString());

    property = dctmSpm.findProperty(SpiConstants.PROPNAME_DOCID);
    assertEquals(DmInitialize.DM_VSID2, property.nextValue().toString());
  }
}
