// Copyright 2007 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmSessionManager;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.TraversalManager;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DctmMockDocumentListTest extends TestCase {
  TraversalManager qtm = null;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    DctmConnector connector = new DctmConnector();
    connector.setLogin(DmInitialize.DM_LOGIN_OK1);
    connector.setPassword(DmInitialize.DM_PWD_OK1);
    connector.setDocbase(DmInitialize.DM_DOCBASE);
    connector.setClientX(DmInitialize.DM_CLIENTX);
    connector.setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    connector.setIs_public("false");
    connector.setIncluded_object_type(DmInitialize.DM_INCLUDED_OBJECT_TYPE);
    connector.setIncluded_meta(DmInitialize.DM_INCLUDED_META);
    Session dctmSession = connector.login();

    qtm = dctmSession.getTraversalManager();
    qtm.setBatchHint(2);
  }

  protected void tearDown() {
    MockDmSessionManager.tearDown();
  }

  public void testNextDocument() throws RepositoryException {
    ArrayList<String> expectedDocids = new ArrayList<String>();
    expectedDocids.add("users");
    for (int i = 1; i <= 26; i++) {
      expectedDocids.add("doc" + i);
    }

    ArrayList<String> actualDocids = new ArrayList<String>();
    DocumentList propertyMapList = qtm.startTraversal();
    Document pm;
    while ((pm = propertyMapList.nextDocument()) != null) {
      assertTrue(pm.getClass().getName(), pm instanceof DctmSysobjectDocument);
      Property prop = pm.findProperty(SpiConstants.PROPNAME_DOCID);
      assertNotNull(prop);
      actualDocids.add(prop.nextValue().toString());
    }

    assertEquals(expectedDocids, actualDocids);
  }

  public void testCheckpoint() throws RepositoryException {
    String checkPoint = null;

    int counter = 0;
    DocumentList propertyMapList = qtm.startTraversal();

    while ((propertyMapList.nextDocument()) != null) {
      counter++;
    }
    assertEquals(DmInitialize.DM_RETURN_TOP_UNBOUNDED, counter);

    // XXX: This time comes back as if the timestamp value were in
    // seconds, but in MockDmTimeTest we get milliseconds. Also, this
    // time is local time. Should it be UTC?
    SimpleDateFormat iso8601 =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String lastModified = iso8601.format(new Date(260 * 1000));

    checkPoint = propertyMapList.checkpoint();
    assertTrue(checkPoint,
        checkPoint.indexOf("\"uuid\":[\"doc26\"]") != -1);
    assertTrue(checkPoint,
        checkPoint.indexOf("\"lastModified\":[\"" + lastModified + "\"]") != -1);
  }
}
