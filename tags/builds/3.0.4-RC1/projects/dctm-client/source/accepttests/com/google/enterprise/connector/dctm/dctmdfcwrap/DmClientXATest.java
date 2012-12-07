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

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmClientXATest extends TestCase {
  IClientX clientX;

  public void setUp() throws Exception {
    super.setUp();
    clientX = new DmClientX();
  }

  public void testGetId() {
    IId id = clientX.getId("xxxxxxxxxxxxxxxx");
    Assert.assertTrue(id instanceof DmId);
  }

  public void testGetLocalClient() throws RepositoryException {
    IClient localClient = clientX.getLocalClient();
    Assert.assertTrue(localClient instanceof DmClient);
  }

  public void testGetQuery() {
    IQuery query = clientX.getQuery();
    Assert.assertTrue(query instanceof DmQuery);
  }

  public void testGetLoginInfo() {
    ILoginInfo loginInfo = clientX.getLoginInfo();
    Assert.assertTrue(loginInfo instanceof DmLoginInfo);
    loginInfo.setUser("max");
    loginInfo.setPassword("foo");
    Assert.assertEquals("max", loginInfo.getUser());
    Assert.assertEquals("foo", loginInfo.getPassword());
  }
}
