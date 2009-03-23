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

public interface IType {
  public static int BOOLEAN = 0;

  public static int INT = 1;

  public static int STRING = 2;

  public static int ID = 3;

  public static int TIME = 4;

  public static int DOUBLE = 5;

  public static int UNDEFINED = 6;

  public int getTypeAttrCount() throws RepositoryException;

  public IType getSuperType() throws RepositoryException;

  public IAttr getTypeAttr(int attrIndex) throws RepositoryException;

  public String getDescription() throws RepositoryException;

  public boolean isSubTypeOf(String type) throws RepositoryException;

  public String getName() throws RepositoryException;
}
