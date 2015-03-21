// Copyright 2007 Google Inc.
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

import java.util.HashSet;
import java.util.Set;

public class DmInitialize {
  public static final String DM_LOGIN_OK1 = "joe";

  public static final String DM_LOGIN_OK2 = "mary";

  public static final String DM_LOGIN_OK2_DOMAIN = "example\\mary";

  public static final String DM_LOGIN_OK2_DNS_DOMAIN = "mary@example.com";

  public static final String DM_LOGIN_OK3 = "user1";

  public static final String DM_LOGIN_OK4 = "mark";

  public static final String DM_LOGIN_OK5 = "bill";

  public static final String DM_LOGIN_KO = "machinchouette";

  public static final String DM_PWD_OK1 = "joe";

  public static final String DM_PWD_OK2 = "mary";

  public static final String DM_PWD_OK2_DNS_DOMAIN = "mary@example.com";

  public static final String DM_PWD_OK3 = "user1";

  public static final String DM_PWD_OK4 = "mark";

  public static final String DM_PWD_OK5 = "bill";

  public static final String DM_PWD_KO = "wdfshsgdh";

  public static final String DM_DOCBASE = "SwordEventLog.txt";

  public static final int DM_RETURN_TOP_UNBOUNDED = 27;

  public static final int DM_RETURN_TOP_BOUNDED = 27;

  public static final String DM_CLIENTX = "com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClientX";

  public static final String DM_WEBTOP_SERVER_URL = "http://swp-vm-wt:8080/webtop/drl/objectId/";

  public static final String DM_QUERY_STRING_ENABLE = "//*[@jcr:primaryType='nt:resource'] order by @jcr:lastModified, @jcr:uuid";

  public static final String DM_ID1 = "users";

  public static final String DM_ID2 = "doc2";

  public static final String DM_ID3 = "doc3";

  public static final String DM_ID4 = "doc10";

  public static final String DM_ID5 = "doc26";

  public static final boolean DM_ID2_IS_PUBLIC = false;

  public static final String DM_DEFAULT_MIMETYPE = "application/octet-stream";

  public static final int DM_DEFAULT_ATTRS = 2;

  public static final int DM_ID2_SIZE = 16;

  public static final int DM_ID2_TIMESTAMP = 20;

  public static final String DM_ID2_TIMESTRING = "1970-01-01T00:00:00Z";

  public static final String DM_LOCAL_NAMESPACE = "localNS";

  public static final String DM_GLOBAL_NAMESPACE = "globalNS";

  public static final String DM_INCLUDED_OBJECT_TYPE = "dm_document";

  public static final String DM_INCLUDED_META = "object_name,r_object_type,title,subject,keywords,authors,r_creation_date,r_modify_date,r_content_size,a_content_type";

  public static final Set<String> included_meta;

  static {
    included_meta = new HashSet<String>();
    included_meta.add("object_name");
    included_meta.add("r_object_type");
    included_meta.add("title");
    included_meta.add("subject");
    included_meta.add("keywords");
    included_meta.add("authors");
    included_meta.add("r_creation_date");
    included_meta.add("r_modify_date");
  }
}
