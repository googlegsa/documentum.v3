package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dfcwrap.IQuery;

public class DFC_MOCK_instanciator {
	//That private static variable has to be valuated by Spring configuration? For the moment let's keep it hard coded.
	private static final boolean isDFCavailable=true;
	
	public static String QUERY_STRING_UNBOUNDED_DEFAULT;
	
	public static String QUERY_STRING_BOUNDED_DEFAULT;
	
	public static void initialize(){
		if (isDFCavailable) {
			QUERY_STRING_UNBOUNDED_DEFAULT = "select i_chronicle_id, r_modify_date from dm_sysobject where r_object_type='dm_document' and r_creator_name!='Administrator' order by r_modify_date, i_chronicle_id";
			  
			 QUERY_STRING_BOUNDED_DEFAULT = 
				 "select i_chronicle_id, r_modify_date from dm_sysobject where r_object_type='dm_document' and r_creator_name!='Administrator' and r_modify_date >= "+ 
				 "''{0}'' "+
				 "order by r_modify_date, i_chronicle_id";
		} else {
			QUERY_STRING_BOUNDED_DEFAULT = 
				"//*[@jcr:primaryType = 'nt:resource' and @jcr:lastModified >= " +
				"''{0}''] order by @jcr:lastModified, @jcr:uuid";
			
			QUERY_STRING_UNBOUNDED_DEFAULT = 
				"//*[@jcr:primaryType='nt:resource'] order by @jcr:lastModified, @jcr:uuid";
			 
		}
	}
	
	public static IQuery getIQueryObject(){
		IQuery qObj = null;
		if (isDFCavailable) {
			try {
				qObj = (IQuery) Class.forName("com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmQuery").newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				qObj = (IQuery) Class.forName("com.google.enterprise.connector.dctm.dctmmockwrap.DctmMockQuery").newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (qObj!=null) return qObj;
		else return null;//Dans un premier temps
	}
}
