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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

public class DctmConnector implements Connector {
  private static final Logger logger =
      Logger.getLogger(DctmConnector.class.getName());

  private String login;

  private String password;

  private String docbase;

  private String clientX;

  private String webtopDisplayUrl;

  private String authenticationType;

  private String whereClause;

  private String isPublic;

  private String includedMeta;

  private String includedObjectType;

  private String rootObjectType;

  /*
   * Setters for the data retrieved from Spring
   */
  public void setLogin(String login) {
    this.login = login;
    logger.log(Level.CONFIG, "login set to " + login);
  }

  public void setDocbase(String docbase) {
    this.docbase = docbase;
    logger.log(Level.CONFIG, "docbase set to " + docbase);
  }

  public void setWebtop_display_url(String wsu) {
    this.webtopDisplayUrl = wsu;
    logger.log(Level.CONFIG, "webtop_display_url set to " + wsu);
  }

  public void setClientX(String clientX) {
    this.clientX = clientX;
    logger.log(Level.CONFIG, "clientX set to " +clientX);
  }

  public DctmConnector() {
  }

  public DctmConnector(String googleConnectorWorkDir) {
  }

  public Session login() throws RepositoryException {
    logger.log(Level.CONFIG, "login in the docbase " + docbase + " and user "
        + login + " " + clientX + " " + docbase + " "
        + webtopDisplayUrl + " " + whereClause + " "
        + isPublic.equals("on"));

    Session sess = null;
    sess = new DctmSession(clientX, login, password, docbase,
        webtopDisplayUrl, whereClause, isPublic.equals("on"),
        includedMeta, rootObjectType,
        includedObjectType);

    return sess;
  }

  public void setAuthentication_type(String authenticationType) {
    this.authenticationType = authenticationType;
    logger.log(Level.CONFIG, "authenticationType set to " + authenticationType);
  }

  public void setWhere_clause(String additionalWhereClause) {
    this.whereClause = DqlUtils.stripLeadingAnd(additionalWhereClause);
    if (logger.isLoggable(Level.FINE)
        && !whereClause.equals(additionalWhereClause)) {
      logger.log(Level.FINE, "where_clause was " + additionalWhereClause);
    }
    logger.log(Level.CONFIG, "where_clause set to " + whereClause);
  }

  public String getIs_public() {
    return isPublic;
  }

  public void setIs_public(String isPublic) {
    this.isPublic = isPublic;
    logger.log(Level.CONFIG, "is_public set to " + isPublic);
  }

  public String getAuthentication_type() {
    return authenticationType;
  }

  public String getIncluded_meta() {
    return includedMeta;
  }

  public String getRoot_object_type() {
    return rootObjectType;
  }

  public String getIncluded_object_type() {
    return includedObjectType;
  }

  public void setIncluded_meta(String includedMeta) {
    this.includedMeta = includedMeta;
    logger.log(Level.CONFIG, "included_meta set to " + includedMeta);
  }

  public void setPassword(String password) {
    this.password = password;
    logger.log(Level.CONFIG, "password set to [...]");
  }

  public void setIncluded_object_type(String includedObjectType) {
    this.includedObjectType = includedObjectType;
    logger.log(Level.CONFIG, "included_object_type set to "
        + includedObjectType);
  }

  public void setRoot_object_type(String rootObjectType) {
    this.rootObjectType = rootObjectType;
    logger.log(Level.CONFIG, "root_object_type set to " + rootObjectType);
  }
}
