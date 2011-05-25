// Copyright 2006 Google Inc.
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
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MockDmSessionManagerTest extends TestCase {
  private IClientX dctmClientX;
  private IClient localClient;
  private ISessionManager sessionManager;

  protected void setUp() throws RepositoryException {
    dctmClientX = new MockDmClientX();
    localClient = dctmClientX.getLocalClient();
    sessionManager = localClient.newSessionManager();
  }

  public void testSetAndClearIdentity() {
    try {
      ILoginInfo ili = dctmClientX.getLoginInfo();
      ili.setUser("mark");
      ili.setPassword("mark");
      sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);

      // No way to check identity as correctly been set but to try to
      // authenticate on the docbase
      assertTrue(sessionManager
          .authenticate("MockRepositoryEventLog7.txt"));

      sessionManager.clearIdentity("MockRepositoryEventLog7.txt");
      assertFalse(sessionManager
          .authenticate("MockRepositoryEventLog7.txt"));

    } catch (RepositoryException e) {
      assertEquals(true, false);
    }
  }

  public void testNewSession() {
    try {
      try {
        sessionManager.newSession("MockRepositoryEventLog7.txt");
        assertTrue(false);
      } catch (RepositoryLoginException e) {
        assertEquals(
            e.getMessage(),
            "newSession(MockRepositoryEventLog7.txt) called for docbase "
                + "MockRepositoryEventLog7.txt without setting any "
                + "credentials prior to this call");
      }
      ILoginInfo ili = dctmClientX.getLoginInfo();
      ili.setUser("mark");
      ili.setPassword("mark");
      sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);
      ISession sess = sessionManager
          .newSession("MockRepositoryEventLog7.txt");
      assertTrue(sess instanceof MockDmSession);
      assertNotNull(sess);
    } catch (RepositoryException e) {
      assertEquals(true, false);
    }
  }

  public void testGetSession() {
    try {
      ILoginInfo ili = dctmClientX.getLoginInfo();
      ili.setUser("mark");
      ili.setPassword("mark");
      sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);
      ISession sess = sessionManager
          .getSession("MockRepositoryEventLog7.txt");
      assertNotNull(sess);
      assertTrue(sess instanceof MockDmSession);
    } catch (RepositoryException e) {
      assertTrue(false);
    }
  }

  public void testAuthenticate() {
    try {
      ILoginInfo ili = dctmClientX.getLoginInfo();
      ili.setUser("mark");
      ili.setPassword("mark");
      sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);
      boolean rep = sessionManager
          .authenticate("MockRepositoryEventLog7.txt");
      assertTrue(rep);

      sessionManager.clearIdentity("MockRepositoryEventLog7.txt");
      ILoginInfo ili2 = dctmClientX.getLoginInfo();
      ili2.setUser("mark");
      ili2.setPassword("hghfhgfhgf");
      sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili2);
      boolean rep2 = sessionManager
          .authenticate("MockRepositoryEventLog7.txt");
      assertFalse(rep2);
    } catch (RepositoryException e) {
      fail();
    }
  }

  public void testGetIdentity() {
    try {
      ILoginInfo ili = dctmClientX.getLoginInfo();
      ili.setUser("mark");
      ili.setPassword("mark");
      sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);
      ILoginInfo ili2 = sessionManager
          .getIdentity("MockRepositoryEventLog7.txt");
      String user = ili2.getUser();
      String pwd = ili2.getPassword();
      Assert.assertEquals(user, "mark");
      Assert.assertEquals(pwd, "mark");
    } catch (RepositoryException e) {
      fail();
    }
  }
}
