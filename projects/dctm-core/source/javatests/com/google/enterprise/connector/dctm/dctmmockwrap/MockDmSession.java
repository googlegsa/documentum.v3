// Copyright 2006 Google Inc.
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

import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.IPersistentObject;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.mock.jcr.MockJcrRepository;
import com.google.enterprise.connector.mock.jcr.MockJcrSession;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;

public class MockDmSession implements ISession {
  private final ISessionManager sessMgr;

  private final MockJcrRepository mockRep;

  private final String sessionFileNameSuffix;

  public MockDmSession(ISessionManager sessMgr, MockJcrRepository mjR,
      MockJcrSession mjS, String dbFileName) {
    this.sessMgr = sessMgr;
    this.mockRep = mjR;
    this.sessionFileNameSuffix = dbFileName;
  }

  /**
   * Constructs a session that cannot do much. It must, however,
   * support {@link #getType}.
   */
  public MockDmSession() {
    this(null, null, null, null);
  }

  MockRepositoryDocumentStore getStore() {
    return mockRep.getRepo().getStore();
  }

  public String getLoginTicketForUser(String username) {
    // this assumes that Mock authenticated the session by
    // checking username==paswword
    // /return mockJcrSession.getUserID();// The only security here is
    return username;
    // inherent to the fact that if
    // authentication failed,
    // Session==null the returning
    // getUserId instead of directly
    // retuning username would throw a
    // nullPointerException
  }

  public String getLoginTicketEx(String username, String scope, int timeout,
      boolean singleUse, String serverName) {
    // See getLoginTicketForUser.
    return username;
  }

  public String getDocbaseName() {
    return this.sessionFileNameSuffix;
  }

  public String getServerVersion() {
    return "dctmmockwrap";
  }

  public ISysObject getObject(IId objectId) throws RepositoryDocumentException {
    MockRepositoryDocument mockRepositoryDocument = mockRep.getRepo()
        .getStore().getDocByID(objectId.toString());
    MockDmObject dctmMockRepositoryDocument = new MockDmObject(
        mockRepositoryDocument);
    return dctmMockRepositoryDocument;
  }

  public IType getType(String typeName) throws RepositoryException {
    return new MockDmType(typeName);
  }

  public ISessionManager getSessionManager() throws RepositoryException {
    return sessMgr;
  }

  public boolean isConnected() {
    return sessMgr != null;
  }

  @Override
  public IPersistentObject getObjectByQualification(String queryString)
      throws RepositoryDocumentException {
    if (queryString.startsWith("dm_user")) {
      // get user name from the query string of form 
      // "dm_user where user_name = 'userName'", so add 3 to index of '='
      String username = queryString.substring(queryString.indexOf('=') + 3,
          queryString.length() - 1);
      if (username.equals(DmInitialize.DM_LOGIN_KO)) {
        return null;
      } else {
        return new MockDmUser(username);
      }
    } else {
      return null;
    }
  }
}
