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

package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class DctmMockConnectorTest extends TestCase {
  public void testLogin() throws RepositoryException {
    DctmConnector connector = new DctmConnector();
    connector.setLogin(DmInitialize.DM_LOGIN_OK1);
    connector.setPassword(DmInitialize.DM_PWD_OK1);
    connector.setDocbase(DmInitialize.DM_DOCBASE);
    connector.setClientX(DmInitialize.DM_CLIENTX);
    connector.setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    connector.setIs_public("true");
    DctmSession session = connector.login();
    assertNotNull(session);
    DctmTraversalManager qtm = session.getTraversalManager();
    assertNotNull(qtm);
    assertEquals(DmInitialize.DM_WEBTOP_SERVER_URL, qtm.getServerUrl());
  }

  public void testSetWhere_clause() {
    DctmConnector connector = new DctmConnector();
    String[] before = { " and 1=1", "  ", "4 = 5  " };
    connector.setWhere_clause(Arrays.asList(before));
    List<String> after = connector.getWhereClause();
    assertEquals(2, after.size());
    assertTrue(after.toString(), after.contains("1=1"));
    assertTrue(after.toString(), after.contains("4 = 5"));
  }
}
