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

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmSession implements ISession {
  IDfSession idfSession;

  private static Logger logger = Logger.getLogger(DmSession.class
      .getName());

  public DmSession(IDfSession dfSession) {
    this.idfSession = dfSession;
  }

  public ISysObject getObject(IId objectId) throws RepositoryDocumentException {
    if (!(objectId instanceof DmId)) {
      throw new IllegalArgumentException();
    }
    DmId dctmId = (DmId) objectId;
    IDfId idfId = dctmId.getidfId();

    IDfSysObject idfSysObject = null;
    try {
      idfSysObject = (IDfSysObject) idfSession.getObject(idfId);
    } catch (DfException de) {
      RepositoryDocumentException re = new RepositoryDocumentException(de);
      throw re;
    }
    return new DmSysObject(idfSysObject);
  }

  public IDfSession getDfSession() {
    return idfSession;
  }

  public void setDfSession(IDfSession dfSession) {
    idfSession = dfSession;
  }

  public String getLoginTicketForUser(String username)
      throws RepositoryException {
    String ticket = null;
    try {
      ticket = this.idfSession.getLoginTicketForUser(username);
    } catch (DfException de) {
      RepositoryException re = new RepositoryException(de);
      throw re;
    }
    return ticket;
  }

  public DmDocument newObject() throws RepositoryException {
    IDfDocument document = null;
    try {
      document = (IDfDocument) this.idfSession.newObject("dm_document");
    } catch (DfException de) {
      RepositoryException re = new RepositoryException(de);
      throw re;
    }
    return new DmDocument(document);
  }

  public IType getType(String typeName) throws RepositoryException {
    IDfType idfType = null;
    try {
      idfType = (IDfType) idfSession.getType(typeName);
    } catch (DfException de) {
      RepositoryException re = new RepositoryException(de);
      throw re;
    }
    return new DmType(idfType);
  }

  public ISessionManager getSessionManager() {
    IDfSessionManager idfSessmag = null;
    idfSessmag = (IDfSessionManager) this.idfSession.getSessionManager();
    return new DmSessionManager(idfSessmag);
  }
}
