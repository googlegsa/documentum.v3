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

import com.google.enterprise.connector.dctm.dfcwrap.ITime;

import com.documentum.fc.common.IDfTime;

import java.util.Date;

public class DmTime implements ITime {
  private final IDfTime idfTime;

  public DmTime(IDfTime idfTime) {
    this.idfTime = idfTime;
  }

  @Override
  public Date getDate() {
    return idfTime.getDate();
  }

  @Override
  public String asString(String pattern) {
    return idfTime.asString(pattern);
  }

  @Override
  public String getTime_pattern44() {
    return IDfTime.DF_TIME_PATTERN44;
  }

  @Override
  public String toString() {
    return idfTime.toString();
  }
}
