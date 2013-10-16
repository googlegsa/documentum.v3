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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IFormat;


public class MockDmFormat implements IFormat {
  private final String mimeType;

  public MockDmFormat(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getName() {
    return mimeType;
  }

  public boolean canIndex() {
    return true;
  }

  public String getMIMEType() {
    return mimeType;
  }

  public String getDOSExtension() {
    return null;
  }
}
