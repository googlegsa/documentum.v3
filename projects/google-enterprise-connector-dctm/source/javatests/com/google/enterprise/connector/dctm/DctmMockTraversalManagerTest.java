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
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

import junit.framework.TestCase;

public class DctmMockTraversalManagerTest extends TestCase {
  Session session = null;

  Connector connector = null;

  DctmTraversalManager qtm = null;

  protected void setUp() throws Exception {
    super.setUp();

    qtm = null;
    connector = new DctmConnector();

    ((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
    ((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
    ((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
    ((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
    ((DctmConnector) connector)
        .setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    ((DctmConnector) connector).setIs_public("true");
    ((DctmConnector) connector)
        .setIncluded_object_type(DmInitialize.DM_INCLUDED_OBJECT_TYPE);
    ((DctmConnector) connector).setIncluded_meta(DmInitialize.DM_INCLUDED_META);
    session = (DctmSession) connector.login();
    qtm = (DctmTraversalManager) session.getTraversalManager();
  }

  public void testStartTraversal() throws RepositoryException {
    DocumentList documentList = null;
    int counter = 0;

    qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_UNBOUNDED);
    documentList = qtm.startTraversal();

    while (documentList.nextDocument() != null) {
      counter++;
    }

    assertEquals(DmInitialize.DM_RETURN_TOP_UNBOUNDED, counter);
  }

  public void testResumeTraversal() throws RepositoryException {
    session = (DctmSession) connector.login();
    qtm = (DctmTraversalManager) session.getTraversalManager();
    DocumentList documentList = null;

    String checkPoint = "{\"uuid\":\"doc2\",\"lastModified\":\"1969-01-01 01:00:00.010\"}";
    qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_BOUNDED);
    documentList = (DctmDocumentList) qtm.resumeTraversal(checkPoint);
    assertNotNull(documentList);

    int counter = 0;
    while (documentList.nextDocument() != null) {
      counter++;
    }

    assertEquals(DmInitialize.DM_RETURN_TOP_BOUNDED, counter);
  }
}
