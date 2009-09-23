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

import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IDocbaseMap;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmClient implements IClient {
  IDfClient idfClient;

  IDfClientX idfClientX;

  DmSessionManager dmSessionManager = null;

  public DmClient(IDfClient idfClient) {
    this.idfClient = idfClient;
  }

  public IQuery getQuery() {
    return new DmQuery(new DfQuery());
  }

  public ISessionManager getSessionManager() {
    return dmSessionManager;
  }

  public void setSessionManager(ISessionManager sessionManager) {
    dmSessionManager = (DmSessionManager) sessionManager;
  }

  public ISessionManager newSessionManager() {
    IDfSessionManager newSessionManager = idfClient.newSessionManager();
    DmSessionManager dctmSessionManager = new DmSessionManager(
        newSessionManager);
    return dctmSessionManager;
  }

  public IDocbaseMap getDocbaseMap() throws RepositoryException {
    try {
      return (IDocbaseMap) new DmDocbaseMap(this.idfClient
          .getDocbaseMap());
    } catch (DfException e) {
      throw new RepositoryException(e);
    }
  }
}
