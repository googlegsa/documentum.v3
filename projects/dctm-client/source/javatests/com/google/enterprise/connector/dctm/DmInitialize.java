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

public class DmInitialize {
  public static String DM_LOGIN_OK1 = "emilie";

  public static String DM_LOGIN_OK2 = "user1";

  public static String DM_LOGIN_OK3 = "user2";

  public static String DM_LOGIN_OK4 = "queryUser";

  public static String DM_LOGIN_OK5 = "Fred";

  public static String DM_LOGIN_KO = "machinchouette";

  public static String DM_PWD_OK1 = "p@ssw0rd";

  public static String DM_PWD_OK2 = "p@ssw0rd";

  public static String DM_PWD_OK3 = "p@ssw0rd";

  public static String DM_PWD_OK4 = "p@ssw0rd";

  public static String DM_PWD_OK5 = "UnDeux34";

  public static String DM_PWD_KO = "false";

  public static String DM_DOCBASE = "gsadctm";

  public static int DM_RETURN_TOP_UNBOUNDED = 50;

  public static int DM_RETURN_TOP_BOUNDED = 100;

  public static String DM_CLIENTX = "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX";

  public static String DM_WEBTOP_SERVER_URL = "http://swp-vm-wt:8080/webtop/drl/objectId/";

  public static String DM_CHECKPOINT_QUERY_STRING = " and ((r_modify_date = date('2007-01-02 13:58:10','yyyy-mm-dd hh:mi:ss')  and r_object_id > '090000018000e100') OR ( r_modify_date > date('2007-01-02 13:58:10','yyyy-mm-dd hh:mi:ss')))";

  public static String DM_CHECKPOINT = "{\"uuid\":\"0900000180041d34\",\"lastModified\":\"2007-04-19 15:28:16\"}";

  public static String DM_QUERY_STRING_ENABLE = "select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document'"
      + "order by r_modify_date, i_chronicle_id ENABLE (return_top 50)";

  public static String DM_FAlSE_PWD = "false";

  public static String DM_ID1 = "0900000180041d34";

  public static String DM_VSID1 = "0900000180041702";

  public static String DM_ID2 = "0900000180041d34";

  public static String DM_VSID2 = "0900000180041702";

  public static String DM_ID3 = "100000018000017d";

  public static String DM_VSID3 = "100000018000017d";

  public static String DM_ID4 = "4c000001800001b8";

  public static String DM_VSID4 = "4c000001800001b8";

  public static String DM_ID5 = "0900000180005a5b";

  public static String DM_VSID5 = "0900000180005a5b ";

  public static boolean DM_ID2_IS_PUBLIC = false;

  public static String DM_DEFAULT_MIMETYPE = "application/octet-stream";

  public static int DM_DEFAULT_ATTRS = 0;

  public static int DM_ID2_SIZE = 16;

  public static String DM_FIRST_ATTR = "google:ispublic";

  public static HashSet hashIncluded_meta = null;

  static {
    hashIncluded_meta = new HashSet();
    hashIncluded_meta.add("object_name");
    hashIncluded_meta.add("r_object_type");
    hashIncluded_meta.add("title");
    hashIncluded_meta.add("subject");
    hashIncluded_meta.add("keywords");
    hashIncluded_meta.add("authors");
    hashIncluded_meta.add("r_creation_date");
    hashIncluded_meta.add("r_modify_date");
  }

  public static String root_object_type = "dm_sysobject";

  public static HashSet hashIncluded_object_type = null;

  static {
    hashIncluded_object_type = new HashSet();
    hashIncluded_object_type.add("dm_document");
    hashIncluded_object_type.add("custom_type");
  }

  public static String included_meta = "object_name r_object_type title subject keywords authors r_creation_date r_modify_date" ;
  public static String included_object_type = "dm_document custom_type";
}
