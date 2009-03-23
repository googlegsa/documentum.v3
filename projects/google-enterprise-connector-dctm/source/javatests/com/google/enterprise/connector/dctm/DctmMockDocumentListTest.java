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
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.TraversalManager;

import junit.framework.TestCase;

public class DctmMockDocumentListTest extends TestCase {
  TraversalManager qtm = null;

  DctmSession dctmSession = null;

  DctmConnector connector = null;

  public void setUp() throws Exception {
    super.setUp();

    connector = new DctmConnector();
    ((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
    ((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
    ((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
    ((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
    ((DctmConnector) connector)
        .setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    ((DctmConnector) connector).setIs_public("false");
    dctmSession = (DctmSession) connector.login();

    qtm = (DctmTraversalManager) dctmSession.getTraversalManager();
    qtm.setBatchHint(2);
  }

  /*
   * Test method for
   * 'com.google.enterprise.connector.dctm.DctmDocumentList.nextDocument()'
   */
  public void testNextDocument() throws RepositoryException {
    int counter = 0;
    DocumentList propertyMapList = qtm.startTraversal();
    Document pm = null;
    Property prop = null;

    while ((pm = propertyMapList.nextDocument()) != null) {

      assertTrue(pm instanceof DctmSysobjectDocument);
      prop = pm.findProperty(SpiConstants.PROPNAME_DOCID);

      assertNotNull(prop);
      assertEquals("users", prop.nextValue().toString());
      counter++;
      if (counter == 1) {
        break;
      }
    }
  }

  /*
   * Test method for
   * 'com.google.enterprise.connector.dctm.DctmDocumentList.checkpoint()'
   */
  public void testCheckpoint() throws RepositoryException {
    String checkPoint = null;

    int counter = 0;
    DocumentList propertyMapList = qtm.startTraversal();

    while ((propertyMapList.nextDocument()) != null) {
      counter++;
    }
    checkPoint = propertyMapList.checkpoint();
    assertEquals(
        "{\"uuid\":\"doc26\",\"lastModified\":\"1970-01-01 01:00:00\"}",
        checkPoint);
  }
}
