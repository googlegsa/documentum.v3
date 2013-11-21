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

import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.common.DfException;

public class DmFormat implements IFormat {
  private final IDfFormat idfFormat;

  public DmFormat(IDfFormat idfFormat) {
    this.idfFormat = idfFormat;
  }

  @Override
  public String getName() {
    String rep;
    try {
      if (idfFormat != null) {
        rep = idfFormat.getName();
      } else {
        rep = "";
      }
    } catch (DfException de) {
      rep = "";
    }
    return rep;
  }

  /**
   * Gets whether this format is configured for indexing on the
   * server. If the answer isn't definitive, err on the side of
   * feeding the content.
   */
  @Override
  public boolean canIndex() {
    boolean rep;
    try {
      if (idfFormat != null) {
        rep = idfFormat.canIndex();
      } else {
        rep = true;
      }
    } catch (DfException de) {
      rep = true;
    }
    return rep;
  }

  @Override
  public String getMIMEType() {
    String rep;
    try {
      if (idfFormat != null) {
        rep = idfFormat.getMIMEType();
        if (rep == null || rep.length() == 0 || rep.indexOf('/') < 0) {
          rep = "application/octet-stream";
        }
      } else {
        rep = "application/octet-stream";
      }
    } catch (DfException de) {
      rep = "application/octet-stream";
    }
    return rep;
  }

  @Override
  public String getDOSExtension() {
    String rep;
    try {
      if (idfFormat != null) {
        rep = idfFormat.getDOSExtension();
      } else {
          rep = "";
      }
    } catch (DfException de) {
      rep = "";
    }
    return rep;
  }
}
