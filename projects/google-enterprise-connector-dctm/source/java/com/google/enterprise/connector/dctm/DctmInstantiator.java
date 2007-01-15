package com.google.enterprise.connector.dctm;

public class DctmInstantiator {
	//This class simulates an acces to the ressources provided by Spring.

	public static boolean isDFCavailable = true;

	public static String QUERY_STRING_UNBOUNDED_DEFAULT;
	
	public static String QUERY_STRING_BOUNDED_DEFAULT;
	public static String WEBTOP_SERVER_URL;

	public static String QUERY_STRING_AUTHORISE_DEFAULT = "select r_object_id from dm_sysobject where r_object_id in (";
	public static String ATTRIBUTE_NAME = "r_object_id";

	
	public static void initialize(){
		if (isDFCavailable) {
			QUERY_STRING_UNBOUNDED_DEFAULT = "select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document' " +
					"order by r_modify_date, i_chronicle_id ";
			  
			 QUERY_STRING_BOUNDED_DEFAULT = 
				 "select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type=''dm_document'' and r_modify_date >= "+ 
				 "''{0}'' "+
				 "order by r_modify_date, i_chronicle_id";
			 WEBTOP_SERVER_URL = "http://swp-pc-jpn:8080/webtop/drl/objectId=";

			 
			 QUERY_STRING_AUTHORISE_DEFAULT = "select r_object_id from dm_sysobject where r_object_id in (";
			 ATTRIBUTE_NAME = "r_object_id";

		} else {
			QUERY_STRING_BOUNDED_DEFAULT = 
				"//*[@jcr:primaryType = 'nt:resource' and @jcr:lastModified >= " +
				"''{0}''] order by @jcr:lastModified, @jcr:uuid";
			
			QUERY_STRING_UNBOUNDED_DEFAULT = 
				"//*[@jcr:primaryType='nt:resource'] order by @jcr:lastModified, @jcr:uuid";
			ATTRIBUTE_NAME="";
			QUERY_STRING_AUTHORISE_DEFAULT="";
		}
	}
}
