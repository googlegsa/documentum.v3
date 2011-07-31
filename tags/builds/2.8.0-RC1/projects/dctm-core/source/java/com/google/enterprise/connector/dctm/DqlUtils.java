// Copyright 2009 Google Inc.
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

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * DQL-related utility methods.
 */
public class DqlUtils {
  private static final Logger logger =
      Logger.getLogger(DqlUtils.class.getName());

  /**
   * Strips a leading "AND" along with possible leading and trailing
   * whitespace from the given string.
   *
   * @param whereClause a conditional expression with an optional
   * leading "AND"
   * @return a string without the leading "AND" or whitespace
   */
  public static String stripLeadingAnd(String whereClause) {
    logger.config("check additional where clause: " + whereClause);
    return (whereClause == null)
        ? null : whereClause.trim().replaceFirst("(?i)^and\\b\\s*", "");
  }

  /** Appends a conditional expression restricting r_object_type to
   * the given types.
   *
   * @param buffer the buffer to append to
   * @param objectTypes the object type values
   */
  public static void appendObjectTypes(StringBuilder buffer,
      Set<String> objectTypes) {
    Iterator<String> iter = objectTypes.iterator();
    String name = iter.next();
    buffer.append("(r_object_type='").append(name).append("'");
    while (iter.hasNext()) {
      name = iter.next();
      buffer.append(" OR r_object_type='").append(name).append("'");
    }
    buffer.append(')');
  }

  /** This class should not be instantiated. */
  private DqlUtils() {
    throw new AssertionError();
  }
}
