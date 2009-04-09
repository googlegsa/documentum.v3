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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.DfIdentityException;
import com.documentum.fc.client.DfPrincipalException;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmSessionManager implements ISessionManager {
  IDfSessionManager dfSessionManager;

  IDfSession DfSessionDel;
  IDfSession DfSessionAdd;
  IDfSession DfSessionAuto;
  IDfSession DfSessionConfig;

  private String docbaseName;

  private String serverUrl;

  private static Logger logger = Logger.getLogger(DmSessionManager.class
      .getName());

  public DmSessionManager(IDfSessionManager DfSessionManager) {
    this.dfSessionManager = DfSessionManager;
  }

  public void setSessionDel(ISession sess) {
    this.DfSessionDel = ((DmSession) sess).getDfSession();
    logger.finest("setSessionDel");
  }

  public void setSessionAdd(ISession sess) {
    this.DfSessionAdd = ((DmSession) sess).getDfSession();
    logger.finest("setSessionAdd");
  }

  public void setSessionAuto(ISession sess) {
    this.DfSessionAuto = ((DmSession) sess).getDfSession();
    logger.finest("setSessionAuto");
  }

  public void setSessionConfig(ISession sess) {
    this.DfSessionConfig = ((DmSession) sess).getDfSession();
    logger.finest("setSessionSessionConfig");
  }

  public void releaseSessionAdd() {
    logger.finest("before session released");
    this.dfSessionManager.release(this.DfSessionAdd);
    logger.finest("after session released");
  }

  public void releaseSessionDel() {
    logger.finest("before session released");
    this.dfSessionManager.release(this.DfSessionDel);
    logger.finest("after session released");
  }

  public void releaseSessionAuto() {
    logger.finest("before session released");
    this.dfSessionManager.release(this.DfSessionAuto);
    logger.finest("after session released");
  }

  public void releaseSessionConfig() {
    logger.finest("before session released");
    this.dfSessionManager.release(this.DfSessionConfig);
    logger.finest("after session released");
  }

  public ISession getSession(String docbase) throws RepositoryLoginException,
      RepositoryException {
    IDfSession DfSession = null;
    try {
      DfSession = dfSessionManager.getSession(docbase);
      if (logger.isLoggable(Level.FINER)) {
        IDfLoginInfo idfLoginInfo = dfSessionManager.getIdentity(docbase);
        logger.finer("Session for user: " + idfLoginInfo.getUser());
      }
    } catch (DfIdentityException iE) {
      RepositoryLoginException le = new RepositoryLoginException(iE);
      throw le;
    } catch (DfAuthenticationException iE) {
      RepositoryLoginException le = new RepositoryLoginException(iE);
      throw le;
    } catch (DfPrincipalException iE) {
      RepositoryLoginException le = new RepositoryLoginException(iE);
      throw le;
    } catch (DfServiceException iE) {
      RepositoryException re = new RepositoryException(iE);
      throw re;
    }
    return new DmSession(DfSession);
  }

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
    } catch (DfServiceException iE) {
      RepositoryLoginException le = new RepositoryLoginException(iE);
      throw le;
    }
  }

  public ILoginInfo getIdentity(String docbase) {
    IDfLoginInfo idfLoginInfo = dfSessionManager.getIdentity(docbase);
    return new DmLoginInfo(idfLoginInfo);
  }

  public ISession newSession(String docbase) throws RepositoryLoginException,
      RepositoryException {
    IDfSession idfSession = null;
    try {
      idfSession = dfSessionManager.newSession(docbase);
    } catch (DfIdentityException iE) {
      throw new RepositoryLoginException(iE);
    } catch (DfAuthenticationException iE) {
      throw new RepositoryLoginException(iE);
    } catch (DfPrincipalException iE) {
      throw new RepositoryLoginException(iE);
    } catch (DfServiceException iE) {
      throw new RepositoryException(iE);
    } catch (NoClassDefFoundError iE) {
      throw new RepositoryException(iE);
    }
    return new DmSession(idfSession);
  }

  public void release(ISession session) {
    logger.finest("before session released");
    this.dfSessionManager.release(((DmSession) session).getDfSession());
    logger.finest("after session released");
  }

  public IDfSessionManager getDfSessionManager() {
    return dfSessionManager;
  }

  public void setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
  }

  public String getDocbaseName() {
    return docbaseName;
  }

  public void setDocbaseName(String docbaseName) {
    this.docbaseName = docbaseName;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public boolean authenticate(String docbaseName) {
    boolean authent = false;
    try {
      this.dfSessionManager.authenticate(docbaseName);
      authent = true;
    } catch (DfException e) {
      logger.finer(e.getMessage());
      authent = false;
    }
    return authent;
  }

  public void clearIdentity(String docbase) {
    this.dfSessionManager.clearIdentity(docbase);
  }

  public IDfSessionManager getSessionManager() {
    return this.dfSessionManager;
  }
}
