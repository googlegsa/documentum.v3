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

package com.google.enterprise.connector.dctm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.enterprise.connector.spiimpl.DateValue;

public class DctmDateValue extends DateValue {
  Calendar calendarValue;

  private static Logger logger = null;

  static {
    logger = Logger.getLogger(DctmDateValue.class.getName());
  }

  private static final SimpleDateFormat DCTM_DATE_FORMAT = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm:ss");

  public DctmDateValue(Calendar calendarValue) {
    super(calendarValue);
    this.calendarValue = calendarValue;
  }

  /**
   * Formats a calendar object as Dctm format.
   *
   * @param c
   * @return a String in ISO-8601 format - always in GMT zone
   */
  public String toDctmFormat() {
    Date d = calendarValue.getTime();

    logger.log(Level.INFO, "Date obtained, After getTime()");

    String dctmFormat = DCTM_DATE_FORMAT.format(d);

    logger.log(Level.INFO, "to Dctm Format for the date : " + dctmFormat);

    return dctmFormat;
  }
}
