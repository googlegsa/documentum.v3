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

import java.io.ByteArrayInputStream;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

public class MockDmObjectTest extends TestCase {
  IClientX dctmClientX;

  IClient localClient;

  ISessionManager sessionManager;

  ISession sess7;

  IId id;

  ISysObject object;

  IQuery query;

  String crID;

  public void setUp() throws Exception {
    super.setUp();
    dctmClientX = new MockDmClientX();
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
  }

  public void testGetString() {
    try {
      String value = object.getString("google:docid");
      assertNotNull(value);
      assertEquals(value, DmInitialize.DM_ID2);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testGetContent() {
    try {
      ByteArrayInputStream value = object.getContent();
      assertNotNull(value);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testGetBoolean() {
    try {
      boolean value = object.getBoolean("google:ispublic");
      assertEquals(value, DmInitialize.DM_ID2_IS_PUBLIC);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testGetFormat() {
    try {
      IFormat format = object.getFormat();
      assertEquals(format.getMIMEType(), DmInitialize.DM_DEFAULT_MIMETYPE);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testGetAttrCount() {
    try {
      int count = object.getAttrCount();
      assertEquals(count, DmInitialize.DM_DEFAULT_ATTRS);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testGetId() {
    try {
      IId ID = object.getId(DmInitialize.DM_ID2);
      assertEquals(ID.toString(), DmInitialize.DM_ID2);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testGetContentSize() {
    try {
      long size = object.getContentSize();
      assertEquals(size, DmInitialize.DM_ID2_SIZE);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testGetTime() {
    try {
      ITime time = object.getTime("timestamp");
      assertNotNull(time);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testGetAttrDataType() {
    try {
      int type = object.getAttrDataType("google:ispublic");
      assertEquals(type, 1);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }

  public void testGetAttr() {
    try {
      IAttr attr = object.getAttr(1);
      assertEquals(attr.getName(), DmInitialize.DM_FIRST_ATTR);
    } catch (RepositoryException e) {
      // TODO: Why is this exception ignored?
    }
  }
}
