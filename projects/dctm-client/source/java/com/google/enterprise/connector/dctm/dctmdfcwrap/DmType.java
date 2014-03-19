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

import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.spi.RepositoryException;

import com.documentum.fc.client.IDfType;
import com.documentum.fc.common.DfException;

public class DmType implements IType {
  private final IDfType idfType;

  public DmType(IDfType idfType) {
    this.idfType = idfType;
  }

  @Override
  public int getTypeAttrCount() throws RepositoryException {
    try {
      return idfType.getTypeAttrCount();
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
  }

  @Override
  public IType getSuperType() throws RepositoryException {
    IDfType idfSuperType;
    try {
      idfSuperType = idfType.getSuperType();
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
    return (idfSuperType == null) ? null : new DmType(idfSuperType);
  }

  @Override
  public boolean isSubTypeOf(String type) throws RepositoryException {
    try {
      return idfType.isSubTypeOf(type);
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
  }

  @Override
  public String getName() throws RepositoryException {
    try {
      return idfType.getName();
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
  }

  @Override
  public IAttr getTypeAttr(int attrIndex) throws RepositoryException {
    try {
      return new DmAttr(idfType.getTypeAttr(attrIndex));
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
  }

  @Override
  public String getTypeAttrNameAt(int attrIndex) throws RepositoryException {
    try {
      return idfType.getTypeAttrNameAt(attrIndex);
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
  }

  @Override
  public String getDescription() throws RepositoryException {
    try {
      return idfType.getDescription();
    } catch (DfException de) {
      throw new RepositoryException(de);
    }
  }
}
