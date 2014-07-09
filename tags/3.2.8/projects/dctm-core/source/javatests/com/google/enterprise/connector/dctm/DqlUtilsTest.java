// Copyright 2014 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.dctm;

import junit.framework.TestCase;

public class DqlUtilsTest extends TestCase {
  public void testEscapeString_null() {
    try {
      DqlUtils.escapeString(null);
      fail("Expected a NullPointerException");
    } catch (NullPointerException expected) {
    }
  }

  public void testEscapeString_empty() {
    assertEquals("", DqlUtils.escapeString(""));
  }

  public void testEscapeString_unescapedString() {
    String value = "hello world";
    assertEquals(value, DqlUtils.escapeString(value));
  }

  public void testEscapeString_unescapedPattern() {
    String value = "\\ % _ \\\\ \\% \\_";
    assertEquals(value, DqlUtils.escapeString(value));
  }

  public void testEscapeString_quoted() {
    String value = "' \\ % _ \\' \\\\ \\% \\_";
    assertEquals(value.replace("'", "''"), DqlUtils.escapeString(value));
  }

  public void testEscapePattern_null() {
    try {
      DqlUtils.escapePattern(null, '\\');
      fail("Expected a NullPointerException");
    } catch (NullPointerException expected) {
    }
  }

  public void testEscapePattern_empty() {
    assertEquals("", DqlUtils.escapePattern("", '\\'));
  }

  public void testEscapePattern_unescapedString() {
    String value = "hello world";
    assertEquals(value, DqlUtils.escapePattern(value, '\\'));
  }

  public void testEscapePattern_escapedPattern() {
    String value = "\\ % _ \\\\ \\% \\_";
    String escaped = "\\\\ \\% \\_ \\\\\\\\ \\\\\\% \\\\\\_";
    assertEquals(escaped, DqlUtils.escapePattern(value, '\\'));
  }

  public void testEscapePattern_quoted() {
    String value = "' \\ % _ \\' \\\\ \\% \\_";
    String escaped = "'' \\\\ \\% \\_ \\\\'' \\\\\\\\ \\\\\\% \\\\\\_";
    assertEquals(escaped, DqlUtils.escapePattern(value, '\\'));
  }

  public void testEscapePattern_quotedSlash() {
    String value = "' \\ / % _ /' \\\\ // /% /_";
    String escaped = "'' \\ // /% /_ //'' \\\\ //// ///% ///_";
    assertEquals(escaped, DqlUtils.escapePattern(value, '/'));
  }
}
