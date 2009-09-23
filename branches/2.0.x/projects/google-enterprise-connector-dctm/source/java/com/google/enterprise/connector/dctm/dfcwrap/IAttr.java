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
  int DM_BOOLEAN = 0;

  int DM_INTEGER = 1;

  int DM_STRING = 2;

  int DM_ID = 3;

  int DM_TIME = 4;

  int DM_DOUBLE = 5;

  int DM_UNDEFINED = 6;

  String getName();

  int getDataType();
}
