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

package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthorizationManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

import java.util.logging.Logger;

public class DctmSession implements Session {
  private static Logger logger = Logger.getLogger(DctmSession.class.getName());

  private final DctmConnector connector;

  private final IClientX clientX;

  private final ISessionManager sessionManager;

  public DctmSession(DctmConnector connector) throws RepositoryException {
    this.connector = connector;
    this.clientX = connector.getClientX();
    IClient client = clientX.getLocalClient();
    this.sessionManager = client.newSessionManager();

    ILoginInfo dctmLoginInfo = clientX.getLoginInfo();
    dctmLoginInfo.setUser(connector.getLogin());
    dctmLoginInfo.setPassword(connector.getPassword());
    String docbase = connector.getDocbase();
    sessionManager.setIdentity(connector.getDocbase(), dctmLoginInfo);
    logger.fine("Session Manager set the identity for " + connector.getLogin());

    ISession session = sessionManager.newSession(docbase);
    logger.info("DFC " + this.clientX.getDFCVersion()
        + " connected to Content Server " + session.getServerVersion());
    sessionManager.release(session);
    logger.info("Tested a new session for the docbase " + docbase);
  }

  @Override
  public DctmTraversalManager getTraversalManager() throws RepositoryException {
    return new DctmTraversalManager(connector, sessionManager);
  }

  /**
   * Gets an AuthenticationManager. It is permissible to return null. A null
   * return means that this implementation does not support an Authentication
   * Manager. This may be for one of these reasons:
   * <ul>
   * <li> Authentication is not needed for this data source
   * <li> Authentication is handled through another GSA-supported mechanism,
   * such as LDAP
   * </ul>
   *
   * @return a AuthenticationManager - may be null
   * @throws RepositoryException
   */
  @Override
  public DctmAuthenticationManager getAuthenticationManager() {
    return new DctmAuthenticationManager(connector, clientX,
        connector.getDocbase());
  }

  /**
   * Gets an AuthorizationManager. It is permissible to return null. A null
   * return means that this implementation does not support an Authorization
   * Manager. This may be for one of these reasons:
   * <ul>
   * <li> Authorization is not needed for this data source - all documents are
   * public
   * <li> Authorization is handled through another GSA-supported mechanism,
   * such as NTLM or Basic Auth
   * </ul>
   *
   * @return a AuthorizationManager - may be null
   * @throws RepositoryException
   */
  @Override
  public DctmAuthorizationManager getAuthorizationManager() {
    return new DctmAuthorizationManager(clientX, sessionManager,
        connector.getDocbase());
  }
}
