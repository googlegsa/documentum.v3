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

import java.util.Date;

import com.documentum.fc.common.IDfTime;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;

public class DmTime implements ITime {
  private IDfTime idfTime;

  private String time_pattern44 = IDfTime.DF_TIME_PATTERN44;

  public DmTime(IDfTime idfTime) {
    this.idfTime = idfTime;
  }

  public Date getDate() {
    return idfTime.getDate();
  }

  public String asString(String pattern) {
    return idfTime.asString(pattern);
  }

  public String getTime_pattern44() {
    return time_pattern44;
  }

  @Override
  public String toString() {
    return idfTime.toString();
  }
}
