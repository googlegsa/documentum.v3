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

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.dfcwrap.IId;

public class DmId implements IId {
  IDfId idfId;

  public DmId(String id) {
    this.idfId = new DfId(id);
  }

  public DmId(IDfId idfId) {
    this.idfId = idfId;
  }

  public IDfId getidfId() {
    return idfId;
  }

  public void setidfId(IDfId idfId) {
    this.idfId = idfId;
  }

  public String getId() {
    return this.idfId.getId();
  }
}
