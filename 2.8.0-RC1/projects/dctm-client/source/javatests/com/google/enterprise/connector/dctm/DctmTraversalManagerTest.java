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

import java.text.SimpleDateFormat;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.Value;

import junit.framework.TestCase;

public class DctmTraversalManagerTest extends TestCase {
  Session session = null;

  Connector connector = null;

  DctmTraversalManager qtm = null;

  protected void setUp() throws Exception {
    super.setUp();
    qtm = null;
    Connector connector = new DctmConnector();
    ((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
    ((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
    ((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
    ((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
    ((DctmConnector) connector)
        .setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    ((DctmConnector) connector).setIs_public("false");
    ((DctmConnector) connector)
        .setIncluded_object_type(DmInitialize.DM_INCLUDED_OBJECT_TYPE);

    ((DctmConnector) connector)
        .setRoot_object_type(DmInitialize.ROOT_OBJECT_TYPE);
    Session sess = (DctmSession) connector.login();
    qtm = (DctmTraversalManager) sess.getTraversalManager();
  }

  public void testStartTraversal() throws RepositoryException {
    DocumentList list = null;
    int counter = 0;

    qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_UNBOUNDED);
    list = qtm.startTraversal();

    while (list.nextDocument() != null) {
      counter++;
    }
    assertEquals(DmInitialize.DM_RETURN_TOP_UNBOUNDED, counter);
  }

  public void testResumeTraversal() throws RepositoryException {
    DocumentList propertyMapList = null;

    String checkPoint = DmInitialize.DM_CHECKPOINT;

    qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_BOUNDED);
    propertyMapList = qtm.resumeTraversal(checkPoint);

    int counter = 0;
    while (propertyMapList.nextDocument() != null) {
      counter++;
    }
    assertEquals(DmInitialize.DM_RETURN_TOP_BOUNDED, counter);
  }

  public void testResumeTraversalWithSimilarDate() throws RepositoryException {
    DocumentList documentList = null;

    String checkPoint = "{\"uuid\":\"090000018000015d\",\"lastModified\":\"2006-12-14 20:09:13\"}";

    qtm.setBatchHint(1);
    documentList = qtm.resumeTraversal(checkPoint);

    DctmSysobjectDocument map = null;
    while ((map = (DctmSysobjectDocument) documentList.nextDocument()) != null) {
      String docId = map.findProperty(SpiConstants.PROPNAME_DOCID)
          .nextValue().toString();
      String expectedid = "090000018000015e";
      assertEquals(expectedid, docId);
      Value date =
          map.findProperty(SpiConstants.PROPNAME_LASTMODIFIED).nextValue();
      String modifyDate = date.toString();
      // TODO: may have to adjust the assertion for the date format returned by Value.toString()
      System.out.println("testResumeTraversalWithSimilarDate modifyDate = '" + modifyDate + "'");
      String expecterModifyDate = "2006-12-14 20:09:13";
      assertEquals(expecterModifyDate, modifyDate);
    }
  }
}
