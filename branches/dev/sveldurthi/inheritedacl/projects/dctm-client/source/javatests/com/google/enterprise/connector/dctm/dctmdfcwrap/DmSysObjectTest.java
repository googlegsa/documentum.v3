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
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;

import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmSysObjectTest extends TestCase {
  ISysObject object;

  ISession session = null;

  ISessionManager sessionManager;

  IClientX dctmClientX;

  public void setUp() throws Exception {
    super.setUp();

    IClient localClient;

    ILoginInfo loginInfo;
    dctmClientX = new DmClientX();
    localClient = dctmClientX.getLocalClient();
    sessionManager = localClient.newSessionManager();
    loginInfo = dctmClientX.getLoginInfo();
    loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
    loginInfo.setPassword(DmInitialize.DM_PWD_OK1);
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, loginInfo);

    session = sessionManager.getSession(DmInitialize.DM_DOCBASE);

    object = (ISysObject) session.getObject(dctmClientX.getId(DmInitialize.DM_ID1));
    object.setSessionManager(sessionManager);
  }

  public void testGetFormat() throws RepositoryException {
    try {
      IFormat format = object.getFormat();
      Assert.assertNotNull(format);
    } finally {
      if (session != null) {
        sessionManager.release(session);
      }
    }
  }

  public void testGetContentSize() throws RepositoryException {
    try {
      long size = object.getContentSize();
      assertTrue(size > 0);
    } finally {
      if (session != null) {
        sessionManager.release(session);
      }
    }
  }

  public void testGetString() throws RepositoryException {
    try {
      object = (ISysObject) session.getObject(dctmClientX.getId("0900000180041704"));
      assertEquals("Alpha", object.getString("keywords"));
      assertEquals("Marketing Plan", object.getString("title"));
      assertEquals("Fri Apr 13 15:08:03 CEST 2007", object
          .getString("r_creation_date"));
    } finally {
      if (session != null) {
        sessionManager.release(session);
      }
    }
  }

  public void testGetContent() throws RepositoryException {
    try {
      object = (ISysObject) session.getObject(dctmClientX.getId("0900000180041704"));
      sessionManager.release(session);
      object.getContent();
      // assertEquals("Alpha, Beta", object.getString("keywords"));
      // assertEquals("Marketing Plan", object.getString("title"));
      // assertEquals("Fri Apr 13 15:08:03 CEST
      // 2007", object.getString("r_creation_date"));
    } finally {
      // if (session != null) {
      // sessionManager.release(session);
      // }
    }
  }
}
