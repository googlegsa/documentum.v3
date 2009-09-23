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

package com.google.enterprise.connector.dctm.dfcwrap;

import java.io.ByteArrayInputStream;

import com.google.enterprise.connector.spi.RepositoryDocumentException;

public interface ISysObject {
  String getObjectName() throws RepositoryDocumentException;

  long getContentSize() throws RepositoryDocumentException;

  ByteArrayInputStream getContent() throws RepositoryDocumentException;

  String getACLDomain() throws RepositoryDocumentException;

  String getACLName() throws RepositoryDocumentException;

  String getString(String name) throws RepositoryDocumentException;

  int getInt(String name) throws RepositoryDocumentException;

  ITime getTime(String name) throws RepositoryDocumentException;

  IType getType() throws RepositoryDocumentException;

  double getDouble(String name) throws RepositoryDocumentException;

  boolean getBoolean(String name) throws RepositoryDocumentException;

  IId getId(String name) throws RepositoryDocumentException;

  IFormat getFormat() throws RepositoryDocumentException;

  int getAttrDataType(String name) throws RepositoryDocumentException;

  int getAttrCount() throws RepositoryDocumentException;

  IAttr getAttr(int attrIndex) throws RepositoryDocumentException;

  void setSessionManager(ISessionManager sessionManager)
      throws RepositoryDocumentException;

  IValue getRepeatingValue(String name, int index)
      throws RepositoryDocumentException;

  int findAttrIndex(String name) throws RepositoryDocumentException;

  int getValueCount(String name) throws RepositoryDocumentException;
}
