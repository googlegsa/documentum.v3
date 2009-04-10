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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.AuthorizationResponse;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SimpleAuthenticationIdentity;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DctmMockAuthorizationManagerTest extends TestCase {
  public final void testAuthorizeDocids() throws RepositoryException {

    AuthorizationManager authorizationManager;
    authorizationManager = null;
    Connector connector = new DctmConnector();

    ((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
    ((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
    ((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
    ((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
    ((DctmConnector) connector)
        .setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    ((DctmConnector) connector).setIs_public("false");
    Session sess = (DctmSession) connector.login();
    authorizationManager = (DctmAuthorizationManager) sess
        .getAuthorizationManager();
    assertNotNull(authorizationManager);

    {
      String username = DmInitialize.DM_LOGIN_OK2;

      Map expectedResults = new HashMap();
      expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID2, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID3, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID4, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID5, Boolean.FALSE);
      assertNotNull((DctmAuthorizationManager) authorizationManager);
      assertNotNull(expectedResults);
      assertNotNull(username);
      testAuthorization((DctmAuthorizationManager) authorizationManager,
          expectedResults, username);
    }

    {
      String username = DmInitialize.DM_LOGIN_OK3;

      Map expectedResults = new HashMap();
      expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID2, Boolean.FALSE);
      expectedResults.put(DmInitialize.DM_ID3, Boolean.FALSE);
      expectedResults.put(DmInitialize.DM_ID4, Boolean.FALSE);
      expectedResults.put(DmInitialize.DM_ID5, Boolean.FALSE);
      testAuthorization((DctmAuthorizationManager) authorizationManager,
          expectedResults, username);
    }

    {
      String username = DmInitialize.DM_LOGIN_OK1;

      Map expectedResults = new HashMap();
      expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID2, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID3, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID4, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID5, Boolean.TRUE);

      testAuthorization((DctmAuthorizationManager) authorizationManager,
          expectedResults, username);
    }
  }

  private void testAuthorization(
      DctmAuthorizationManager authorizationManager, Map expectedResults,
      String username) throws RepositoryException {
    List docids = new LinkedList(expectedResults.keySet());

    assertNotNull(docids);
    List list = (List) authorizationManager.authorizeDocids(docids,
        new SimpleAuthenticationIdentity(username, null));
    assertNotNull(list);
    for (Iterator i = list.iterator(); i.hasNext();) {
      AuthorizationResponse pm = (AuthorizationResponse) i.next();
      String uuid = pm.getDocid();
      boolean ok = pm.isValid();
      Boolean expected = (Boolean) expectedResults.get(uuid);
      Assert.assertEquals(username + " access to " + uuid, expected
          .booleanValue(), ok);
    }
  }
}
