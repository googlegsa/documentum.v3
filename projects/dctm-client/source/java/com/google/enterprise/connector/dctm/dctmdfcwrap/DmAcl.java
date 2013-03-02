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

import com.google.enterprise.connector.dctm.dfcwrap.IAcl;
import com.google.enterprise.connector.spi.RepositoryDocumentException;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.common.DfException;

/*
 * @since TODO(Srinivas)
 */
public class DmAcl implements IAcl {
  IDfACL idfAcl;

  public DmAcl(IDfACL idfAcl) {
    this.idfAcl = idfAcl;
  }

  public String getObjectName() throws RepositoryDocumentException {
    try {
      return idfAcl.getObjectName();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public int getAccessorCount() throws RepositoryDocumentException {
    try {
      return idfAcl.getAccessorCount();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public String getAccessorName(int index) throws RepositoryDocumentException {
    try {
      return idfAcl.getAccessorName(index);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public int getAccessorPermit(int index) throws RepositoryDocumentException {
    try {
      return idfAcl.getAccessorPermit(index);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public boolean hasPermission(String permissionName, String accessorName)
      throws RepositoryDocumentException {
    try {
      return idfAcl.hasPermission(permissionName, accessorName);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public boolean isGroup(int index) throws RepositoryDocumentException {
    try {
      return idfAcl.isGroup(index);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public String getDomain() throws RepositoryDocumentException {
    try {
      return idfAcl.getDomain();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }
}
