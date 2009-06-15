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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MockDmClientTest extends TestCase {
  public void testGetLoginInfo() {
    IClientX dctmClientX = new MockDmClient();

    ILoginInfo loginInfo = dctmClientX.getLoginInfo();

    Assert.assertTrue(loginInfo instanceof MockDmLoginInfo);

    loginInfo.setUser("max");
    loginInfo.setPassword("foo");

    Assert.assertEquals("max", loginInfo.getUser());
    Assert.assertEquals("foo", loginInfo.getPassword());
  }

  public void testNewSessionManager() {
    IClientX dctmClientX = new MockDmClient();
    try {
      IClient localClient = dctmClientX.getLocalClient();
      ISessionManager sessionManager = localClient.newSessionManager();
      Assert.assertNotNull(sessionManager);
      Assert.assertTrue(sessionManager instanceof MockDmClient);
    } catch (RepositoryException e) {
      assertEquals(true, false);
    }
  }

  public void testSetAndClearIdentity() {
    IClientX dctmClientX = new MockDmClient();
    try {
      IClient localClient = dctmClientX.getLocalClient();
      ISessionManager sessionManager = localClient.newSessionManager();
      ILoginInfo ili = new MockDmLoginInfo();
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
      IClientX dctmClientX = new MockDmClient();
      IClient localClient = dctmClientX.getLocalClient();
      ISessionManager sessionManager = localClient.newSessionManager();
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
      ILoginInfo ili = new MockDmLoginInfo();
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

  public void testGetSessionAndGetAndSetDocbaseName() {
    try {
      IClientX dctmClientX = new MockDmClient();
      IClient localClient = null;
      localClient = dctmClientX.getLocalClient();
      ISessionManager sessionManager = localClient.newSessionManager();
      ILoginInfo ili = new MockDmLoginInfo();
      ili.setUser("mark");
      ili.setPassword("mark");
      sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);
      ISession sess7 = sessionManager
          .getSession("MockRepositoryEventLog7.txt");
      assertNotNull(sess7);
      assertTrue(sess7 instanceof MockDmSession);
      String checkName = sessionManager.getDocbaseName();
      assertEquals(checkName, "MockRepositoryEventLog7.txt");

      sessionManager.setIdentity("MockRepositoryEventLog2.txt", ili);
      ISession sess2 = sessionManager
          .getSession("MockRepositoryEventLog2.txt");
      assertNotNull(sess2);
      assertTrue(sess2 instanceof MockDmSession);
      checkName = sessionManager.getDocbaseName();
      assertEquals(checkName, "MockRepositoryEventLog2.txt");

      // Change the current docbase name
      sessionManager.setDocbaseName("MockRepositoryEventLog7.txt");
      checkName = sessionManager.getDocbaseName();
      assertEquals(checkName, "MockRepositoryEventLog7.txt");
    } catch (RepositoryException e) {
      assertTrue(false);
    }
  }

  public void testAuthenticate() {
    try {
      IClientX dctmClientX = new MockDmClient();
      IClient localClient = dctmClientX.getLocalClient();
      ISessionManager sessionManager = localClient.newSessionManager();

      ILoginInfo ili = new MockDmLoginInfo();
      ili.setUser("mark");
      ili.setPassword("mark");
      sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);
      boolean rep = sessionManager
          .authenticate("MockRepositoryEventLog7.txt");
      assertTrue(rep);

      sessionManager.clearIdentity("MockRepositoryEventLog7.txt");
      ILoginInfo ili2 = new MockDmLoginInfo();
      ili2.setUser("mark");
      ili2.setPassword("hghfhgfhgf");
      sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili2);
      boolean rep2 = sessionManager
          .authenticate("MockRepositoryEventLog7.txt");
      assertFalse(rep2);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testGetQuery() {
    IClientX dctmClientX = new MockDmClient();
    IClient localClient = null;
    IQuery query = null;
    try {
      localClient = dctmClientX.getLocalClient();
      query = localClient.getQuery();
      assertNotNull(query);
      Assert.assertTrue(query instanceof MockDmQuery);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testGetIdentity() {
    IClientX dctmClientX = new MockDmClient();
    IClient localClient = null;
    String user = "";
    String pwd = "";
    try {
      localClient = dctmClientX.getLocalClient();
      ISessionManager sessionManager = localClient.newSessionManager();
      ILoginInfo ili = new MockDmLoginInfo();
      ili.setUser("mark");
      ili.setPassword("mark");
      sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);
      ILoginInfo ili2 = sessionManager
          .getIdentity("MockRepositoryEventLog7.txt");
      user = ili2.getUser();
      pwd = ili2.getPassword();
      Assert.assertEquals(user, "mark");
      Assert.assertEquals(pwd, "mark");
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testGetSession() {
    IClientX dctmClientX = new MockDmClient();
    IClient localClient = null;
    ISession session = null;
    try {
      localClient = dctmClientX.getLocalClient();
      ISessionManager sessionManager = localClient.newSessionManager();
      ILoginInfo ili = new MockDmLoginInfo();
      ili.setUser("mark");
      ili.setPassword("mark");
      sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);
      session = sessionManager.getSession("MockRepositoryEventLog7.txt");
      Assert.assertNotNull(session);
      Assert.assertTrue(session instanceof MockDmSession);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testGetDocBaseName() {
    IClientX dctmClientX = new MockDmClient();
    IClient localClient = null;

    String docbase = "";
    try {
      localClient = dctmClientX.getLocalClient();
      ISessionManager sessionManager = localClient.newSessionManager();
      ILoginInfo ili = new MockDmLoginInfo();
      ili.setUser("mark");
      ili.setPassword("mark");
      sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);
      sessionManager.getSession("MockRepositoryEventLog7.txt");
      docbase = sessionManager.getDocbaseName();
      Assert.assertEquals("MockRepositoryEventLog7.txt", docbase);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }
}
