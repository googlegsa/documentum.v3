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

import com.google.enterprise.connector.dctm.JdbcFixture;
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
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.RepositoryException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MockDmSession implements ISession {
  private final ISessionManager sessMgr;

  private final MockJcrRepository mockRep;

  private final MockJcrSession mockSess;

  private final String sessionFileNameSuffix;

  private final Connection jdbcConnection = JdbcFixture.getSharedConnection();

  public MockDmSession(ISessionManager sessMgr, MockJcrRepository mjR,
      MockJcrSession mjS, String dbFileName) {
    this.sessMgr = sessMgr;
    this.mockRep = mjR;
    this.mockSess = mjS;
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

  @Override
  public String getLoginUserName() throws RepositoryException {
    // TODO(jlacey): We are not supporting domains for authentication.
    // This code is similar to getObjectByQualification in that it
    // just returns the first match if there are multiple matches.
    try {
      return executeQuery(
          "select user_name from dm_user where user_login_name = '"
          + mockSess.getUserID() + "'");
    } catch (SQLException e) {
      throw new RepositoryLoginException("Database error", e);
    }
  }

  @Override
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

  @Override
  public String getLoginTicketEx(String username, String scope, int timeout,
      boolean singleUse, String serverName) {
    // See getLoginTicketForUser.
    return username;
  }

  @Override
  public String getDocbaseName() {
    return this.sessionFileNameSuffix;
  }

  @Override
  public String getServerVersion() {
    return "dctmmockwrap";
  }

  @Override
  public ISysObject getObject(IId objectId) throws RepositoryDocumentException {
    MockRepositoryDocument mockRepositoryDocument = mockRep.getRepo()
        .getStore().getDocByID(objectId.toString());
    MockDmObject dctmMockRepositoryDocument = new MockDmObject(
        mockRepositoryDocument);
    return dctmMockRepositoryDocument;
  }

  @Override
  public IType getType(String typeName) throws RepositoryException {
    return new MockDmType(typeName);
  }

  @Override
  public ISessionManager getSessionManager() throws RepositoryException {
    return sessMgr;
  }

  @Override
  public boolean isConnected() {
    return sessMgr != null;
  }

  @Override
  public IPersistentObject getObjectByQualification(String queryString)
      throws RepositoryDocumentException {
    try {
      if (queryString.startsWith("dm_user")) {
        String name = executeQuery("select user_name from " + queryString);
        return (name == null) ? null : new MockDmUser(name);
      } else if (queryString.startsWith("dm_group")) {
        String name = executeQuery("select group_name from " + queryString);
        return (name == null) ? null : new MockDmGroup(name);
      } else {
        return null;
      }
    } catch (SQLException e) {
      throw new RepositoryDocumentException("Database error", e);
    }
  }

  /**
   * Executes a SQL query and returns the string value of the first
   * column in the first row. It is not an error if there are
   * additional rows in the result set.
   */
  private String executeQuery(String query) throws SQLException {
    Statement stmt = jdbcConnection.createStatement();
    try {
      ResultSet rs = stmt.executeQuery(query);
      if (rs.next()) {
        return rs.getString(1);
      } else {
        return null;
      }
    } finally {
      stmt.close();
    }
  }
}
