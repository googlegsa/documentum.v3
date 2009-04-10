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

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

public class DctmConnector implements Connector {
  private String login;

  private String password;

  private String docbase;

  private String clientX;

  private String webtop_display_url;

  private String authentication_type;

  private String where_clause;

  private String is_public;

  private String included_meta;

  private String included_object_type;

  private String root_object_type;

  private String advanced_configuration;

  private String action_update;

  private static Logger logger = null;

  static {
    logger = Logger.getLogger(DctmConnector.class.getName());
  }

  /**
   * Setters for the data retrieved from Spring
   *
   */
  public void setLogin(String login) {
    this.login = login;
    logger.log(Level.INFO, "login set to " +login);
  }

  public void setDocbase(String docbase) {
    this.docbase = docbase;
    logger.log(Level.INFO, "docbase set to " +docbase);
  }

  public void setWebtop_display_url(String wsu) {
    this.webtop_display_url = wsu;
    logger.log(Level.INFO, "webtopdisplayurl set to " +wsu);
  }

  public void setClientX(String clientX) {
    this.clientX = clientX;
    logger.log(Level.INFO, "clientX set to " +clientX);
  }

  public DctmConnector() {
  }

  public DctmConnector(String googleConnectorWorkDir) {
  }

  public Session login() throws RepositoryException {
    logger.log(Level.INFO, "login in the docbase " + docbase + " and user "
        + login + " " + clientX + " " + docbase + " "
        + webtop_display_url + " " + where_clause + " "
        + is_public.equals("on"));

    Session sess = null;
    sess = new DctmSession(clientX, login, password, docbase,
        webtop_display_url, where_clause, is_public.equals("on"),
        included_meta, root_object_type,
        included_object_type);

    return sess;
  }

  public void setAuthentication_type(String authenticationType) {
    this.authentication_type = authenticationType;
    logger.log(Level.INFO, "authenticationType set to " +authenticationType);
  }

  public void setWhere_clause(String additionalWhereClause) {
    this.where_clause = additionalWhereClause;
    logger.log(Level.INFO, "where_clause set to " +additionalWhereClause);
  }

  public String getIs_public() {
    return is_public;
  }

  public void setIs_public(String is_public) {
    this.is_public = is_public;
    logger.log(Level.INFO, "is_public set to " +is_public);
  }

  public String getAuthentication_type() {
    return authentication_type;
  }

  public String getIncluded_meta() {
    return included_meta;
  }

  public String getRoot_object_type() {
    return root_object_type;
  }

  public String getIncluded_object_type() {
    return included_object_type;
  }

  public String getAdvanced_configuration() {
    return advanced_configuration;
  }

  public String getAction_update() {
    return action_update;
  }

  public void setIncluded_meta(String included_meta) {
    this.included_meta = included_meta;
    logger.log(Level.INFO, "included_meta set to " +included_meta);
  }

  public void setAdvanced_configuration(String advanced_configuration) {
    this.advanced_configuration = advanced_configuration;
    logger.log(Level.INFO, "advanced_configuration set to " +advanced_configuration);
  }

  public void setPassword(String password) {
    this.password = password;
    logger.log(Level.INFO, "password set to [...]");
  }

  public void setIncluded_object_type(String included_object_type) {
    this.included_object_type = included_object_type + ",dm_folder";
    logger.log(Level.INFO, "included_object_type set to " +included_object_type);
  }

  public void setRoot_object_type(String root_object_type) {
    this.root_object_type = root_object_type;
    logger.log(Level.INFO, "root_object_type set to " +root_object_type);
  }

  public void setAction_update(String action_update) {
    this.action_update = action_update;
    logger.log(Level.INFO, "action_update set to " +action_update);
  }
}
