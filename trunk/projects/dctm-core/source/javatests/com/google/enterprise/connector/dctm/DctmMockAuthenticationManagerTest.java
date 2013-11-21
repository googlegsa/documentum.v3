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

import com.google.common.collect.ImmutableSet;
import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmSessionManager;
import com.google.enterprise.connector.spi.AuthenticationResponse;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.Principal;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SimpleAuthenticationIdentity;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class DctmMockAuthenticationManagerTest extends TestCase {
  DctmAuthenticationManager authentManager;

  @Override
  protected void setUp() throws RepositoryException {
    Connector connector = new DctmConnector();
    ((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
    ((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
    ((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
    ((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
    ((DctmConnector) connector).setGoogleGlobalNamespace("global");
    ((DctmConnector) connector).setGoogleLocalNamespace("local");
    ((DctmConnector) connector)
        .setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
    ((DctmConnector) connector).setIs_public("true");
    Session sess = (DctmSession) connector.login();

    authentManager = (DctmAuthenticationManager) sess
        .getAuthenticationManager();
  }

  protected void tearDown() {
    MockDmSessionManager.tearDown();
  }

  public void testAuthenticate() throws RepositoryException {
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
    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_KO,
            DmInitialize.DM_PWD_KO)).isValid());
    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2, null))
        .isValid());
    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_KO, null))
        .isValid());
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

    assertFalse(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2_DOMAIN,
            DmInitialize.DM_PWD_KO)).isValid());
    assertTrue(authentManager.authenticate(
        new SimpleAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2_DNS_DOMAIN,
            DmInitialize.DM_PWD_OK2_DNS_DOMAIN)).isValid());
  }

  public void testGroupLookup() throws RepositoryLoginException,
      RepositoryException {
    AuthenticationResponse result =
        authentManager.authenticate(new SimpleAuthenticationIdentity(
            DmInitialize.DM_LOGIN_OK1, DmInitialize.DM_PWD_OK1));
    assertTrue(result.isValid());
    Collection<?> groups = (Collection<?>) result.getGroups();
    Set<String> expected = ImmutableSet.of("grp1", "grp2", "grp3", "dm_world");
    Set<String> setGroups = ImmutableSet.copyOf(toStringList(groups));
    assertEquals(expected, setGroups);

    AuthenticationResponse result2 =
        authentManager.authenticate(new SimpleAuthenticationIdentity(
            DmInitialize.DM_LOGIN_OK2, DmInitialize.DM_PWD_OK2));
    assertTrue(result2.isValid());
    Collection<?> groups2 =
        (Collection<?>) result2.getGroups();
    List<String> users2 = Arrays.asList("dm_world");
    assertEquals(users2, toStringList(groups2));
  }

  private List<String> toStringList(Collection<?> groups) {
    if (groups == null) {
      return null;
    }
    List<String> names = new ArrayList<String>(groups.size());
    for (Object obj : groups) {
      String name = (obj instanceof String) ? (String) obj : ((Principal)
          obj).getName();
      names.add(name);
    }
    return names;
  }
}
