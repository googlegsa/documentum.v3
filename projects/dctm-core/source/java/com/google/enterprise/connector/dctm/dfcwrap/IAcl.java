// Copyright 2013 Google Inc.
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

package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryDocumentException;

/*
 * @since TODO(Srinivas)
 */
public interface IAcl extends IPersistentObject {
  static final int DF_PERMIT_READ = 3;
  static final int DF_PERMIT_BROWSE = 2;
  static final int DF_PERMIT_NONE = 1;

  static final int DF_PERMIT_TYPE_ACCESS_RESTRICTION = 3;
  static final int DF_PERMIT_TYPE_REQUIRED_GROUP = 6;
  static final int DF_PERMIT_TYPE_REQUIRED_GROUP_SET = 7;

  String getObjectName() throws RepositoryDocumentException;

  int getAccessorCount() throws RepositoryDocumentException;

  String getAccessorName(int index) throws RepositoryDocumentException;

  int getAccessorPermitType(int index) throws RepositoryDocumentException;

  int getAccessorPermit(int index) throws RepositoryDocumentException;

  boolean hasPermission(String permissionName, String accessorName)
      throws RepositoryDocumentException;

  boolean isGroup(int index) throws RepositoryDocumentException;

  String getDomain() throws RepositoryDocumentException;
}
