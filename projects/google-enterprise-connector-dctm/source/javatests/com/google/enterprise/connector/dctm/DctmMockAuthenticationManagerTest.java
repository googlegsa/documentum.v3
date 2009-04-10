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

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SimpleAuthenticationIdentity;

import junit.framework.TestCase;

public class DctmMockAuthenticationManagerTest extends TestCase {
  public void testAuthenticate() throws RepositoryException {
    Connector connector = new DctmConnector();
    ((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
    ((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
    ((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
    ((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
    ((DctmConnector) connector)
        .setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    ((DctmConnector) connector).setIs_public("true");
    Session sess = (DctmSession) connector.login();

    DctmAuthenticationManager authentManager = (DctmAuthenticationManager) sess
        .getAuthenticationManager();

    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK1,
            DmInitialize.DM_PWD_OK1)).isValid());
    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2,
            DmInitialize.DM_PWD_KO)).isValid());
    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2,
            DmInitialize.DM_PWD_OK2)).isValid());
    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2,
            DmInitialize.DM_PWD_KO)).isValid());
    assertFalse(authentManager
        .authenticate(
            new SimpleAuthenticationIdentity(
                DmInitialize.DM_LOGIN_OK2, null)).isValid());
    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(null, DmInitialize.DM_PWD_OK1))
        .isValid());
    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(null, null)).isValid());

    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK3,
            DmInitialize.DM_PWD_OK3)).isValid());
    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK1,
            DmInitialize.DM_PWD_OK1)).isValid());
    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK5,
            DmInitialize.DM_PWD_OK5)).isValid());
  }
}
