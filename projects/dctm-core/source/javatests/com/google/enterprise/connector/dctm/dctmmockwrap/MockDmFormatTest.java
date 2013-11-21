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
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

public class MockDmFormatTest extends TestCase {
  private ISysObject object;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    IClientX dctmClientX = new MockDmClientX();
    IClient localClient = dctmClientX.getLocalClient();
    ISessionManager sessionManager = localClient.newSessionManager();
    ILoginInfo ili = new MockDmLoginInfo();
    ili.setUser("mark");
    ili.setPassword("mark");
    sessionManager.setIdentity(DmInitialize.DM_DOCBASE, ili);
    ISession sess7 = sessionManager.getSession(DmInitialize.DM_DOCBASE);

    IId id = dctmClientX.getId(DmInitialize.DM_ID2);
    object = (ISysObject) sess7.getObject(id);
  }

  public void testCanIndex() throws RepositoryException {
    IFormat format = object.getFormat();
    assertTrue(format.canIndex());
  }

  public void testGetMIMEType() throws RepositoryException {
    IFormat format = object.getFormat();
    String mime = ((MockDmFormat) format).getMIMEType();
    assertEquals(DmInitialize.DM_DEFAULT_MIMETYPE, mime);
  }
}
