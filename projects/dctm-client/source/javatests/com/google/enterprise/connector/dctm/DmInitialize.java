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

package com.google.enterprise.connector.dctm;

import java.util.HashSet;
import java.util.Set;

public class DmInitialize {
  public static final String DM_LOGIN_OK1 = "emilie";

  public static final String DM_LOGIN_OK2 = "user1";

  public static final String DM_LOGIN_OK3 = "user2";

  public static final String DM_LOGIN_OK4 = "queryUser";

  public static final String DM_LOGIN_OK5 = "Fred";

  public static final String DM_LOGIN_KO = "machinchouette";

  public static final String DM_PWD_OK1 = "p@ssw0rd";

  public static final String DM_PWD_OK2 = "p@ssw0rd";

  public static final String DM_PWD_OK3 = "p@ssw0rd";

  public static final String DM_PWD_OK4 = "p@ssw0rd";

  public static final String DM_PWD_OK5 = "UnDeux34";

  public static final String DM_PWD_KO = "false";

  public static final String DM_DOCBASE = "gsadctm";

  public static final int DM_RETURN_TOP_UNBOUNDED = 50;

  public static final int DM_RETURN_TOP_BOUNDED = 100;

  public static final String DM_CLIENTX = "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX";

  public static final String DM_WEBTOP_SERVER_URL = "http://swp-vm-wt:8080/webtop/drl/objectId/";

  public static final String DM_CHECKPOINT_QUERY_STRING = " and ((r_modify_date = date('2007-01-02 13:58:10','yyyy-mm-dd hh:mi:ss')  and r_object_id > '090000018000e100') OR ( r_modify_date > date('2007-01-02 13:58:10','yyyy-mm-dd hh:mi:ss')))";

  public static final String DM_CHECKPOINT = "{\"uuid\":\"0900000180041d34\",\"lastModified\":\"2007-04-19 15:28:16\"}";

  public static final String DM_QUERY_STRING_ENABLE = "select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document'"
      + "order by r_modify_date, i_chronicle_id ENABLE (return_top 50)";

  public static final String DM_FAlSE_PWD = "false";

  public static final String DM_ID1 = "0900000180041d34";

  public static final String DM_VSID1 = "0900000180041702";

  public static final String DM_ID2 = "0900000180041d34";

  public static final String DM_VSID2 = "0900000180041702";

  public static final String DM_ID3 = "100000018000017d";

  public static final String DM_VSID3 = "100000018000017d";

  public static final String DM_ID4 = "4c000001800001b8";

  public static final String DM_VSID4 = "4c000001800001b8";

  public static final String DM_ID5 = "0900000180005a5b";

  public static final String DM_VSID5 = "0900000180005a5b ";

  public static final boolean DM_ID2_IS_PUBLIC = false;

  public static final String DM_DEFAULT_MIMETYPE = "application/octet-stream";

  public static final int DM_DEFAULT_ATTRS = 0;

  public static final int DM_ID2_SIZE = 16;

  public static final String DM_FIRST_ATTR = "google:ispublic";

  public static final String DM_INCLUDED_META = "object_name r_object_type title subject keywords authors r_creation_date r_modify_date";
  public static final String DM_INCLUDED_OBJECT_TYPE = "dm_document custom_type";

  public static final Set<String> included_meta = new HashSet<String>();

  static {
    included_meta.add("object_name");
    included_meta.add("r_object_type");
    included_meta.add("title");
    included_meta.add("subject");
    included_meta.add("keywords");
    included_meta.add("authors");
    included_meta.add("r_creation_date");
    included_meta.add("r_modify_date");
  }

  public static final String ROOT_OBJECT_TYPE = "dm_sysobject";

  public static final Set<String> hashIncluded_object_type =
      new HashSet<String>();

  static {
    hashIncluded_object_type.add("dm_document");
    hashIncluded_object_type.add("custom_type");
  }
}
