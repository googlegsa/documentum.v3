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

import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;

import com.documentum.fc.common.IDfLoginInfo;

public class DmLoginInfo implements ILoginInfo {
  protected final IDfLoginInfo idfLoginInfo;

  public DmLoginInfo(IDfLoginInfo tmp) {
    idfLoginInfo = tmp;
  }

  @Override
  public void setUser(String u) {
    idfLoginInfo.setUser(u);
  }

  @Override
  public void setPassword(String p) {
    idfLoginInfo.setPassword(p);
  }

  public IDfLoginInfo getIdfLoginInfo() {
    return idfLoginInfo;
  }

  @Override
  public String getUser() {
    return idfLoginInfo.getUser();
  }

  @Override
  public String getPassword() {
    return idfLoginInfo.getPassword();
  }
}
