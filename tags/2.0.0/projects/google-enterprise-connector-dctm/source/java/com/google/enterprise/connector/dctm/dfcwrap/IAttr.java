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

public interface IAttr {
  public static final int DM_BOOLEAN = 0;

  public static final int DM_INTEGER = 1;

  public static final int DM_STRING = 2;

  public static final int DM_ID = 3;

  public static final int DM_TIME = 4;

  public static final int DM_DOUBLE = 5;

  public static final int DM_UNDEFINED = 6;

  public String getName();

  public int getDataType();
}
