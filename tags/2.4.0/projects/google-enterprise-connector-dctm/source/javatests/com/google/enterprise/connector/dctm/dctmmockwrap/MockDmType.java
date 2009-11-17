// Copyright (C) 2009 Google Inc.
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

import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.spi.RepositoryDocumentException;

public class MockDmType implements IType {
  private final String type;
  private final MockDmObject prototype;

  public MockDmType(String type) {
    this(type, null);
  }

  public MockDmType(String type, MockDmObject prototype) {
    this.type = type;
    this.prototype = prototype;
  }

  public int getTypeAttrCount() throws RepositoryDocumentException {
    return (prototype == null) ? 0 : prototype.getAttrCount();
  }

  public IType getSuperType() {
    if (type.startsWith("Super")) {
      return null;
    } else {
      return new MockDmType("Super" + type);
    }
  }

  public IAttr getTypeAttr(int attrIndex) throws RepositoryDocumentException {
    return (prototype == null) ? null : prototype.getAttr(attrIndex);
  }

  public String getDescription() {
    return type + "Description";
  }

  public boolean isSubTypeOf(String type) {
    return (type == null) ? false : type.equals("Super" + this.type);
  }

  public String getName() {
    return type;
  }
}
