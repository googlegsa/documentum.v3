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

import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;

public class MockDmLoginInfo implements ILoginInfo {
  private String user;

  private String password;

  public MockDmLoginInfo() {
    user = null;
    password = null;
  }

  public void setUser(String u) {
    user = u;
  }

  public void setPassword(String p) {
    password = p;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }
}
