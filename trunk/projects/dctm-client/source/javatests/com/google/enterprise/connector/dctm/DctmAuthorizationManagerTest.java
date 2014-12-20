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

import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.AuthorizationResponse;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SimpleAuthenticationIdentity;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DctmAuthorizationManagerTest extends TestCase {
  public DctmAuthorizationManagerTest(String arg0) {
    super(arg0);
  }

  public final void testAuthorizeDocids() throws RepositoryException {
    DctmConnector connector = new DctmConnector();
    connector.setLogin(DmInitialize.DM_LOGIN_OK1);
    connector.setPassword(DmInitialize.DM_PWD_OK1);
    connector.setDocbase(DmInitialize.DM_DOCBASE);
    connector.setClientX(DmInitialize.DM_CLIENTX);
    connector.setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    connector.setIs_public("false");
    Session sess = connector.login();
    AuthorizationManager authorizationManager = sess.getAuthorizationManager();

    {
      String username = DmInitialize.DM_LOGIN_OK2;

      Map<String, Boolean> expectedResults = new HashMap<String, Boolean>();
      expectedResults.put(DmInitialize.DM_VSID1, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_VSID2, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_VSID3, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_VSID4, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_VSID5, Boolean.TRUE);
      testAuthorization(authorizationManager, expectedResults, username);
    }

    {
      String username = DmInitialize.DM_LOGIN_OK3;

      Map<String, Boolean> expectedResults = new HashMap<String, Boolean>();
      expectedResults.put(DmInitialize.DM_VSID1, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_VSID2, Boolean.FALSE);
      expectedResults.put(DmInitialize.DM_VSID3, Boolean.FALSE);
      expectedResults.put(DmInitialize.DM_VSID4, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_VSID5, Boolean.TRUE);
      testAuthorization(authorizationManager, expectedResults, username);
    }

    {
      String username = DmInitialize.DM_LOGIN_OK5;

      Map<String, Boolean> expectedResults = new HashMap<String, Boolean>();
      expectedResults.put(DmInitialize.DM_VSID1, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_VSID2, Boolean.FALSE);
      expectedResults.put(DmInitialize.DM_VSID3, Boolean.FALSE);
      expectedResults.put(DmInitialize.DM_VSID4, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_VSID5, Boolean.TRUE);
      testAuthorization(authorizationManager, expectedResults, username);
    }
  }

  private void testAuthorization(
      AuthorizationManager authorizationManager,
      Map<String, Boolean> expectedResults, String username)
      throws RepositoryException {
    Collection<AuthorizationResponse> list =
        authorizationManager.authorizeDocids(expectedResults.keySet(),
            new SimpleAuthenticationIdentity(username, null));
    assertNotNull(list);
    for (AuthorizationResponse pm : list) {
      String uuid = pm.getDocid();
      boolean ok = pm.isValid();
      Boolean expected = expectedResults.get(uuid);
      Assert.assertNotNull(expected);
      Assert.assertEquals(username + " access to " + uuid,
          expected.booleanValue(), ok);
    }
  }
}
