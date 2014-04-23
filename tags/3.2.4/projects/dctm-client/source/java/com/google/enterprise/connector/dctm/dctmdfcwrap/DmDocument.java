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

package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IDocument;
import com.google.enterprise.connector.spi.RepositoryException;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;

public class DmDocument extends DmSysObject implements IDocument {
  private final IDfDocument idfDocument;

  public DmDocument(IDfDocument idfDocument) {
    super((IDfSysObject) idfDocument);
    this.idfDocument = idfDocument;
  }

  public void setFileEx(String fileName, String formatName)
      throws RepositoryException {
    try {
      idfDocument.setFileEx(fileName, formatName, 0, null);
    } catch (DfException e) {
      RepositoryException re = new RepositoryException(e);
      throw re;
    }
  }

  public void setObjectName(String name) throws RepositoryException {
    try {
      idfDocument.setObjectName(name);
    } catch (DfException e) {
      RepositoryException re = new RepositoryException(e);
      throw re;
    }
  }

  public void setContentType(String contentType) throws RepositoryException {
    try {
      idfDocument.setContentType(contentType);
    } catch (DfException e) {
      RepositoryException re = new RepositoryException(e);
      throw re;
    }
  }

  public void save() throws RepositoryException {
    try {
      idfDocument.save();
    } catch (DfException e) {
      RepositoryException re = new RepositoryException(e);
      throw re;
    }
  }

  public void destroyAllVersions() throws RepositoryException {
    try {
      idfDocument.destroyAllVersions();
    } catch (DfException e) {
      RepositoryException re = new RepositoryException(e);
      throw re;
    }
  }
}
