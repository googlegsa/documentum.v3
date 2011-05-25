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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.util.ArrayList;

import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.mock.MockRepositoryProperty;
import com.google.enterprise.connector.mock.MockRepositoryPropertyList;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;

/**
 * @author jpasquon This list will be utilized only by the MockJcrQueryResult
 *         (which constructor takes a List as parameter) Only Iterator is called
 *         and only next and hasNext are called on this parameter. The most
 *         important thing to ensure is that the Object returned by next can be
 *         cast as a MockRepositoryDocument.
 */
public class MockMockList extends ArrayList<MockRepositoryDocument> {
  MockMockList(String[] ids, ISessionManager sessionManager, String docbase)
      throws RepositoryLoginException, RepositoryException {
    String claimant = sessionManager.getIdentity(docbase).getUser();
    MockRepositoryDocumentStore store = ((MockDmSession) sessionManager
        .getSession(docbase)).getStore();
    for (int j = 0; j < ids.length; j++) {
      MockRepositoryDocument doc = store.getDocByID(ids[j]);// if no
      // 'content'
      // defined,
      // doc==null
      if (doc != null) {
        MockRepositoryPropertyList pl = doc.getProplist();
        MockRepositoryProperty p = pl.getProperty("acl");
        if (p != null) { // If doc contains acls
          String[] acl = p.getValues();
          for (int i = 0; i < acl.length; i++) {
            if (claimant.equals(acl[i])) {
              add(doc);
            }
          }
        } else if (pl.getProperty("google:ispublic") != null) {
          if (pl.getProperty("google:ispublic").getValue().equals(
              "true")) {
            add(doc);
          }
        }
      }
    }
  }
}
