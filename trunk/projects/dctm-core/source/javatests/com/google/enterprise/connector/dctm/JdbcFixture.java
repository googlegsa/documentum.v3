// Copyright 2014 Google Inc. All Rights Reserved.
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

import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Manages an in-memory H2 database modeling the Documentum database. */
public class JdbcFixture {
  private static final String CREATE_TABLE_GROUP = "create table dm_group "
      + "(group_name varchar, i_all_users_names varchar)";

  private static final String CREATE_TABLE_USER = "create table dm_user "
      + "(user_name varchar primary key, user_login_name varchar, "
      + "user_source varchar, user_ldap_dn varchar)";

  /**
   * Gets a JDBC connection to a named in-memory database. The default
   * escape character is disabled, to match the DQL behavior (and
   * standard SQL).
   */
  public static Connection getSharedConnection() {
    try {
      JdbcDataSource ds = new JdbcDataSource();
      ds.setURL("jdbc:h2:mem:test;DEFAULT_ESCAPE=");
      ds.setUser("sa");
      ds.setPassword("");
      return ds.getConnection();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /** The database connection. */
  private Connection jdbcConnection;

  /** Initializes a database connection. */
  protected void setUp() throws SQLException {
    // Get a named in-memory database for sharing across connections.
    // We delete all objects from the database for each test in tearDown.
    jdbcConnection = getSharedConnection();

    executeUpdate(
        CREATE_TABLE_GROUP,
        CREATE_TABLE_USER);
  }

  protected void tearDown() throws SQLException {
    try {
      executeUpdate("drop all objects");
    } finally {
      Connection tmp = jdbcConnection;
      jdbcConnection = null; // In case this object is reused.
      tmp.close();
    }
  }

  public Connection getConnection() {
    return jdbcConnection;
  }

  public void executeUpdate(String... sqls) throws SQLException {
    Statement stmt = jdbcConnection.createStatement();
    try {
      for (String sql : sqls) {
        stmt.executeUpdate(sql);
      }
    } finally {
      stmt.close();
    }
  }
}
