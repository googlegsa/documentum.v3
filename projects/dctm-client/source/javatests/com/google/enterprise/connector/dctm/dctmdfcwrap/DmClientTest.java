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
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmClientTest extends TestCase {
  IClientX dctmClientX;

  IClient localClient;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dctmClientX = new DmClientX();
    localClient = dctmClientX.getLocalClient();
  }

  public void testNewSessionManager() throws RepositoryException {
    ISessionManager sessionManager = localClient.newSessionManager();
    Assert.assertNotNull(sessionManager);
    Assert.assertTrue(sessionManager instanceof DmSessionManager);
  }
}
