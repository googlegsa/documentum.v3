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

import junit.framework.TestCase;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.spi.RepositoryException;

public class MockMockListTest extends TestCase {
  public void testMockMockList() {
    IClientX dctmClientX = new MockDmClientX();
    IClient localClient = null;
    try {
      localClient = dctmClientX.getLocalClient();
    } catch (RepositoryException e) {
      assertTrue(false);
    }
    ISessionManager sessionManager = localClient.newSessionManager();
    ILoginInfo ili = new MockDmLoginInfo();
    ili.setUser("mark");
    ili.setPassword("mark");
    try {
      sessionManager.setIdentity("SwordEventLog.txt", ili);
      sessionManager.getSession("SwordEventLog.txt");
    } catch (RepositoryException e) {
      assertTrue(false);
    }
    String query = "kqsfgopqsudhnfpioqsdf^qsdfhqsdo 'doc26', 'doc2', 'doc3', 'doc4'";
    String[] ids = query.split("', '");
    ids[0] = ids[0].substring(ids[0].lastIndexOf("'") + 1, ids[0].length());
    ids[ids.length - 1] = ids[ids.length - 1].substring(0, ids.length);
    MockMockList lst = null;
    try {
      lst = new MockMockList(ids, sessionManager, DmInitialize.DM_DOCBASE);
    } catch (Exception e) {
      fail(e.toString());
    }
    assertTrue(lst.iterator().next() instanceof MockRepositoryDocument);
  }
}
