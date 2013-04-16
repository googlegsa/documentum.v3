// Copyright 2013 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IUser;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;

import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;

import java.util.logging.Logger;

/*
 * @since TODO(Srinivas)
 */
public class DmUser implements IUser {
  private static final Logger logger = 
      Logger.getLogger(DmQuery.class.getName());

  private final IDfUser idfUser;

  public DmUser(IDfUser idfUser) {
    this.idfUser = idfUser;
  }

  public String getUserName() throws RepositoryException {
    try {
      return idfUser.getUserName();
    } catch (DfException e) {
      throw new RepositoryException(e);
    }
  }

  public String getUserLoginName() throws RepositoryException {
    try {
      return idfUser.getUserLoginName();
    } catch (DfException e) {
      throw new RepositoryException(e);
    }
  }

  public String getUserSourceAsString() throws RepositoryDocumentException {
    try {
      logger.finest("getUserSourceAsString value: "
          + idfUser.getUserSourceAsString() + " for user "
          + idfUser.getUserName());
      return idfUser.getUserSourceAsString();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }
}
