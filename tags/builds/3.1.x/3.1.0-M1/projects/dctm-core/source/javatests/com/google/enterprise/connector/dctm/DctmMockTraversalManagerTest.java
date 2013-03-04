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
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SimpleTraversalContext;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class DctmMockTraversalManagerTest extends TestCase {
  DctmConnector connector;
  DctmTraversalManager qtm;

  protected void setUp() throws RepositoryException {
    connector = new DctmConnector();
    connector.setLogin(DmInitialize.DM_LOGIN_OK1);
    connector.setPassword(DmInitialize.DM_PWD_OK1);
    connector.setDocbase(DmInitialize.DM_DOCBASE);
    connector.setClientX(DmInitialize.DM_CLIENTX);
    connector.setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    connector.setIs_public("true");
    List<String> whereClause = new ArrayList<String>();
    whereClause.add("first");
    whereClause.add("second");
    connector.setWhere_clause(whereClause);
    connector.setIncluded_object_type(DmInitialize.DM_INCLUDED_OBJECT_TYPE);
    connector.setIncluded_meta(DmInitialize.DM_INCLUDED_META);
    Session session = connector.login();
    qtm = (DctmTraversalManager) session.getTraversalManager();
  }

  public void testStartTraversal() throws RepositoryException {
    qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_UNBOUNDED);
    DocumentList documentList = qtm.startTraversal();

    int counter = 0;
    while (documentList.nextDocument() != null) {
      counter++;
    }
    assertEquals(DmInitialize.DM_RETURN_TOP_UNBOUNDED, counter);
  }

  public void testResumeTraversal() throws RepositoryException {
    String checkPoint =
        "{\"uuid\":\"doc2\",\"lastModified\":\"1969-01-01 01:00:00.010\"}";
    qtm.setBatchHint(DmInitialize.DM_RETURN_TOP_BOUNDED);
    DocumentList documentList = qtm.resumeTraversal(checkPoint);

    assertNotNull(documentList);
    int counter = 0;
    while (documentList.nextDocument() != null) {
      counter++;
    }
    assertEquals(DmInitialize.DM_RETURN_TOP_BOUNDED, counter);
  }

  /**
   * Extends the production TraversalManager to override execQuery
   * with a mock imlementation that counts calls to that method and
   * optionally delays to simulate the execution of the queries.
   */
  private class MockTraversalManager extends DctmTraversalManager {
    private final long delayMillis;
    int count = 0;

    MockTraversalManager() throws RepositoryException {
      this(0);
    }

    MockTraversalManager(long delaySeconds) throws RepositoryException {
      super(connector, null);
      this.delayMillis = delaySeconds * 1000L;
    }

    @Override
    public DocumentList execQuery(Checkpoint checkpoint)
        throws RepositoryException {
      count++;
      if (delayMillis > 0) {
        try {
          Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
          throw new RepositoryException(e);
        }
      }
      return ((checkpoint.getInsertIndex() == -1 
                && checkpoint.getAclId() == null)
             || (checkpoint.getInsertIndex() != -1 
                && checkpoint.getInsertId() == null))
             ? new MockDocumentList() : null;
    }
  }

  /**
   * A minimal DocumentList: all that is required is the construction
   * of an instance.
   */
  private static class MockDocumentList implements DocumentList {
    public Document nextDocument() { return null; }
    public String checkpoint() { return null; }
  }

  public void testGetDocumentList_start() throws RepositoryException {
    MockTraversalManager mtm = new MockTraversalManager();

    DocumentList documentList = mtm.startTraversal();
    assertNotNull(documentList);
    assertEquals(1, mtm.count);
  }

  /** Resumes the traversal on the first where clause with updates. */
  public void testGetDocumentList_resumeFirst() throws RepositoryException {
    // The nulls in the checkpoint simulate new content with our execQuery stub.
    MockTraversalManager mtm = new MockTraversalManager();
    String checkpoint =
        "{index:0,uuid:[null,'one'],lastModified=[null,'2010-04-01 00:01:00']}";

    DocumentList documentList = mtm.resumeTraversal(checkpoint);
    assertNotNull(documentList);
    assertEquals(1, mtm.count);
  }

  /** Resumes the traversal on the second where clause with udpates. */
  public void testGetDocumentList_resumeSecond() throws RepositoryException {
    MockTraversalManager mtm = new MockTraversalManager();
    String checkpoint =
        "{index:1, uuid:['one'],lastModified=['2010-04-01 00:01:00']}";

    DocumentList documentList = mtm.resumeTraversal(checkpoint);
    assertNotNull(documentList);
    assertEquals(1, mtm.count);
  }

  /**
   * Resumes the traversal on the first where clause and continues to
   * find updates on the second where clause.
   */
  public void testGetDocumentList_resumeFirstEmpty()
      throws RepositoryException {
    // The nulls in the checkpoint simulate new content with our execQuery stub.
    MockTraversalManager mtm = new MockTraversalManager();
    String checkpoint =
        "{index:0,uuid:['one',null],lastModified=['2010-04-01 00:01:00',null]}";

    DocumentList documentList = mtm.resumeTraversal(checkpoint);
    assertNotNull(documentList);
    assertEquals(2, mtm.count);
  }

  /**
   * Resumes the traversal on the second where clause and wraps to
   * find updates on the first where clause.
   */
  public void testGetDocumentList_resumeSecondEmpty()
      throws RepositoryException {
    // The nulls in the checkpoint simulate new content with our execQuery stub.
    MockTraversalManager mtm = new MockTraversalManager();
    String checkpoint =
        "{index:1,uuid:[null,'one'],lastModified=[null,'2010-04-01 00:01:00']}";

    DocumentList documentList = mtm.resumeTraversal(checkpoint);
    assertNotNull(documentList);
    assertEquals(2, mtm.count);
  }

  /** Test for resuming ACLS traversal. */
  public void testGetDocumentList_resumeAcls() throws RepositoryException {
    MockTraversalManager mtm = new MockTraversalManager();
    String checkpoint = "{index:0,uuid:['one','another'],"
        + "lastModified=['2010-04-01 00:01:00','2010-10-27 22:55:03']}";

    DocumentList documentList = mtm.resumeTraversal(checkpoint);
    assertNotNull(documentList);
    assertEquals(3, mtm.count);
  }

  /** Tests reaching the end of the traversal. */
  public void testGetDocumentList_end() throws RepositoryException {
    MockTraversalManager mtm = new MockTraversalManager();
    String checkpoint = "{index:0,aclid:'id1',uuid:['one','another'],"
        + "lastModified=['2010-04-01 00:01:00','2010-10-27 22:55:03']}";

    DocumentList documentList = mtm.resumeTraversal(checkpoint);
    assertNull(documentList);
    assertEquals(3, mtm.count);
  }

  /** Tests adding a new where clause to the configuration. */
  public void testGetDocumentList_newAcls()
      throws RepositoryException {
    MockTraversalManager mtm = new MockTraversalManager();
    String checkpoint =
        "{uuid:['one'],lastModified:['2010-04-01 00:01:00'], index:0}";
    DocumentList documentList = mtm.resumeTraversal(checkpoint);
    assertNotNull(documentList);
    assertEquals(2, mtm.count);
  }

  public void testGetDocumentList_timeout() throws RepositoryException {
    // Delay one second in the queries, and set a traversal time limit
    // of one second, so that the loop in getDocumentList will timeout.
    MockTraversalManager mtm = new MockTraversalManager(1);
    SimpleTraversalContext traversalContext = new SimpleTraversalContext();
    traversalContext.setTraversalTimeLimitSeconds(1);
    mtm.setTraversalContext(traversalContext);
    String checkpoint = "{index:0,uuid:['one','another'],"
        + "lastModified=['2010-04-01 00:01:00','2010-10-27 22:55:03']}";

    DocumentList documentList = mtm.resumeTraversal(checkpoint);
    assertNotNull(documentList);
    assertNull(documentList.nextDocument());
    assertNotNull(documentList.checkpoint()); // It's not MockDocumentList.
    assertEquals(1, mtm.count); // Would have been 2 without the timeout.
  }
}
