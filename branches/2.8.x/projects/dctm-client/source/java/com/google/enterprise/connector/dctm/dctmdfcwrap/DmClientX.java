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

package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmClientX implements IClientX {
  private final IDfClientX idfClientX;

  public DmClientX() {
    this.idfClientX = new DfClientX();
  }

  public String getDFCVersion() {
    return idfClientX.getDFCVersion();
  }

  public IId getId(String id) {
    return new DmId(idfClientX.getId(id));
  }

  public IClient getLocalClient() throws RepositoryException {
    try {
      return new DmClient(idfClientX.getLocalClient());
    } catch (DfException e) {
      throw new RepositoryException(e);
    } catch (Error e) {
      // DFC 5.3 sometimes throws exceptions wrapped in an Error here.
      Throwable cause = e.getCause();
      if (cause != null)
        throw new RepositoryException(cause);
      else
        throw e;
    }
  }

  public ILoginInfo getLoginInfo() {
    return new DmLoginInfo(idfClientX.getLoginInfo());
  }

  public IQuery getQuery() {
    return new DmQuery(new DfQuery());
  }
}
