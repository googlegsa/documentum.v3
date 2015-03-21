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

import com.google.enterprise.connector.dctm.dfcwrap.IGroup;
import com.google.enterprise.connector.spi.RepositoryDocumentException;

import com.documentum.fc.client.IDfGroup;
import com.documentum.fc.common.DfException;

import java.util.logging.Logger;

/**
 * @since 3.2.0
 */
public class DmGroup implements IGroup {
  private static final Logger logger =
      Logger.getLogger(DmQuery.class.getName());

  private final IDfGroup idfGroup;

  public DmGroup(IDfGroup idfGroup) {
    this.idfGroup = idfGroup;
  }

  @Override
  public String getUserSource() throws RepositoryDocumentException {
    try {
      logger.finest("getUserSourceAsString value: " + idfGroup.getGroupSource()
          + " for user " + idfGroup.getGroupName());
      return idfGroup.getGroupSource();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }
}
