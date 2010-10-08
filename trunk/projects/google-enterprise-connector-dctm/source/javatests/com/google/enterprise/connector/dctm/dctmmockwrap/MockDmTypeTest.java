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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MockDmTypeTest extends TestCase {
  private static final String TYPE_NAME = "MockType";
  private static final String ATTR_NAME = TYPE_NAME + "_attr";
  private static final String[] SUPER_TYPE_NAMES = {
      TYPE_NAME, "Super" + TYPE_NAME, "Grand" + TYPE_NAME, "dm_sysobject" };

  private List<String> superTypeNames;

  protected void setUp() {
    superTypeNames = new ArrayList<String>(Arrays.asList(SUPER_TYPE_NAMES));
  }

  public void testType() throws RepositoryException {
    IType type = new MockDmType(TYPE_NAME);
    assertEquals(TYPE_NAME, type.getName());
    assertEquals(TYPE_NAME + "Description", type.getDescription());
  }

  public void testSuperType() throws RepositoryException {
    IType type = new MockDmType(TYPE_NAME);
    IType souper = type.getSuperType();
    assertNotNull(souper);
    assertEquals("Super" + TYPE_NAME, souper.getName());
    assertTrue(type.isSubTypeOf(souper.getName()));
    assertNotNull(souper.getSuperType());
  }

  public void testAncestorTypes() throws RepositoryException {
    IType type = new MockDmType(TYPE_NAME);
    assertTrue(superTypeNames.remove(TYPE_NAME));
    for (IType souper = type.getSuperType();
         souper != null;
         souper = souper.getSuperType()) {
      String name = souper.getName();
      assertTrue(name, type.isSubTypeOf(souper.getName()));
      assertTrue(name, superTypeNames.remove(name));
    }
    assertTrue(superTypeNames.toString(), superTypeNames.isEmpty());
  }

  public void testSysObject() throws RepositoryException {
    IType type = new MockDmType("dm_sysobject");
    IType souper = type.getSuperType();
    assertNull(souper);
    int attrCount = type.getTypeAttrCount();
    assertEquals(1, attrCount);
    assertEquals("dm_sysobject_attr", type.getTypeAttrNameAt(0));
  }

  public void testAttributes() throws RepositoryException {
    IType type = new MockDmType(TYPE_NAME);
    int attrCount = type.getTypeAttrCount();
    assertEquals(7, attrCount);

    Set<String> expected = new HashSet<String>(superTypeNames);
    Set<String> expectedShared = new HashSet<String>();
    expectedShared.add("Grand_sharedattr");
    expectedShared.add("Super_sharedattr");
    expectedShared.add("sharedattr");

    Set<String> actual = new HashSet<String>();
    Set<String> actualShared = new HashSet<String>();
    for (int i = 0; i < attrCount; i++) {
      String attrName = type.getTypeAttrNameAt(i);
      assertTrue(attrName, attrName.endsWith("attr"));
      if (attrName.endsWith("_attr")) {
        actual.add(attrName.substring(0, attrName.length() - "_attr".length()));
      } else {
        actualShared.add(attrName);
      }
    }
    assertEquals(expected, actual);
    assertEquals(expectedShared, actualShared);
  }
}
