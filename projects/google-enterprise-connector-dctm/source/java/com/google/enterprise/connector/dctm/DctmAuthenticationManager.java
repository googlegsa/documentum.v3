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

package com.google.enterprise.connector.dctm;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.AuthenticationIdentity;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthenticationResponse;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.RepositoryException;

public class DctmAuthenticationManager implements AuthenticationManager {
  ISessionManager sessionManager;

  IClientX clientX;

  ILoginInfo loginInfo;

  private static Logger logger = null;

  static {
    logger = Logger.getLogger(DctmAuthenticationManager.class.getName());
  }

  public DctmAuthenticationManager(IClientX clientX) {
    setClientX(clientX);
    sessionManager = clientX.getSessionManager();
  }

  public AuthenticationResponse authenticate(
      AuthenticationIdentity authenticationIdentity)
      throws RepositoryLoginException, RepositoryException {
    String username = authenticationIdentity.getUsername();
    String password = authenticationIdentity.getPassword();

    logger.info("authentication process for user " + username);

    setLoginInfo(username, password);
    sessionManager.clearIdentity(sessionManager.getDocbaseName());
    try {
      sessionManager.setIdentity(sessionManager.getDocbaseName(),
          loginInfo);
    } catch (RepositoryLoginException e) {
      logger.warning("authentication failed for user  " + username
          + "\ncause:" + e.getMessage());

      return new AuthenticationResponse(false, "");
    }
    boolean authenticate = false;

    authenticate = sessionManager.authenticate(sessionManager
        .getDocbaseName());

    logger.log(Level.INFO, "authentication status: " + authenticate);

    return new AuthenticationResponse(authenticate, "");
  }

  public void setLoginInfo(String username, String password) {
    loginInfo = clientX.getLoginInfo();
    loginInfo.setUser(username);
    loginInfo.setPassword(password);
  }

  public void setClientX(IClientX clientX) {
    this.clientX = clientX;
  }
}
