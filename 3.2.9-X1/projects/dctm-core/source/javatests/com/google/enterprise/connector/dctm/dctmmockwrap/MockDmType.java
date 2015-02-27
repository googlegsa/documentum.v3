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

import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.spi.RepositoryDocumentException;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock {@code IType} implementation. For any string "foo", this
 * implementation creates a type hierarchy of "dm_sysobject" >
 * "Grandfoo" > "Superfoo" > "foo". Each type creates a new attribute
 * named {@code getTypeName() + "_attr"}, and includes the types from
 * its supertypes. So a type named "foo" has four attributes:
 * "foo_attr", "Superfoo_attr", "Grandfoo_attr", and
 * "dm_sysobject_attr".
 */
/*
 * TODO: Support bushier trees, so that "Superbar_x" and "Superbar_y"
 * are both subtypes of "Grand_bar", or something like that. Use
 * underscore, or capital letters, or even just character prefixes of
 * a fixed length (3 chars: "barx" and "bary" => "Superbar").
 */
public class MockDmType implements IType {
  private final String typeName;
  private final MockDmObject prototype;
  private final MockDmType superType;
  private final List<IAttr> attributes;

  public MockDmType(String typeName) {
    this(typeName, null);
  }

  public MockDmType(String typeName, MockDmObject prototype) {
    this.typeName = typeName;
    this.prototype = prototype;

    this.attributes = new ArrayList<IAttr>();
    this.attributes.add(new MockDmAttr(typeName + "_attr"));
    if (typeName.equals("dm_sysobject")) {
      this.superType = null;
    } else {
      if (typeName.startsWith("Grand")) {
        this.superType = new MockDmType("dm_sysobject");
        this.attributes.add(new MockDmAttr("Grand_sharedattr"));
      } else if (typeName.startsWith("Super")) {
        this.superType =
            new MockDmType(typeName.replaceFirst("Super", "Grand"));
        this.attributes.add(new MockDmAttr("Super_sharedattr"));
      } else {
        this.superType = new MockDmType("Super" + typeName);
        this.attributes.add(new MockDmAttr("sharedattr"));
      }
      this.attributes.addAll(this.superType.attributes);
    }
  }

  @Override
  public int getTypeAttrCount() throws RepositoryDocumentException {
    return (prototype == null) ? attributes.size() : prototype.getAttrCount();
  }

  @Override
  public IType getSuperType() {
    return superType;
  }

  @Override
  public IAttr getTypeAttr(int attrIndex) throws RepositoryDocumentException {
    return (prototype == null)
        ? attributes.get(attrIndex) : prototype.getAttr(attrIndex);
  }

  @Override
  public String getTypeAttrNameAt(int attrIndex)
      throws RepositoryDocumentException {
    return getTypeAttr(attrIndex).getName();
  }

  @Override
  public String getDescription() {
    return typeName + "Description";
  }

  @Override
  public boolean isSubTypeOf(String typeName) {
    for (MockDmType souper = superType;
         souper != null;
         souper = souper.superType) {
      if (souper.typeName.equals(typeName))
        return true;
    }
    return false;
  }

  @Override
  public String getName() {
    return typeName;
  }
}
