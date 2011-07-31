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
  private static final Logger LOGGER = Logger.getLogger(DctmAuthenticationManager.class.getName());

  private final IClientX clientX;

  private final String docbase;

  public DctmAuthenticationManager(IClientX clientX, String docbase) {
    this.clientX = clientX;
    this.docbase = docbase;
  }

  public AuthenticationResponse authenticate(
      AuthenticationIdentity authenticationIdentity)
      throws RepositoryLoginException, RepositoryException {
    String username = authenticationIdentity.getUsername();
    String password = authenticationIdentity.getPassword();
    LOGGER.info("authentication process for user " + username);

    ILoginInfo loginInfo = clientX.getLoginInfo();
    loginInfo.setUser(username);
    loginInfo.setPassword(password);
    ISessionManager sessionManagerUser = clientX.getLocalClient().newSessionManager();

    try {
      sessionManagerUser.setIdentity(docbase, loginInfo);
    } catch (RepositoryLoginException e) {
      LOGGER.finer(e.getMessage());
      LOGGER.info("authentication status: false");
      return new AuthenticationResponse(false, "");
    }

    boolean authenticate = sessionManagerUser.authenticate(docbase);
    LOGGER.info("authentication status: " + authenticate);
    return new AuthenticationResponse(authenticate, "");
  }
}
