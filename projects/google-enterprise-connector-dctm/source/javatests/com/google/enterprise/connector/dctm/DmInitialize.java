package com.google.enterprise.connector.dctm;

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

	public static String DM_CHECKPOINT_QUERY_STRING = " and r_modify_date >= '2007-01-02 13:58:10' and i_chronicle_id > '090000018000e100'";

	public static String DM_QUERY_STRING_ENABLE = "select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document' "
			+ "order by r_modify_date, i_chronicle_id ENABLE (return_top 50)";

	public static String DM_FAlSE_PWD = "false";

	public static String DM_ID1 = "0800000180000210";

	public static String DM_ID2 = "090000018000027e";

	public static String DM_ID3 = "100000018000017d";

	public static String DM_ID4 = "4c000001800001b8";

	public static String DM_ID5 = "090000018000a5ee";

	public DmInitialize(boolean DFC) {
		if (!DFC) {
			DM_LOGIN_OK1 = "joe";
			DM_LOGIN_OK2 = "mary";
			DM_LOGIN_OK3 = "user1";
			DM_LOGIN_OK4 = "mark";
			DM_LOGIN_OK5 = "bill";
			DM_LOGIN_KO = "machinchouette";
			DM_PWD_OK1 = "joe";
			DM_PWD_OK2 = "mary";
			DM_PWD_OK3 = "user1";
			DM_PWD_OK4 = "mark";
			DM_PWD_OK5 = "bill";
			DM_PWD_KO = "wdfshsgdh";
			DM_DOCBASE = "SwordEventLog.txt";
			DM_RETURN_TOP_UNBOUNDED = 50;
			DM_RETURN_TOP_BOUNDED = 100;
			DM_CLIENTX = "com.google.enterprise.connector.dctm.dctmmockwrap.DmClientX";
			DM_WEBTOP_SERVER_URL = "http://swp-vm-wt:8080/webtop/drl/objectId/";
			DM_CHECKPOINT_QUERY_STRING = " and @jcr:lastModified >= ''{0}''";
			DM_QUERY_STRING_ENABLE = "//*[@jcr:primaryType='nt:resource'] order by @jcr:lastModified, @jcr:uuid";
			DM_FAlSE_PWD = "wdfshsgdh";
			DM_ID1 = "doc1";
			DM_ID2 = "doc2";
			DM_ID3 = "doc3";
			DM_ID4 = "doc10";
			DM_ID5 = "doc26";
		}
	}

}
