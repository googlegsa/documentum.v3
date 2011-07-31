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

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

public class MockDmFormatTest extends TestCase {
  IClientX dctmClientX;

  IClient localClient;

  ISessionManager sessionManager;

  ISession sess7;

  IId id;

  ISysObject object;

  IQuery query;

  String crID;

  IFormat format;

  public void setUp() throws Exception {
    super.setUp();
    dctmClientX = new MockDmClientX();
    localClient = null;
    localClient = dctmClientX.getLocalClient();
    sessionManager = localClient.newSessionManager();
    ILoginInfo ili = new MockDmLoginInfo();
    ili.setUser("mark");
    ili.setPassword("mark");
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, ili);
    sess7 = sessionManager.getSession(DmInitialize.DM_DOCBASE);
    query = localClient.getQuery();
    query.setDQL(DmInitialize.DM_QUERY_STRING_ENABLE);

    id = dctmClientX.getId(DmInitialize.DM_ID2);
    object = sess7.getObject(id);
    try {
      format = object.getFormat();
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testCanIndex() {
    boolean indexable = format.canIndex();
    assertTrue(indexable);
  }

  public void testGetMIMEType() {
    String mime = "";
    mime = ((MockDmFormat) format).getMIMEType();
    assertEquals(mime, DmInitialize.DM_DEFAULT_MIMETYPE);
  }
}
