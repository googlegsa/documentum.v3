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

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmClientX implements IClientX {
  private IDfClientX idfClientX = null;

  private DmClient dmClient = null;

  private DmSessionManager dmSessionManager = null;

  public DmClientX() {
    this.idfClientX = new DfClientX();
  }

  public IId getId(String id) {
    IDfId dfid = idfClientX.getId(id);
    return new DmId(dfid);
  }

  public IClient getLocalClient() throws RepositoryException {
    IDfClient localClient;
    try {
      localClient = idfClientX.getLocalClient();
    } catch (DfException e) {
      throw new RepositoryException(e);
    }
    DmClient dctmClient = new DmClient(localClient);
    return dctmClient;
  }

  public ILoginInfo getLoginInfo() {
    return new DmLoginInfo(idfClientX.getLoginInfo());
  }

  public void setClient(IClient client) {
    this.dmClient = (DmClient) client;
  }

  public IClient getClient() {
    return dmClient;
  }

  public void setSessionManager(ISessionManager sessionMag) {
    this.dmSessionManager = (DmSessionManager) sessionMag;
  }

  public ISessionManager getSessionManager() {
    return dmSessionManager;
  }

  public IQuery getQuery() {
    return new DmQuery(new DfQuery());
  }
}
