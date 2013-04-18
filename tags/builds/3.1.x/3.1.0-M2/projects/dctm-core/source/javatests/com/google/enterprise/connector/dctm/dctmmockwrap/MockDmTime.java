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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.enterprise.connector.dctm.dfcwrap.ITime;

public class MockDmTime implements ITime {
  private Date time;

  public MockDmTime(Date time) {
    this.time = time;
  }

  public Date getDate() {
    return time;
  }

  public String getFormattedDate() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss'Z'", new Locale("EN"));
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    return simpleDateFormat.format(time);
  }

  public String asString(String pattern) {
    // TODO Auto-generated method stub
    return null;
  }

  public String getTime_pattern44() {
    // TODO Auto-generated method stub
    return null;
  }
}
