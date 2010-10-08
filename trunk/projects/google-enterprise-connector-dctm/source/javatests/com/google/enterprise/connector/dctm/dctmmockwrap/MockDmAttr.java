// Copyright 2007 Google Inc.
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
import com.google.enterprise.connector.mock.MockRepositoryProperty;

public class MockDmAttr implements IAttr {
  private final String name;
  private final int dataType;

  public MockDmAttr(MockRepositoryProperty mockProp) {
    this.name = mockProp.getName();
    this.dataType = getDataType(mockProp);
  }

  public MockDmAttr(String name) {
    this.name = name;
    this.dataType = IAttr.DM_UNDEFINED; // Does not matter.
  }

  public String getName() {
    return name;
  }

  public int getDataType() {
    return dataType;
  }

  private int getDataType(MockRepositoryProperty mockProp) {
    String type = mockProp.getType().toString();
    if (type.equals("string")) {
      return IAttr.DM_STRING;
    } else if (type.equals("date")) {
      return IAttr.DM_TIME;
    } else if (type.equals("integer")) {
      return IAttr.DM_INTEGER;
    }
    return IAttr.DM_UNDEFINED;
  }
}
