package com.google.enterprise.connector.dctm.dctmmockwrap;

public class DmInitialize {
	public static String DM_LOGIN_OK1 = "joe";

	public static String DM_LOGIN_OK2 = "mary";

	public static String DM_LOGIN_OK3 = "user1";

	public static String DM_LOGIN_OK4 = "mark";

	public static String DM_LOGIN_OK5 = "bill";

	public static String DM_LOGIN_KO = "machinchouette";

	public static String DM_PWD_OK1 = "joe";

	public static String DM_PWD_OK2 = "mary";

	public static String DM_PWD_OK3 = "user1";

	public static String DM_PWD_OK4 = "mark";

	public static String DM_PWD_OK5 = "bill";

	public static String DM_PWD_KO = "wdfshsgdh";

	public static String DM_DOCBASE = "SwordEventLog.txt";

	public static int DM_RETURN_TOP_UNBOUNDED = 27;

	public static int DM_RETURN_TOP_BOUNDED = 27;

	public static String DM_CLIENTX = "com.google.enterprise.connector.dctm.dctmmockwrap.MockDmClient";

	public static String DM_WEBTOP_SERVER_URL = "http://swp-vm-wt:8080/webtop/drl/objectId/";

	// /public static String DM_CHECKPOINT_QUERY_STRING = " and
	// @jcr:lastModified >= ''{0}''";

	public static String DM_CHECKPOINT_QUERY_STRING = " and r_modify_date >= '1970-01-01 01:00:00.020' and i_chronicle_id > 'doc2'";

	public static String DM_QUERY_STRING_ENABLE = "//*[@jcr:primaryType='nt:resource'] order by @jcr:lastModified, @jcr:uuid";

	public static String DM_FAlSE_PWD = "wdfshsgdh";

	public static String DM_ID1 = "users";

	public static String DM_ID2 = "doc2";

	public static String DM_ID3 = "doc3";

	public static String DM_ID4 = "doc10";

	public static String DM_ID5 = "doc26";

	public static boolean DM_ID2_IS_PUBLIC = false;

	public static String DM_DEFAULT_MIMETYPE = "application/octet-stream";

	public static int DM_DEFAULT_ATTRS = 2;

	public static int DM_ID2_SIZE = 16;

	public static int DM_ID2_TIMESTAMP = 20;

	public static String DM_ID2_TIMESTRING = "1970-01-01T01:00:00Z";

	public static String DM_FIRST_ATTR = "google:ispublic";

	public static int DM_ID1_TYPE = 1;
}
