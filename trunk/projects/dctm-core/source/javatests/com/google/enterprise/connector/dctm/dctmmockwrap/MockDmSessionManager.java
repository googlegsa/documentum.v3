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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.mock.MockRepository;
import com.google.enterprise.connector.mock.MockRepositoryEventList;
import com.google.enterprise.connector.mock.jcr.MockJcrRepository;
import com.google.enterprise.connector.mock.jcr.MockJcrSession;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.SimpleCredentials;

/**
 * Simulates the session pool.
 * Does not manage multiple sessions for the same docbase (for the moment)
 */
public class MockDmSessionManager implements ISessionManager {
  private static final Map<ISession, Throwable> allSessions =
      new ConcurrentHashMap<ISession, Throwable>();

  /**
   * Check for unreleased sessions and throw an AssertionError if any
   * are found.
   */
  public static void tearDown() {
    if (!allSessions.isEmpty()) {
      // TODO(jlacey): Include all the unreleased sessions instead of just one.
      Throwable first = allSessions.values().iterator().next();
      allSessions.clear();
      // TODO(jlacey): AssertionError does not take a cause in Java 6.
      Error assertion = new AssertionError("Unreleased session");
      assertion.initCause(first);
      throw assertion;
    }
  }

  private MockDmSession currentSession;

  private final HashMap<String, ILoginInfo> sessMgerCreds =
      new HashMap<String, ILoginInfo>(1, 1);

  private final HashMap<String, MockDmSession> sessMgerSessions =
      new HashMap<String, MockDmSession>(1, 1);

  public MockDmSessionManager() {
  }

  /**
   * SessionManager's method - do not set the identified session as current
   * This method only stores credentials. Authentication is performed later,
   * through a newSession(docbase) call.
   */
  @Override
  public void setIdentity(String docbase, ILoginInfo identity) {
    if (identity.getUser() != null && identity.getPassword() != null) {
      if (!sessMgerCreds.containsKey(docbase))
        sessMgerCreds.put(docbase, identity);
      else {
        sessMgerCreds.remove(docbase);
        sessMgerCreds.put(docbase, identity);
      }
    }
  }

  @Override
  public ISession newSession(String docbase) throws RepositoryException {
    if (sessMgerCreds.containsKey(docbase)) {
      currentSession = createAuthenticatedSession(docbase,
          sessMgerCreds.get(docbase));
      if (!sessMgerSessions.containsKey(docbase))
        sessMgerSessions.put(docbase, currentSession);
      else {
        sessMgerSessions.remove(docbase);
        sessMgerSessions.put(docbase, currentSession);
      }
      allSessions.put(currentSession, new Throwable("New session created"));
      return currentSession;
    } else {
      throw new RepositoryLoginException("newSession(" + docbase
          + ") called for docbase " + docbase
          + " without setting any credentials prior to this call");
    }
  }

  @Override
  public void release(ISession session) {
    allSessions.remove(session);
  }

  @Override
  public void clearIdentity(String docbase) {
    sessMgerCreds.remove(docbase);
  }

  @Deprecated
  @Override
  public boolean authenticate(String docbaseName) {
    MockDmSession tmp;
    try {
      tmp = createAuthenticatedSession(docbaseName,
          sessMgerCreds.get(docbaseName));
    } catch (RepositoryException e) {
      return false;
    }
    if (tmp == null) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Sets current session as well. Per DFC javadoc, if session does
   * not exist, a new one is created.
   */
  @Override
  public ISession getSession(String docbase) throws RepositoryException {
    ISession session;
    if (!sessMgerSessions.containsKey(docbase))
      session = newSession(docbase);
    else {
      session = sessMgerSessions.get(docbase);
      allSessions.put(session, new Throwable("Session allocated from pool"));
    }
    return session;
  }

  public String getDocbaseName() {
    for (String n : sessMgerSessions.keySet()) {
      if (sessMgerSessions.get(n).equals(currentSession)) {
        return n;
      }
    }
    return null;
  }

  @Override
  public ILoginInfo getIdentity(String docbase) {
    return sessMgerCreds.get(docbase);
  }

  // //////////////////////////////////////////////////
  /** *****************Internal use****************** */
  // //////////////////////////////////////////////////
  /**
   * Authenticates the same way the SpiRepositoryFromJcr connector does.
   * private then we manage sessions synchronisation within the class that
   * called this method, not here.
   *
   * @param db
   * @param iLI
   * @return
   * @throws RepositoryException
   */
  private MockDmSession createAuthenticatedSession(String db, ILoginInfo iLI)
      throws RepositoryException, RepositoryLoginException {
    // db is actually the suffix of the filename that is used to create the
    // eventlist.
    // As there is no way we can retrieve it, we will store it in the
    // MockDmSession we create.
    MockRepositoryEventList mrel = null;
    try {
      mrel = new MockRepositoryEventList(db);
    } catch (RuntimeException e) {
      throw new RepositoryException(e);
    }

    MockJcrRepository repo = new MockJcrRepository(new MockRepository(mrel));
    Credentials creds = null;
    if (iLI != null) {
      creds = new SimpleCredentials(iLI.getUser(), iLI.getPassword()
          .toCharArray());
    } else {
      throw new RepositoryLoginException("No credentials defined for "
          + db);
    }

    try {
      MockJcrSession sess = (MockJcrSession) repo.login(creds);
      if (sess != null) {
        return new MockDmSession(this, repo, sess, db);
      } else {
        return null;
      }
    } catch (LoginException e) {
      throw new RepositoryLoginException(e);
    }
  }
}
