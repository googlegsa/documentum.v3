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

package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.util.logging.Logger;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmQuery implements IQuery {
  IDfQuery idfQuery;

  DmSession dmSession;

  private static Logger logger = null;

  static {
    logger = Logger.getLogger(DmQuery.class.getName());
  }

  public DmQuery(IDfQuery idfQuery) {
    this.idfQuery = idfQuery;
  }

  public DmQuery() {
    this.idfQuery = new DfQuery();
  }

  public void setDQL(String dqlStatement) {
    idfQuery.setDQL(dqlStatement);
  }

  public ICollection execute(ISessionManager sessionManager, int queryType)
      throws RepositoryException {
    if (!(sessionManager instanceof DmSessionManager)) {
      throw new IllegalArgumentException();
    }

    DmSessionManager dmSessionManager = (DmSessionManager) sessionManager;
    IDfSessionManager idfSessionmanager = dmSessionManager
        .getDfSessionManager();

    logger.info("value of IdfQuery " + idfQuery.getDQL());

    IDfSession idfSession = null;
    IDfCollection dfCollection = null;
    try {
      idfSession = idfSessionmanager.getSession(sessionManager
          .getDocbaseName());
      dfCollection = idfQuery.execute(idfSession, queryType);
    } catch (DfException de) {
      throw new RepositoryException(de);
    }

    return new DmCollection(dfCollection);
  }

  public ICollection execute(ISession session, int queryType)
      throws RepositoryException {
    if (!(session instanceof DmSession)) {
      throw new IllegalArgumentException();
    }

    dmSession = (DmSession) session;
    IDfSession idfSession = dmSession.getDfSession();

    logger.info("value of IdfQuery " + idfQuery.getDQL());

    IDfCollection dfCollection = null;
    try {
      dfCollection = idfQuery.execute(idfSession, queryType);
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
    return new DmCollection(dfCollection);
  }
}
