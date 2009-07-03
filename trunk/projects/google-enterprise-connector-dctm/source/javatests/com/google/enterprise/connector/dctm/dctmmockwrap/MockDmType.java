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

public class MockDmType implements IType {
  String type;

  public MockDmType(String type) {
    this.type = type;
  }

  public int getTypeAttrCount() {
    return 0;
  }

  public IType getSuperType() {
    if (type.startsWith("Super")) {
      return null;
    } else {
      return new MockDmType("Super" + type);
    }
  }

  public IAttr getTypeAttr(int attrIndex) {
    return null;
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
