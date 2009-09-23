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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.AuthorizationResponse;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SimpleAuthenticationIdentity;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DctmMockAuthorizationManagerTest extends TestCase {
  private AuthorizationManager authorizationManager;

  protected void setUp() throws RepositoryException {
    DctmConnector connector = new DctmConnector();
    connector.setLogin(DmInitialize.DM_LOGIN_OK1);
    connector.setPassword(DmInitialize.DM_PWD_OK1);
    connector.setDocbase(DmInitialize.DM_DOCBASE);
    connector.setClientX(DmInitialize.DM_CLIENTX);
    connector.setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    connector.setIs_public("false");
    Session sess = connector.login();
    authorizationManager = sess.getAuthorizationManager();
    assertNotNull(authorizationManager);
  }

  public final void testOk2() throws RepositoryException {
      Map<String, Boolean> expectedResults = new HashMap<String, Boolean>();
      expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID2, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID3, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID4, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID5, Boolean.FALSE);
      testAuthorization(expectedResults, DmInitialize.DM_LOGIN_OK2);
    }

  public final void testOk2Domain() throws RepositoryException {
      Map<String, Boolean> expectedResults = new HashMap<String, Boolean>();
      expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID2, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID3, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID4, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID5, Boolean.FALSE);
      testAuthorization(expectedResults, DmInitialize.DM_LOGIN_OK2_DOMAIN);
    }

  public final void testOk2DnsDomain() throws RepositoryException {
      Map<String, Boolean> expectedResults = new HashMap<String, Boolean>();
      expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID2, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID3, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID4, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID5, Boolean.FALSE);
      testAuthorization(expectedResults, DmInitialize.DM_LOGIN_OK2_DNS_DOMAIN);
    }

  public final void testOk3() throws RepositoryException {
      Map<String, Boolean> expectedResults = new HashMap<String, Boolean>();
      expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID2, Boolean.FALSE);
      expectedResults.put(DmInitialize.DM_ID3, Boolean.FALSE);
      expectedResults.put(DmInitialize.DM_ID4, Boolean.FALSE);
      expectedResults.put(DmInitialize.DM_ID5, Boolean.FALSE);
      testAuthorization(expectedResults, DmInitialize.DM_LOGIN_OK3);
    }

  public final void testOk1() throws RepositoryException {
      Map<String, Boolean> expectedResults = new HashMap<String, Boolean>();
      expectedResults.put(DmInitialize.DM_ID1, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID2, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID3, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID4, Boolean.TRUE);
      expectedResults.put(DmInitialize.DM_ID5, Boolean.TRUE);
      testAuthorization(expectedResults, DmInitialize.DM_LOGIN_OK1);
  }

  private void testAuthorization(Map<String, Boolean> expectedResults,
      String username) throws RepositoryException {
    Collection<AuthorizationResponse> list =
        authorizationManager.authorizeDocids(expectedResults.keySet(),
            new SimpleAuthenticationIdentity(username, null));
    assertNotNull(list);
    assertEquals(expectedResults.size(), list.size());
    for (AuthorizationResponse pm : list) {
      String uuid = pm.getDocid();
      boolean ok = pm.isValid();
      Boolean expected = expectedResults.get(uuid);
      Assert.assertEquals(username + " access to " + uuid,
          expected.booleanValue(), ok);
    }
  }
}
