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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

public class MockDmSessionTest extends TestCase {
  IClientX dctmClientX;

  IClient localClient;

  ISessionManager sessionManager;

  ISession sess7;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dctmClientX = new MockDmClientX();
    localClient = dctmClientX.getLocalClient();
    sessionManager = localClient.newSessionManager();
    ILoginInfo ili = new MockDmLoginInfo();
    ili.setUser(DmInitialize.DM_LOGIN_OK4);
    ili.setPassword(DmInitialize.DM_PWD_OK4);
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, ili);
    sess7 = sessionManager.getSession(DmInitialize.DM_DOCBASE);
  }

  public void testGetStore() {
    MockRepositoryDocumentStore a = null;

    a = ((MockDmSession) sess7).getStore();
    assertNotNull(a);
    MockRepositoryDocument mockDoc = a.getDocByID(DmInitialize.DM_ID1);
    String docID = mockDoc.getDocID();
    assertEquals(docID, DmInitialize.DM_ID1);
  }

  public void testGetLoginTicketForUser() {
    String userID = "";
    try {
      userID = sess7.getLoginTicketForUser(DmInitialize.DM_LOGIN_OK1);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
    assertEquals(userID, DmInitialize.DM_LOGIN_OK1);
  }

  public void testGetDocbaseName() {
    String docbase = ((MockDmSession) sess7).getDocbaseName();
    assertEquals(docbase, DmInitialize.DM_DOCBASE);
  }

  public void testGetObject() {
    IId id = dctmClientX.getId(DmInitialize.DM_ID2);
    ISysObject dctmMockRepositoryDocument = null;
    boolean idString = true;
    try {
      dctmMockRepositoryDocument = (ISysObject) sess7.getObject(id);
      idString = ((MockDmObject) dctmMockRepositoryDocument)
          .getBoolean("google:ispublic");
      assertEquals(idString, DmInitialize.DM_ID2_IS_PUBLIC);
    } catch (RepositoryException e) {
      fail();
    }
  }
}
