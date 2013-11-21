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
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmQueryTest extends TestCase {
  IQuery query;

  ISessionManager sessionManager;

  IClientX dctmClientX;

  @Override
  protected void setUp() throws Exception {
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
    query = dctmClientX.getQuery();
  }

  public void testSetDQL() {
    Assert.assertNotNull(query);
    Assert.assertTrue(query instanceof DmQuery);
    query.setDQL(DmInitialize.DM_QUERY_STRING_ENABLE);
  }

  public void testExecute() throws RepositoryException {
    ISession session = sessionManager.getSession(DmInitialize.DM_DOCBASE);
    try {
      Assert.assertNotNull(query);
      Assert.assertTrue(query instanceof DmQuery);
      query.setDQL(DmInitialize.DM_QUERY_STRING_ENABLE);
      ICollection collec = query.execute(session, IQuery.READ_QUERY);
      Assert.assertNotNull(collec);
    } finally {
      sessionManager.release(session);
    }
  }
}
