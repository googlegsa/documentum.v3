// Copyright 2013 Google Inc. All Rights Reserved.
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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IAcl;
import com.google.enterprise.connector.spi.RepositoryDocumentException;

import java.util.ArrayList;
import java.util.List;

public class MockDmAcl implements IAcl {
  private final int aclId;
  private final String aclname;
  private final List<MockAce> accessorList = new ArrayList<MockAce>();

  public MockDmAcl(int aclId, String aclname) {
    this.aclId = aclId;
    this.aclname = aclname;
  }

  /*
   * Method to add ACE with permit type.
   */
  public void addAccessor(String name, int permit, int permitType,
      boolean isGroup) {
    accessorList.add(new MockAce(name, permit, permitType, isGroup));
  }

  public int getAclId() {
    return this.aclId;
  }

  @Override
  public String getObjectName() throws RepositoryDocumentException {
    return aclname;
  }

  @Override
  public int getAccessorCount() throws RepositoryDocumentException {
    return accessorList.size();
  }

  @Override
  public String getAccessorName(int index) throws RepositoryDocumentException {
    MockAce accessor = accessorList.get(index);
    return accessor.getName();
  }

  @Override
  public int getAccessorPermitType(int index)
      throws RepositoryDocumentException {
    MockAce accessor = accessorList.get(index);
    return accessor.getPermitType();
  }

  @Override
  public int getAccessorPermit(int index) throws RepositoryDocumentException {
    MockAce accessor = accessorList.get(index);
    return accessor.getPermit();
  }

  @Override
  public boolean hasPermission(String permissionName, String accessorName)
      throws RepositoryDocumentException {
    return false;
  }

  @Override
  public boolean isGroup(int index) throws RepositoryDocumentException {
    MockAce accessor = accessorList.get(index);
    return accessor.isGroup();
  }

  @Override
  public String getDomain() throws RepositoryDocumentException {
    return null;
  }

  private static class MockAce {
    private final String name;
    private final int permit;
    private final int permitType;
    private final boolean bGroup;

    MockAce(String name, int permit, int permitType, boolean bGroup) {
      this.name = name;
      this.permit = permit;
      this.permitType = permitType;
      this.bGroup = bGroup;
    }

    public String getName() {
      return name;
    }

    public int getPermit() {
      return permit;
    }

    public int getPermitType() {
      return permitType;
    }

    boolean isGroup() {
      return bGroup;
    }
  }
}
