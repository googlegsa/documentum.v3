// Copyright (C) 2009 Google Inc.
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

import junit.framework.TestCase;

public class MockDmTypeTest extends TestCase {
  private static final String TYPE_NAME = "MockType";

  public void testType() throws Exception {
    IType type = new MockDmType(TYPE_NAME);
    assertEquals(TYPE_NAME, type.getName());
    assertEquals(TYPE_NAME + "Description", type.getDescription());
  }

  public void testSuperType() throws Exception {
    IType type = new MockDmType(TYPE_NAME);
    IType souper = type.getSuperType();
    assertNotNull(souper);
    assertEquals("Super" + TYPE_NAME, souper.getName());
    assertTrue(type.isSubTypeOf(souper.getName()));
    assertNull(souper.getSuperType());
  }
}
