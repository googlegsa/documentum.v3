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

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmDocbaseMap;
import com.google.enterprise.connector.dctm.dfcwrap.IDocbaseMap;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormSnippetTest extends TestCase {
  /** The Locale-specific resources. */
  private ResourceBundle resources;

  @Override
  protected void setUp() {
    // TODO: This is a hack to share DctmConnectorType.getResources,
    // which is a trivial method.
    DctmConnectorType type = new DctmConnectorType();
    resources = type.getResources(Locale.US);
  }

  /**
   * Tests the {@code appendJavaScript} method. When run in the tests,
   * it will always fail to find the FormSnippet.js file, so this is
   * currently only a test of that error handling.
   */
  public void testJavaScript() {
    // docbases = null, isAdvancedOn = true
    FormSnippet snippet = new FormSnippet(resources, null, true);
    StringBuilder buf = new StringBuilder();

    assertTrue(snippet.isAdvancedOn());
    snippet.appendJavaScript(buf);
    assertFalse(snippet.isAdvancedOn());
    assertTrue(buf.toString(),
        buf.toString().endsWith(resources.getString("javascript_error")));
  }

  /**
   * Tests the {@code appendDropDownListAttribute} method.
   *
   * @param count the number of docbases to return
   * @param value the current docbase value
   * @param options the expected number of options in the select list
   * @param selected the expected value of the selected option
   */
  private void testAddla(int count, String value, int options,
      String selected) {
    StringBuilder buf = new StringBuilder();
    // TODO: With the extraction of getDocbases, we could dispense
    // with the multiple docbase feature of MockDmDocbaseMap.
    try {
      IDocbaseMap docbaseMap = new MockDmDocbaseMap(count);
      List<String> docbases = new ArrayList<String>();
      for (int i = 0; i < docbaseMap.getDocbaseCount(); i++) {
        docbases.add(docbaseMap.getDocbaseName(i));
      }
      FormSnippet snippet = new FormSnippet(resources, docbases, true);

      snippet.appendDropDownListAttribute(buf, value);
    } catch (RepositoryException e) {
      fail(e.toString());
    }
    String selectList = buf.toString();
    assertEquals(options, options(selectList));
    assertEquals(selected, selected(selectList));
  }

  /**
   * Tests the {@code appendDropDownListAttribute} method, and expects
   * the current docbase value to be selected
   *
   * @param count the number of docbases to return
   * @param value the current docbase value
   * @param options the expected number of options in the select list
   */
  private void testAddla(int count, String value, int options) {
    testAddla(count, value, options, value);
  }

  private int options(String selectList) {
    return occurrences("<option ", selectList);
  }

  private String selected(String selectList) {
    Matcher m = Pattern.compile("value=\"(.*)\" selected='selected'")
        .matcher(selectList);
    if (m.find()) {
      assertEquals(1, m.groupCount());
      return m.group(1);
    } else {
      return null;
    }
  }

  private int occurrences(String pattern, String target) {
    Matcher m = Pattern.compile(pattern).matcher(target);
    int count = 0;
    while (m.find()) {
      count++;
    }
    return count;
  }

  /**
   * When there are zero docbases, the empty string is added to the
   * list and selected.
   */
  public void testGcfAddlaZero() {
    testAddla(0, "", 1);
  }

  /** When there is only one docbase, it is the only option, and selected. */
  public void testGcfAddlaOne() {
    testAddla(1, "", 1, DmInitialize.DM_DOCBASE);
  }

  /**
   * When there are multple docbases, the empty string is added to the
   * list and selected.
   */
  public void testGcfAddlaMultiple() {
    testAddla(2, "", 3);
  }

  public void testGpcfAddlaOne() {
    testAddla(1, DmInitialize.DM_DOCBASE, 1);
  }

  public void testGpcfAddlaMultiple() {
    testAddla(2, DmInitialize.DM_DOCBASE, 2);
  }

  /**
   * When the current docbase is not in the list, it is added to the
   * list and selected.
   */
  public void testGpcfAddlaInvalidZero() {
    testAddla(0, "other", 1);
  }

  public void testGpcfAddlaInvalidOne() {
    testAddla(1, "other", 2);
  }

  public void testGpcfAddlaInvalidMultiple() {
    testAddla(2, "other", 3);
  }
}
