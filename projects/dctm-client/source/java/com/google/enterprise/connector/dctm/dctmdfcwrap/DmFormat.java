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

import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.common.DfException;

public class DmFormat implements IFormat {
  IDfFormat idfFormat;

  public DmFormat(IDfFormat idfFormat) {
    this.idfFormat = idfFormat;
  }

  public boolean canIndex() throws RepositoryLoginException {
    boolean rep = false;
    try {
      if (idfFormat != null) {
        rep = idfFormat.canIndex();
      } else {
        rep = false;
      }
    } catch (DfException de) {
      RepositoryLoginException le = new RepositoryLoginException(de);
      throw le;
    }
    return rep;
  }

  public String getMIMEType() {
    String rep = null;
    try {
      if (idfFormat != null) {
        rep = idfFormat.getMIMEType();
      } else {
        rep = "application/octet-stream";
      }
    } catch (DfException de) {
      rep = "application/octet-stream";
    }
    return rep;
  }

  public String getDOSExtension() {
    String rep = null;
    try {
      if (idfFormat != null) {
        rep = idfFormat.getDOSExtension();
      }
    } catch (DfException de) {
      rep = "";
    }
    return rep;
  }
}
