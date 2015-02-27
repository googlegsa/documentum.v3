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

import com.google.enterprise.connector.spi.RepositoryException;

public interface ICollection {
  int DF_WAITING_STATE = 0;

  int DF_READY_STATE = 1;

  int DF_CLOSED_STATE = 2;

  IValue getValue(String attrName) throws RepositoryException;

  boolean hasNext() throws RepositoryException;

  boolean next() throws RepositoryException;

  String getAllRepeatingStrings(String colName, String separator)
      throws RepositoryException;

  String getString(String colName) throws RepositoryException;

  void close() throws RepositoryException;

  int getState();

  ISession getSession();

  ITime getTime(String colName) throws RepositoryException;
}
