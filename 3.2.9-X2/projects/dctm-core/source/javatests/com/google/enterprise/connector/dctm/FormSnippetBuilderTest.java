// Copyright 2010 Google Inc.
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

import com.google.enterprise.connector.dctm.dctmmockwrap.MockDmSession;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FormSnippetBuilderTest extends TestCase {
  private FormSnippetBuilder builder;

  protected void setUp() {
    builder = new FormSnippetBuilder();
  }

  /** Helper method to construct a small set. */
  private Set<String> setOf(String... value) {
      return new HashSet<String>(Arrays.asList(value));
  }

  /** This triggers a comparison using isSubTypeOf, which matches. */
  public void testPropertiesMap_grandtypeFirst() throws RepositoryException {
    Map<String, Set<String>> propertiesMap =
        builder.getPropertiesMap("Grandfoo,foo", new MockDmSession());
    Set<String> types = propertiesMap.get("Grandfoo_attr");
    assertEquals(setOf("Grandfoo"), types);
  }

  /** This triggers a comparison using getSuperType, which fails. */
  public void testPropertiesMap_grandtypeSecond() throws RepositoryException {
    Map<String, Set<String>> propertiesMap =
        builder.getPropertiesMap("foo,Grandfoo", new MockDmSession());
    Set<String> types = propertiesMap.get("Grandfoo_attr");
    assertEquals(setOf("Grandfoo"), types);
  }

  /** This triggers a comparison using getSuperType, which matches. */
  public void testPropertiesMap_supertypeSecond() throws RepositoryException {
    Map<String, Set<String>> propertiesMap =
        builder.getPropertiesMap("foo,Superfoo", new MockDmSession());
    Set<String> types = propertiesMap.get("Superfoo_attr");
    assertEquals(setOf("Superfoo"), types);
  }

  /** A subtype is properly eliminated. */
  public void testPropertiesMap_supertype()
      throws RepositoryException {
    Map<String, Set<String>> propertiesMap =
        builder.getPropertiesMap("Superfoo,foo", new MockDmSession());
    Set<String> types = propertiesMap.get("Super_sharedattr");
    assertEquals(setOf("Superfoo"), types);
  }

  /** Two unrelated types are retained. */
  public void testPropertiesMap_unrelatedType()
      throws RepositoryException {
    Map<String, Set<String>> propertiesMap =
        builder.getPropertiesMap("bar,Superfoo", new MockDmSession());
    Set<String> types = propertiesMap.get("Super_sharedattr");
    Set<String> expected = new HashSet<String>();
    expected.add("Superfoo");
    expected.add("bar");
    assertEquals(expected, types);
  }

  /** The presence of the unrelated type incorrectly retains the subtype. */
  public void testPropertiesMap_supertypeWithUnrelatedType()
      throws RepositoryException {
    Map<String, Set<String>> propertiesMap =
        builder.getPropertiesMap("bar,Superfoo,foo", new MockDmSession());
    Set<String> types = propertiesMap.get("Super_sharedattr");
    Set<String> expected = new HashSet<String>();
    expected.add("Superfoo");
    expected.add("bar");
    assertEquals(expected, types);
  }

  /** Tests a full list of related ancestors. */
  private void testPropertiesMap_oneBranch(String includedObjectType)
      throws RepositoryException {
    Map<String, Set<String>> propertiesMap = builder.getPropertiesMap(
        includedObjectType, new MockDmSession());
    Map<String, Set<String>> expected = new HashMap<String, Set<String>>();
    expected.put("foo_attr", setOf("foo"));
    expected.put("sharedattr", setOf("foo"));
    expected.put("Superfoo_attr", setOf("Superfoo"));
    expected.put("Super_sharedattr", setOf("Superfoo"));
    expected.put("Grandfoo_attr", setOf("Grandfoo"));
    expected.put("Grand_sharedattr", setOf("Grandfoo"));
    expected.put("dm_sysobject_attr", setOf("dm_sysobject"));
    expected.put(SpiConstants.PROPNAME_FOLDER, setOf("dm_sysobject"));
    expected.put(DctmSysobjectDocument.OBJECT_ID_NAME, setOf("dm_sysobject"));
    assertEquals(expected, propertiesMap);
  }

  public void testPropertiesMap_oneBranchForward()
      throws RepositoryException {
    testPropertiesMap_oneBranch("Grandfoo,Superfoo,foo");
  }

  public void testPropertiesMap_oneBranchBackward()
      throws RepositoryException {
    testPropertiesMap_oneBranch("foo,Superfoo,Grandfoo");
  }

  /** Tests a full list of unrelated ancestors. */
  private void testPropertiesMap_twoBranches(String includedObjectType)
      throws RepositoryException {
    Map<String, Set<String>> propertiesMap = builder.getPropertiesMap(
        includedObjectType, new MockDmSession());
    Map<String, Set<String>> expected = new HashMap<String, Set<String>>();
    expected.put("foo_attr", setOf("foo"));
    expected.put("bar_attr", setOf("bar"));
    expected.put("sharedattr", setOf("foo", "bar"));
    expected.put("Superfoo_attr", setOf("Superfoo"));
    expected.put("Superbar_attr", setOf("Superbar"));
    expected.put("Super_sharedattr", setOf("Superfoo", "Superbar"));
    expected.put("Grandfoo_attr", setOf("Grandfoo"));
    expected.put("Grandbar_attr", setOf("Grandbar"));
    expected.put("Grand_sharedattr", setOf("Grandfoo", "Grandbar"));
    expected.put("dm_sysobject_attr", setOf("dm_sysobject"));
    expected.put(SpiConstants.PROPNAME_FOLDER, setOf("dm_sysobject"));
    expected.put(DctmSysobjectDocument.OBJECT_ID_NAME, setOf("dm_sysobject"));
    assertEquals(expected, propertiesMap);
  }

  public void testPropertiesMap_twoBranchesForward()
      throws RepositoryException {
    testPropertiesMap_twoBranches(
        "Grandfoo,Superfoo,foo,Grandbar,Superbar,bar");
  }

  public void testPropertiesMap_sysobject() throws RepositoryException {
    Map<String, Set<String>> propertiesMap = builder.getPropertiesMap(
        "foo,dm_sysobject", new MockDmSession());
    assertEquals(propertiesMap.toString(), 9, propertiesMap.size());
    for (Map.Entry<String, Set<String>> entry : propertiesMap.entrySet()) {
      if (entry.getKey().equals("dm_sysobject_attr") ||
          DctmSysobjectDocument.EXTENDED_PROPERTIES.contains(entry.getKey())) {
        assertEquals(setOf("dm_sysobject"), entry.getValue());
      } else {
        assertEquals(setOf("foo"), entry.getValue());
      }
    }
  }

  public void testPropertiesMap_duplicates() throws RepositoryException {
    Map<String, Set<String>> expected = builder.getPropertiesMap(
        "foo", new MockDmSession());
    Map<String, Set<String>> propertiesMap = builder.getPropertiesMap(
        "foo,foo", new MockDmSession());
    assertEquals(expected, propertiesMap);
  }
}
