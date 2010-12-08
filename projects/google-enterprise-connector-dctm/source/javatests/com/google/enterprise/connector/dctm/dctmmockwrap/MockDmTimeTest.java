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

import java.util.Date;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;

import junit.framework.TestCase;

public class MockDmTimeTest extends TestCase {
  IClientX dctmClientX;

  IClient localClient;

  ISessionManager sessionManager;

  ISession sess7;

  IId id;

  ISysObject object;

  IQuery query;

  String crID;

  ITime time;

  public void setUp() throws Exception {
    super.setUp();
    dctmClientX = new MockDmClient();
    localClient = null;
    localClient = dctmClientX.getLocalClient();
    sessionManager = localClient.newSessionManager();
    ILoginInfo ili = new MockDmLoginInfo();
    ili.setUser(DmInitialize.DM_LOGIN_OK4);
    ili.setPassword(DmInitialize.DM_PWD_OK4);
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, ili);
    sess7 = sessionManager.getSession(DmInitialize.DM_DOCBASE);
    query = localClient.getQuery();
    query.setDQL(DmInitialize.DM_QUERY_STRING_ENABLE);
    id = dctmClientX.getId(DmInitialize.DM_ID2);
    object = sess7.getObject(id);
    time = object.getTime("r_modify_date");
  }

  public void testGetDate() {
    Date date = time.getDate();
    long timeLong = DmInitialize.DM_ID2_TIMESTAMP;
    Date propDateVal = new Date(timeLong);
    assertEquals(propDateVal, date);
  }

  public void testGetFormattedDate() {
    String dateString = ((MockDmTime) time).getFormattedDate();
    assertEquals(DmInitialize.DM_ID2_TIMESTRING, dateString);
  }
}
