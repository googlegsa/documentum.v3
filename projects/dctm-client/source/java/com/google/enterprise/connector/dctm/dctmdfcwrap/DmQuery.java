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

import java.util.logging.Level;
import java.util.logging.Logger;

public class DmQuery implements IQuery {
  private static final Logger logger =
      Logger.getLogger(DmQuery.class.getName());

  private final IDfQuery idfQuery;

  public DmQuery(IDfQuery idfQuery) {
    this.idfQuery = idfQuery;
  }

  public DmQuery() {
    this.idfQuery = new DfQuery();
  }

  public void setDQL(String dqlStatement) {
    idfQuery.setDQL(dqlStatement);
  }

  public ICollection execute(ISession session, int queryType)
      throws RepositoryException {
    if (!(session instanceof DmSession)) {
      throw new IllegalArgumentException();
    }

    DmSession dmSession = (DmSession) session;
    IDfSession idfSession = dmSession.getDfSession();

    if (logger.isLoggable(Level.FINEST))
      logger.finest("value of IdfQuery " + idfQuery.getDQL());

    IDfCollection dfCollection = null;
    try {
      dfCollection = idfQuery.execute(idfSession, queryType);
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
    return new DmCollection(dfCollection);
  }
}
