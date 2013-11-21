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
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.AuthorizationResponse;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SimpleAuthenticationIdentity;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  private void testQueryString(int size, boolean containsOr) {
    List<String> docidList = new ArrayList<String>();
    for (int i = 0; i < size; i++) {
      docidList.add("xyzzy" + i);
    }

    DctmAuthorizationManager out =
        (DctmAuthorizationManager) authorizationManager;
    String queryString = out.buildQueryString(docidList);

    assertEquals(queryString, containsOr,
        queryString.contains(DctmAuthorizationManager.QUERY_STRING_OR));

    // This would happen if QUERY_STRING_OR were appended on the end,
    // with the last character replaced by by ')'.
    assertFalse(queryString, queryString.contains(
        DctmAuthorizationManager.QUERY_STRING_OR.replace('(', ')')));

    // Check for missing or duplicate docids in the query.
    for (String docid : docidList) {
      // We need a unique target that is not a substring of any other targets.
      String target = "'" + docid + "'";
      int index = queryString.indexOf(target);
      assertTrue("Missing docid: " + docid, index != -1);
      assertEquals("Duplicate docid: " + docid,
          -1, queryString.indexOf(target, index + 1));
    }
  }

  public void testQueryString_tooShort() {
    testQueryString(10, false);
  }

  public void testQueryString_justRight() {
    testQueryString(400, false);
  }

  public void testQueryString_barelyTooLong() {
    testQueryString(401, true);
  }

  public void testQueryString_tooLong() {
    testQueryString(500, true);
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
