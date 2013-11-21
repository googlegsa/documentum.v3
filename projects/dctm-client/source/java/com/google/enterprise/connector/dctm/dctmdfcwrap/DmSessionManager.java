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

import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;

import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.DfIdentityException;
import com.documentum.fc.client.DfPrincipalException;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DmSessionManager implements ISessionManager {
  private static Logger logger = Logger.getLogger(DmSessionManager.class
      .getName());

  private final IDfSessionManager dfSessionManager;

  public DmSessionManager(IDfSessionManager DfSessionManager) {
    this.dfSessionManager = DfSessionManager;
  }

  @Override
  public ISession getSession(String docbase) throws RepositoryLoginException,
      RepositoryException {
    IDfSession dfSession = null;
    try {
      dfSession = dfSessionManager.getSession(docbase);
      if (logger.isLoggable(Level.FINER)) {
        IDfLoginInfo idfLoginInfo = dfSessionManager.getIdentity(docbase);
        logger.finer("Session for user: " + idfLoginInfo.getUser()
            + ": " + dfSession + " (id=" + dfSession.getSessionId() + ')');
      }
    } catch (DfIdentityException e) {
      throw new RepositoryLoginException(e);
    } catch (DfAuthenticationException e) {
      throw new RepositoryLoginException(e);
    } catch (DfPrincipalException e) {
      throw new RepositoryLoginException(e);
    } catch (DfServiceException e) {
      throw new RepositoryException(e);
    } catch (DfException e) {
      throw new RepositoryException(e);
    }
    return new DmSession(dfSession);
  }

  @Override
  public void setIdentity(String docbase, ILoginInfo identity)
      throws RepositoryLoginException {
    if (!(identity instanceof DmLoginInfo)) {
      throw new IllegalArgumentException();
    }
    DmLoginInfo dctmLoginInfo = (DmLoginInfo) identity;
    logger.finer("Set identity: " + identity.getUser());
    IDfLoginInfo idfLoginInfo = dctmLoginInfo.getIdfLoginInfo();
    try {
      dfSessionManager.setIdentity(docbase, idfLoginInfo);
    } catch (DfServiceException e) {
      throw new RepositoryLoginException(e);
    }
  }

  @Override
  public ILoginInfo getIdentity(String docbase) {
    IDfLoginInfo idfLoginInfo = dfSessionManager.getIdentity(docbase);
    return new DmLoginInfo(idfLoginInfo);
  }

  @Override
  public ISession newSession(String docbase) throws RepositoryLoginException,
      RepositoryException {
    IDfSession dfSession = null;
    try {
      dfSession = dfSessionManager.newSession(docbase);
    } catch (DfIdentityException e) {
      throw new RepositoryLoginException(e);
    } catch (DfAuthenticationException e) {
      throw new RepositoryLoginException(e);
    } catch (DfPrincipalException e) {
      throw new RepositoryLoginException(e);
    } catch (DfServiceException e) {
      throw new RepositoryException(e);
    } catch (NoClassDefFoundError e) {
      throw new RepositoryException(e);
    }
    return new DmSession(dfSession);
  }

  @Override
  public void release(ISession session) {
    IDfSession dfSession = ((DmSession) session).getDfSession();
    logger.finest("before session released: " + dfSession);
    dfSessionManager.release(dfSession);
    logger.finest("after session released");
  }

  IDfSessionManager getDfSessionManager() {
    return dfSessionManager;
  }

  @Override
  public boolean authenticate(String docbaseName) {
    boolean authent = false;
    try {
      dfSessionManager.authenticate(docbaseName);
      authent = true;
    } catch (DfException e) {
      logger.finer(e.getMessage());
      authent = false;
    }
    return authent;
  }

  @Override
  public void clearIdentity(String docbase) {
    dfSessionManager.clearIdentity(docbase);
  }
}
