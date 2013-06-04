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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IDocbaseMap;
import com.google.enterprise.connector.spi.RepositoryException;

public class MockDmDocbaseMap implements IDocbaseMap {
  private final int count;

  /** Constructs a map with a single entry, {@link DmInitialize#DM_DOCBASE}. */
  public MockDmDocbaseMap() {
    this(1);
  }

  /**
   * Constructs a map with the given number of entries. The first
   * entry is {@link DmInitialize#DM_DOCBASE}, and the remaining
   * entries are invalid fillers.
   */
  public MockDmDocbaseMap(int count) {
    this.count = count;
  }

  public int getDocbaseCount() throws RepositoryException {
    return count;
  }

  public String getDocbaseName(int i) throws RepositoryException {
    if (i == 0) {
      return DmInitialize.DM_DOCBASE;
    } else if (i < count) {
      return "docbase_" + String.valueOf(i);
    } else {
      throw new RepositoryException(
          new RuntimeException("Invalid docbase map index."));
    }
  }
}
