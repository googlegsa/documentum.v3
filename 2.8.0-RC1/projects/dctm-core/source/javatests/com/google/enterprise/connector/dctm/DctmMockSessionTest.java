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

package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.TraversalManager;

import junit.framework.TestCase;

public class DctmMockSessionTest extends TestCase {
  DctmSession dctmSession = null;

  public void setUp() throws Exception {
    super.setUp();

    DctmConnector connector = new DctmConnector();
    connector.setLogin(DmInitialize.DM_LOGIN_OK1);
    connector.setPassword(DmInitialize.DM_PWD_OK1);
    connector.setDocbase(DmInitialize.DM_DOCBASE);
    connector.setClientX(DmInitialize.DM_CLIENTX);
    connector.setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    connector.setIs_public("false");
    dctmSession = (DctmSession) connector.login();
  }

  public void testGetQueryTraversalManager() throws RepositoryException {
    TraversalManager DctmQm = dctmSession.getTraversalManager();
    String serverUrl = ((DctmTraversalManager) DctmQm).getServerUrl();
    assertNotNull(DctmQm);

    assertEquals(serverUrl, DmInitialize.DM_WEBTOP_SERVER_URL);
  }

  public void testGetAuthenticationManager() throws InstantiationException,
      IllegalAccessException, ClassNotFoundException {
    AuthenticationManager DctmAm = dctmSession.getAuthenticationManager();
    assertNotNull(DctmAm);
  }

  public void testGetAuthorizationManager() throws InstantiationException,
      IllegalAccessException, ClassNotFoundException {
    AuthorizationManager DctmAz = dctmSession.getAuthorizationManager();
    assertNotNull(DctmAz);
  }
}
