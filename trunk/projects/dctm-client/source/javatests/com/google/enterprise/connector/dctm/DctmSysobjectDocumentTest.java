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

import java.util.HashSet;
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
import com.google.enterprise.connector.spiimpl.StringValue;

public class DctmSysobjectDocumentTest extends TestCase {
  IClientX dctmClientX = null;

  IClient localClient = null;

  ISessionManager sessionManager = null;

  public void setUp() throws Exception {
    super.setUp();
    dctmClientX = new DmClientX();

    localClient = dctmClientX.getLocalClient();

    sessionManager = localClient.newSessionManager();

    ILoginInfo loginInfo = dctmClientX.getLoginInfo();
    loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
    loginInfo.setPassword(DmInitialize.DM_PWD_OK1);
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);
    sessionManager.setDocbaseName(DmInitialize.DM_DOCBASE);
  }

  public void testGetPropertyNames() throws RepositoryException {
    /*
    (String docid, ITime lastModifDate, ISessionManager sessionManager,
        IClientX clientX, String isPublic, HashSet included_meta,
        SpiConstants.ActionType action)

    (String docid, String commonVersionID, ITime timeStamp, ISessionManager sessionManager,
        IClientX clientX, String isPublic, HashSet included_meta,
        SpiConstants.ActionType action)
    */
        ISession session = sessionManager.getSession(DmInitialize.DM_DOCBASE);
        IId id = dctmClientX.getId(DmInitialize.DM_ID1);

        ISysObject object = session.getObject(id);

        ITime lastModifDate = object.getTime("r_modify_date");

        object = session.getObject(id);

    DctmSysobjectDocument dctmSpm = new DctmSysobjectDocument(
        DmInitialize.DM_ID1, lastModifDate, sessionManager, dctmClientX, "false",
        DmInitialize.hashIncluded_meta, SpiConstants.ActionType.ADD);

        Iterator iterator = dctmSpm.getPropertyNames().iterator();
        int counter = 0;
        while (iterator.hasNext()) {
          iterator.next();
          counter++;
        }
        assertEquals(8, counter);
  }

  public void testFindProperty() throws RepositoryException {
    ISession session = sessionManager.getSession(DmInitialize.DM_DOCBASE);
    IId id = dctmClientX.getId(DmInitialize.DM_ID2);

    ISysObject object = session.getObject(id);

    ITime lastModifDate = object.getTime("r_modify_date");

    object = session.getObject(id);

    DctmSysobjectDocument dctmSpm = new DctmSysobjectDocument(
    DmInitialize.DM_ID2, lastModifDate, sessionManager, dctmClientX, "false",
    DmInitialize.hashIncluded_meta, SpiConstants.ActionType.ADD);

    Property property = dctmSpm.findProperty("keywords");
    assertTrue(property instanceof DctmSysobjectProperty);
    Value val = null;

    while ((val = property.nextValue()) != null) {
      assertTrue(val instanceof StringValue);
    }
    property = dctmSpm.findProperty("r_object_id");
    assertTrue(property instanceof DctmSysobjectProperty);

    assertEquals(DmInitialize.DM_ID2, property.nextValue().toString());
    property = dctmSpm.findProperty(SpiConstants.PROPNAME_DOCID);
    assertTrue(property instanceof DctmSysobjectProperty);
    assertEquals(DmInitialize.DM_VSID2, property.nextValue().toString());
  }
}
