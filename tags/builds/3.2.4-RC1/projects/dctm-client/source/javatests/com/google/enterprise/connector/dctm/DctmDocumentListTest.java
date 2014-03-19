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

import com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

import java.util.Collections;

public class DctmDocumentListTest extends TestCase {
  IClientX dctmClientX = null;

  IClient localClient = null;

  ISessionManager sessionManager = null;

  ISession session = null;

  IQuery query;

  DctmTraversalManager traversalManager = null;

  DctmDocumentList documentList;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dctmClientX = new DmClientX();
    localClient = dctmClientX.getLocalClient();
    sessionManager = localClient.newSessionManager();

    ILoginInfo loginInfo = dctmClientX.getLoginInfo();
    loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
    loginInfo.setPassword(DmInitialize.DM_PWD_OK1);
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);
    session = sessionManager.getSession(DmInitialize.DM_DOCBASE);

    traversalManager = new DctmTraversalManager(dctmClientX,
        DmInitialize.DM_DOCBASE, DmInitialize.DM_WEBTOP_SERVER_URL,
        DmInitialize.included_meta, sessionManager);

    query = dctmClientX.getQuery();
    query.setDQL("select i_chronicle_id, r_object_id, r_modify_date from "
        + "dm_sysobject where folder('/test_docs/GoogleDemo',descend)");
    ICollection collec = query.execute(session, IQuery.READ_QUERY);
    documentList = new DctmDocumentList(traversalManager, session, collec,
        collec, new Checkpoint(Collections.<String>emptyList()));
  }

  @Override
  protected void tearDown() throws RepositoryException {
    documentList.checkpoint();
  }

  /*
   * Test method for
   * 'com.google.enterprise.connector.dctm.DctmDocumentList.nextDocument()'
   */
  public void testNextDocument() throws RepositoryException {
    Document doc = null;
    int counter = 0;
    while ((doc = documentList.nextDocument()) != null) {
      assertTrue(doc instanceof DctmSysobjectDocument);
      counter++;
    }
    assertEquals(6, counter);
  }

  /*
   * Test method for
   * 'com.google.enterprise.connector.dctm.DctmDocumentList.checkpoint()'
   */
  public void testCheckpoint() throws RepositoryException {
    while ((documentList.nextDocument()) != null)
      ;
    assertEquals(DmInitialize.DM_CHECKPOINT, documentList.checkpoint());
  }
}
