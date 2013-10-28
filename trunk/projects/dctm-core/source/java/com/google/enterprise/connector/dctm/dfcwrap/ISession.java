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

package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;

public interface ISession {
  String getDocbaseName() throws RepositoryException;

  String getServerVersion() throws RepositoryException;

  IPersistentObject getObject(IId objectId) throws RepositoryDocumentException;

  String getLoginTicketForUser(String username)
      throws RepositoryException;

  String getLoginTicketEx(String username, String scope, int timeout,
      boolean singleUse, String serverName) throws RepositoryException;

  IType getType(String typeName) throws RepositoryException;

  ISessionManager getSessionManager() throws RepositoryException;

  boolean isConnected();

  /**
   * @since 3.2.0
   */
  IPersistentObject getObjectByQualification(String string)
      throws RepositoryDocumentException;
}
