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

package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;

import com.documentum.fc.common.DfException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmSessionManagerATest extends TestCase {
  IClientX dctmClientX;

  IClient localClient;

  ISessionManager sessionManager;

  ILoginInfo loginInfo;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dctmClientX = new DmClientX();
    localClient = dctmClientX.getLocalClient();
    sessionManager = localClient.newSessionManager();
    loginInfo = dctmClientX.getLoginInfo();
  }

  public void testGetSession() throws RepositoryException {
    String user = DmInitialize.DM_LOGIN_OK4;
    String password = DmInitialize.DM_PWD_OK4;
    String docbase = DmInitialize.DM_DOCBASE;

    loginInfo.setUser(user);
    loginInfo.setPassword(password);

    sessionManager.setIdentity(docbase, loginInfo);

    ISession session = null;
    try {
      session = sessionManager.getSession(docbase);
      Assert.assertNotNull(session);
      Assert.assertTrue(session instanceof DmSession);
    } finally {
      if (session != null) {
        sessionManager.release(session);
      }
    }
  }

  public void testGetIdentity() throws RepositoryLoginException {
    loginInfo.setUser(DmInitialize.DM_LOGIN_OK4);
    loginInfo.setPassword(DmInitialize.DM_PWD_OK4);
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);

    ILoginInfo logInfo = sessionManager
        .getIdentity(DmInitialize.DM_DOCBASE);
    Assert.assertEquals(logInfo.getUser(), DmInitialize.DM_LOGIN_OK4);
    Assert.assertEquals(logInfo.getPassword(), DmInitialize.DM_PWD_OK4);
  }

  public void testNewSession() throws RepositoryLoginException,
      RepositoryException {
    ISession session = null;
    try {
      loginInfo.setUser(DmInitialize.DM_LOGIN_OK4);
      loginInfo.setPassword(DmInitialize.DM_PWD_OK4);
      sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);
      session = sessionManager.newSession(DmInitialize.DM_DOCBASE);
      Assert.assertNotNull(session);
      Assert.assertTrue(session instanceof DmSession);
    } finally {
      if (session != null) {
        sessionManager.release(session);
      }
    }
  }

  @SuppressWarnings("deprecation") // For ISessionManager.authenticate.
  public void testAuthenticateOK() throws RepositoryLoginException,
      DfException {
    loginInfo.setUser(DmInitialize.DM_LOGIN_OK4);
    loginInfo.setPassword(DmInitialize.DM_PWD_OK4);
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);
    boolean rep = sessionManager.authenticate(DmInitialize.DM_DOCBASE);
    Assert.assertTrue(rep);
  }

  @SuppressWarnings("deprecation") // For ISessionManager.authenticate.
  public void testAuthenticateK0() throws RepositoryLoginException,
      DfException {
    loginInfo.setUser(DmInitialize.DM_LOGIN_OK4);
    loginInfo.setPassword(DmInitialize.DM_PWD_KO);
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);
    Assert.assertFalse(sessionManager
        .authenticate(DmInitialize.DM_DOCBASE));
  }

  public void testClearIdentity() throws RepositoryLoginException {

    loginInfo.setUser(DmInitialize.DM_LOGIN_OK4);
    loginInfo.setPassword(DmInitialize.DM_PWD_OK4);
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);
    sessionManager.clearIdentity(DmInitialize.DM_DOCBASE);
    ILoginInfo logInfo = sessionManager
        .getIdentity(DmInitialize.DM_DOCBASE);
    Assert.assertNull(((DmLoginInfo) logInfo).getIdfLoginInfo());
  }
}
