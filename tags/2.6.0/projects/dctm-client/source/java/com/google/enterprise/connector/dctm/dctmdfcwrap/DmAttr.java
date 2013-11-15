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

import com.documentum.fc.common.IDfAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IAttr;

public class DmAttr implements IAttr {
  IDfAttr idfAttr;

  public DmAttr(IDfAttr idfAttr) {
    this.idfAttr = idfAttr;
  }

  public String getName() {
    return idfAttr.getName();
  }

  public int getDataType() {
    return idfAttr.getDataType();
  }
}