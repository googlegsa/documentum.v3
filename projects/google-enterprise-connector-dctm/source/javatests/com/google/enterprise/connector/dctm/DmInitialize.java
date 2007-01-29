package com.google.enterprise.connector.dctm;

public class DmInitialize {
	public static final String DM_LOGIN_OK1 = "emilie";
	public static final String DM_LOGIN_OK2 = "user1";
	public static final String DM_LOGIN_OK3 = "user2";
	public static final String DM_LOGIN_OK4 = "queryUser";
	public static final String DM_LOGIN_OK5 = "Fred";
	public static final String DM_PWD_OK1 = "p@ssw0rd";
	public static final String DM_PWD_OK2 = "UnDeux34";
	public static final String DM_PWD_KO = "false";
	public static final String DM_DOCBASE = "gsadctm";
	public static final int DM_RETURN_TOP = 50;
	public static final String DM_CLIENTX = "com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX";
	public static final String DM_QUERY_STRING_UNBOUNDED_DEFAULT = "select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document' " +"order by r_modify_date, i_chronicle_id ENABLE (return_top "+DM_RETURN_TOP+")";
	public static final String DM_WEBTOP_SERVER_URL = "http://swp-vm-wt:8080/webtop/drl/objectId/";
	public static final String DM_QUERY_STRING_BOUNDED_DEFAULT = "select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type=''dm_document'' and r_modify_date >= "+ "''{0}'' "+"order by r_modify_date, i_chronicle_id";
	public static final String DM_ATTRIBUTE_NAME = "r_object_id";
	public static final String DM_QUERY_STRING_AUTHORISE_DEFAULT = "select r_object_id from dm_sysobject where r_object_id in (";
	public static final String DM_FAlSE_PWD = "false";
}
