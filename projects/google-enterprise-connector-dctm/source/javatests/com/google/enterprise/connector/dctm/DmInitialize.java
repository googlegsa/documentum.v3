package com.google.enterprise.connector.dctm;

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

	public static String DM_CHECKPOINT_QUERY_STRING = " and r_modify_date >= '2007-01-02 13:58:10'";

	public static String DM_QUERY_STRING_ENABLE = "select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document' "
			+ "order by r_modify_date, i_chronicle_id ENABLE (return_top 50)";

	public static final String DM_FAlSE_PWD = "false";

	public static final String DM_ID1 = "0800000180000210";

	public static final String DM_ID2 = "090000018000027e";

	public static final String DM_ID3 = "100000018000017d";

	public static final String DM_ID4 = "4c000001800001b8";

	public static final String DM_ID5 = "4c000001800001b8";

}
