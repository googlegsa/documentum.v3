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

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IDocbaseMap;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;

public class MockDmClient implements IClient {
  public MockDmClient() {
  }

  @Override
  public ISessionManager newSessionManager() {
    return new MockDmSessionManager();
  }

  /**
   * Factory method for an IDfQuery object. Constructs an new query object to
   * use for sending DQL queries to Documentum servers.
   */
  @Override
  public IQuery getQuery() {
    return new MockDmQuery();
  }

  /**
   * @throws RepositoryException if a subclass throws it
   */
  @Override
  public IDocbaseMap getDocbaseMap() throws RepositoryException {
    return new MockDmDocbaseMap();
  }
}
