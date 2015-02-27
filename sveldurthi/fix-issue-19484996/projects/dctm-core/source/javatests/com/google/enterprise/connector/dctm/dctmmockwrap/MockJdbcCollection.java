// Copyright 2014 Google Inc.
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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryException;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** An ICollection implementation on top of JDBC. */
public class MockJdbcCollection implements ICollection {
  private final Statement statement;

  private final ResultSet resultSet;

  protected MockJdbcCollection(Statement statement, ResultSet resultSet)
      throws RepositoryException {
    this.statement = statement;
    this.resultSet = resultSet;
  }

  @Override
  public boolean next() throws RepositoryException {
    try {
      return resultSet.next();
    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public String getString(String colName) throws RepositoryException {
    try {
      return resultSet.getString(colName);
    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public IValue getValue(String attrName) throws RepositoryException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() throws RepositoryException {
    try {
      try {
        resultSet.close();
      } finally {
        statement.close();
      }
    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public int getState() {
    // We could just keep track of the state instead.
    try {
      return (resultSet.isClosed())
          ? ICollection.DF_CLOSED_STATE : ICollection.DF_READY_STATE;
    } catch (SQLException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public ISession getSession() {
    MockDmSessionManager mockDm = new MockDmSessionManager();
    ISession session = null;
    try {
      session = mockDm.getSession(mockDm.getDocbaseName());
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    return session;
  }

  @Override
  public boolean hasNext() throws RepositoryException {
    try {
      return !(resultSet.isLast() || resultSet.isAfterLast());
    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public ITime getTime(String colName) throws RepositoryException {
    Date val;
    try {
      val = resultSet.getDate(colName);
    } catch (SQLException e) {
      throw new RepositoryException(e);
    }
    return new MockDmTime(val);
  }

  @Override
  public String getAllRepeatingStrings(String colName, String separator)
      throws RepositoryException {
    throw new UnsupportedOperationException();
  }
}
