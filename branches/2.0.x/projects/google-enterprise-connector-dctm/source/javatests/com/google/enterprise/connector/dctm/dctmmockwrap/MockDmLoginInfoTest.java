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
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

import junit.framework.TestCase;

public class MockDmLoginInfoTest extends TestCase {
  IClientX dctmClientX;

  IClient localClient;

  ISessionManager sessionManager;

  ISession sess7;

  ILoginInfo ili;

  public void setUp() throws Exception {
    super.setUp();
    dctmClientX = new MockDmClient();
    localClient = null;
    localClient = dctmClientX.getLocalClient();
    sessionManager = localClient.newSessionManager();

    ili = new MockDmLoginInfo();
  }

  public void testSetGetUser() {
    ili.setUser(DmInitialize.DM_LOGIN_OK4);
    String user = ili.getUser();
    assertEquals(user, DmInitialize.DM_LOGIN_OK4);
  }

  public void testSetGetPassword() {
    ili.setPassword(DmInitialize.DM_PWD_OK4);
    String pwd = ili.getPassword();
    assertEquals(pwd, DmInitialize.DM_PWD_OK4);
  }
}
