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

	public static HashSet excluded_meta = null;

	static {
		excluded_meta = new HashSet();
		excluded_meta.add("i_vstamp");
		excluded_meta.add("i_is_replica");
		excluded_meta.add("i_retainer_id");
		excluded_meta.add("r_aspect_name");
		excluded_meta.add("i_retain_until");
		excluded_meta.add("a_last_review_date");
		excluded_meta.add("a_is_signed");
		excluded_meta.add("a_extended_properties");
		excluded_meta.add("r_full_content_size");
		excluded_meta.add("a_controlling_app");
		excluded_meta.add("a_is_template");
		excluded_meta.add("language_code");
		excluded_meta.add("a_category");
		excluded_meta.add("a_effective_flag");
		excluded_meta.add("a_effective_flag");
		excluded_meta.add("a_effective_label");
		excluded_meta.add("a_publish_formats");
		excluded_meta.add("a_expiration_date");
		excluded_meta.add("a_effective_date");
		excluded_meta.add("r_alias_set_id");
		excluded_meta.add("r_current_state");
		excluded_meta.add("r_resume_state");
		excluded_meta.add("r_policy_id");
		excluded_meta.add("r_is_public");
		excluded_meta.add("r_creator_name");
		excluded_meta.add("a_special_app");
		excluded_meta.add("i_is_reference");
		excluded_meta.add("acl_name");
		excluded_meta.add("acl_domain");
		excluded_meta.add("r_has_events");
		excluded_meta.add("r_frozen_flag");
		excluded_meta.add("r_immutable_flag");
		excluded_meta.add("i_branch_cnt");
		excluded_meta.add("i_direct_dsc");
		excluded_meta.add("r_version_label");
		excluded_meta.add("log_entry");
		excluded_meta.add("r_lock_machine");
		excluded_meta.add("r_lock_date");
		excluded_meta.add("r_lock_owner");
		excluded_meta.add("i_latest_flag");
		excluded_meta.add("i_chronicle_id");
		excluded_meta.add("group_permit");
		excluded_meta.add("world_permit");
		excluded_meta.add("object_name");
		excluded_meta.add("i_antecedent_id");
		excluded_meta.add("group_name");
		excluded_meta.add("owner_permit");
		excluded_meta.add("owner_name");
		excluded_meta.add("i_cabinet_id");
		excluded_meta.add("a_storage_type");
		excluded_meta.add("a_full_text");
		excluded_meta.add("r_content_size");
		excluded_meta.add("r_page_cnt");
		excluded_meta.add("a_content_type");
		excluded_meta.add("i_contents_id");
		excluded_meta.add("r_is_virtual_doc");
		excluded_meta.add("resolution_label");
		excluded_meta.add("r_has_frzn_assembly");
		excluded_meta.add("r_frzn_assembly_cnt");
		excluded_meta.add("r_assembled_from_id");
		excluded_meta.add("r_link_high_cnt");
		excluded_meta.add("r_link_cnt");
		excluded_meta.add("r_order_no");
		excluded_meta.add("r_composite_label");
		excluded_meta.add("r_component_label");
		excluded_meta.add("r_composite_id");
		excluded_meta.add("i_folder_id");
		excluded_meta.add("i_has_folder");
		excluded_meta.add("a_link_resolved");
		excluded_meta.add("i_reference_cnt");
		excluded_meta.add("a_compound_architecture");
		excluded_meta.add("a_archive");
		excluded_meta.add("i_is_deleted");
		excluded_meta.add("a_retention_date");
		excluded_meta.add("a_is_hidden");
		excluded_meta.add("r_access_date");
		excluded_meta.add("r_modifier");
		excluded_meta.add("a_status");
		excluded_meta.add("a_application_type");
	}

	public static HashSet included_meta = null;
	static {
		included_meta = new HashSet();
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
