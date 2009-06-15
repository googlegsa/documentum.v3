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
  public static final int DF_WAITING_STATE = 0;

  public static final int DF_READY_STATE = 1;

  public static final int DF_CLOSED_STATE = 2;

  public IValue getValue(String attrName) throws RepositoryException;

  public boolean hasNext() throws RepositoryException;

  public boolean next() throws RepositoryException;

  public String getString(String colName) throws RepositoryException;

  public void close() throws RepositoryException;

  public int getState();

  public ISession getSession();

  public ITime getTime(String colName) throws RepositoryException;
}
