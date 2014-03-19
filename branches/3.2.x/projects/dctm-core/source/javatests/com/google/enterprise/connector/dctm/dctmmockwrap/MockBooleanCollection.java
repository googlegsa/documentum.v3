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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryException;

/** An empty, single row collection, just enough for the where clause tests .*/
public class MockBooleanCollection implements ICollection {
  private boolean hasNext;

  MockBooleanCollection(boolean hasNext) throws RepositoryException {
    this.hasNext = hasNext;
  }

  @Override
  public boolean hasNext() {
    return hasNext;
  }

  @Override
  public boolean next() {
    // It may not matter, but let's return a finite collection.
    boolean temp = hasNext;
    hasNext = false;
    return temp;
  }

  @Override
  public String getString(String colName) throws RepositoryException {
    throw new UnsupportedOperationException();
  }

  @Override
  public IValue getValue(String attrName) throws RepositoryException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() throws RepositoryException {
  }

  @Override
  public int getState() {
    return ICollection.DF_READY_STATE;
  }

  @Override
  public ISession getSession(){
    throw new UnsupportedOperationException();
  }

  @Override
  public ITime getTime(String colName) throws RepositoryException {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAllRepeatingStrings(String colName, String separator)
      throws RepositoryException {
    throw new UnsupportedOperationException();
  }
}
