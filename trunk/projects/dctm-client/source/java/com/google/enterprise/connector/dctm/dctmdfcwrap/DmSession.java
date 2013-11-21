// Copyright 2006 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.IPersistentObject;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfGroup;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DmSession implements ISession {
  private static final Logger logger =
      Logger.getLogger(DmSession.class.getName());

  private final IDfSession idfSession;

  public DmSession(IDfSession dfSession) {
    this.idfSession = dfSession;
  }

  @Override
  public String getDocbaseName() throws RepositoryException {
    try {
      return idfSession.getDocbaseName();
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
  }

  @Override
  public String getServerVersion() throws RepositoryException {
    try {
      return idfSession.getServerVersion();
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
  }

  @Override
  public IPersistentObject getObject(IId objectId)
      throws RepositoryDocumentException {
    if (!(objectId instanceof DmId)) {
      throw new IllegalArgumentException();
    }
    DmId dctmId = (DmId) objectId;
    IDfId idfId = dctmId.getidfId();

    IDfPersistentObject idfPersistentObject;
    try {
      idfPersistentObject = idfSession.getObject(idfId);
      if (idfPersistentObject instanceof IDfSysObject) {
        return new DmSysObject((IDfSysObject) idfPersistentObject);
      } else if (idfPersistentObject instanceof IDfACL) {
        return new DmAcl((IDfACL) idfPersistentObject);
      } else if (idfPersistentObject instanceof IDfUser) {
        return new DmUser((IDfUser) idfPersistentObject);
      } else if (idfPersistentObject instanceof IDfGroup) {
        return new DmGroup((IDfGroup) idfPersistentObject);
      } else {
        return new DmPersistentObject(idfPersistentObject);
      }
    } catch (DfException de) {
      throw new RepositoryDocumentException(de);
    }
  }

  @Override
  public IPersistentObject getObjectByQualification(String qualification)
      throws RepositoryDocumentException {
    IDfPersistentObject idfPersistentObject;
    try {
      idfPersistentObject = idfSession.getObjectByQualification(qualification);
      if (idfPersistentObject == null) {
        return null;
      } else if (idfPersistentObject instanceof IDfSysObject) {
        return new DmSysObject((IDfSysObject) idfPersistentObject);
      } else if (idfPersistentObject instanceof IDfACL) {
        return new DmAcl((IDfACL) idfPersistentObject);
      } else if (idfPersistentObject instanceof IDfUser) {
        return new DmUser((IDfUser) idfPersistentObject);
      } else if (idfPersistentObject instanceof IDfGroup) {
        return new DmGroup((IDfGroup) idfPersistentObject);
      } else {
        return new DmPersistentObject(idfPersistentObject);
      }
    } catch (DfException de) {
      throw new RepositoryDocumentException(de);
    }
  }

  IDfSession getDfSession() {
    return idfSession;
  }

  @Override
  public String getLoginTicketForUser(String username)
      throws RepositoryException {
    String ticket;
    try {
      ticket = idfSession.getLoginTicketForUser(username);
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
    return ticket;
  }

  @Override
  public String getLoginTicketEx(String username, String scope, int timeout,
      boolean singleUse, String serverName) throws RepositoryException {
    try {
      String ticket = idfSession.getLoginTicketEx(username, scope, timeout,
          singleUse, serverName);

      // TODO: This call should be moved to DctmAuthorizationManager,
      // but the client and session aren't wired together. If we
      // refactor the sessions or create a facade pattern DFC
      // interface, then we should include this in the refactoring.
      if (logger.isLoggable(Level.FINEST)) {
        try {
          logger.finest("Ticket diagnostics: "
              + idfSession.getClient().getLoginTicketDiagnostics(ticket));
        } catch (DfException de) {
          // Do not log the ticket, which is a de facto password.
          logger.log(Level.FINEST, "Ticket diagnostics failed", de);
        }
      }

      return ticket;
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
  }

  DmDocument newObject() throws RepositoryException {
    IDfDocument document;
    try {
      document = (IDfDocument) idfSession.newObject("dm_document");
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
    return new DmDocument(document);
  }

  @Override
  public IType getType(String typeName) throws RepositoryException {
    IDfType idfType;
    try {
      idfType = idfSession.getType(typeName);
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
    return new DmType(idfType);
  }

  @Override
  public ISessionManager getSessionManager() {
    return new DmSessionManager(idfSession.getSessionManager());
  }

  @Override
  public boolean isConnected() {
    try {
      return idfSession.isConnected();
    } catch (Throwable t) {
      return false;
    }
  }
}
