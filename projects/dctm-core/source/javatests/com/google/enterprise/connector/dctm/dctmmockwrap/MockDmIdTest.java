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

import junit.framework.TestCase;

public class MockDmIdTest extends TestCase {
  IClientX dctmClientX;

  IClient localClient;

  ISessionManager sessionManager;

  ISession sess7;

  public void setUp() throws Exception {
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

  public void testToString() {
    IId id = dctmClientX.getId(DmInitialize.DM_ID2);
    String idString = id.toString();
    assertEquals(idString, DmInitialize.DM_ID2);
  }
}
