package com.google.enterprise.connector.dctm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmDocument;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmPersistentObject;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSession;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSysObject;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.manager.Context;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmInitialize {
	public static final String DM_LOGIN_OK1 = "emilie";
	public static final String DM_LOGIN_OK2 = "user1";
	public static final String DM_LOGIN_OK3 = "user2";
	public static final String DM_LOGIN_OK4 = "queryUser";
	public static final String DM_LOGIN_OK5 = "Fred";
	public static final String DM_LOGIN_K0 = "machinchouette";
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
	public static String DM_QUERY_STRING_BOUNDED_DEFAULT="select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type=''dm_document'' and r_modify_date >= "+ "''{0}'' "+"order by r_modify_date, i_chronicle_id";
	public static String DM_QUERY_STRING_UNBOUNDED_DEFAULT="select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document' " +"order by r_modify_date, i_chronicle_id";
	public static String DM_QUERY_STRING_ENABLE="select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document' " +"order by r_modify_date, i_chronicle_id ENABLE (return_top 50)";
	public static final String DM_ATTRIBUTE_NAME = "r_object_id";
	public static final String DM_QUERY_STRING_AUTHORISE_DEFAULT = "select r_object_id from dm_sysobject where r_object_id in (";
	public static final String DM_FAlSE_PWD = "false";
	
	public static String getAnExistingObjectId(ISession session) throws DfException {
		// move into real DFC to find a docid that's in this docbase
		String idString;
		DmSession dctmSession = (DmSession) session;
		IDfSession dfSession = dctmSession.getDfSession();
		IDfId id = dfSession.getIdByQualification("dm_sysobject");
		idString = id.toString();
		return idString;
	}
	
	public static String getAnExistingExcelObjectId(ISession session) throws DfException {
		String idString;
		DmSession dctmSession = (DmSession) session;
		IDfSession dfSession = dctmSession.getDfSession();
		IDfId id = dfSession.getIdByQualification("dm_sysobject where a_content_type = 'excel8book'");
		idString = id.toString();
		System.out.println("idString getAnExistingExcelObjectId vaut "+idString);
		return idString;
	}	

	public static String getAnExistingPDFObjectId(ISession session) throws DfException {
		// move into real DFC to find a docid that's in this docbase
		String idString;
		DmSession dctmSession = (DmSession) session;
		IDfSession dfSession = dctmSession.getDfSession();
		IDfId id = dfSession.getIdByQualification("dm_sysobject where a_content_type = 'pdf'");
		idString = id.toString();
		System.out.println("idString getAnExistingPDFObjectId vaut "+idString);
		return idString;
		
	}
	
	public static String getAnExistingAccessObjectId(ISession session) throws DfException {
		// move into real DFC to find a docid that's in this docbase
		String idString;
		DmSession dctmSession = (DmSession) session;
		IDfSession dfSession = dctmSession.getDfSession();
		IDfId id = dfSession.getIdByQualification("dm_sysobject where a_content_type = 'ms_access7'");
		idString = id.toString();
		System.out.println("idString getAnExistingAccessObjectId vaut "+idString);
		return idString;
	}
	
	public static String getAnExistingWordObjectId(ISession session) throws DfException {
		// move into real DFC to find a docid that's in this docbase
		String idString;
		DmSession dctmSession = (DmSession) session;
		IDfSession dfSession = dctmSession.getDfSession();
		IDfId id = dfSession.getIdByQualification("dm_sysobject where a_content_type = 'msw8'");
		idString = id.toString();
		System.out.println("idString getAnExistingAccessObjectId vaut "+idString);
		return idString;
	}
	
	public static DmDocument CreateNewDocument(ISession session) throws RepositoryException, IOException{
		DmDocument document=((DmSession)session).newObject();
		File f=new File("DocumentCreationTest.txt");
		ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject("Foundation Course Content Outline Overview Concepts The mission of Google and Google Enterprise");
		document.setFileEx("DocumentCreationTest.txt","text");
		document.setObjectName("Document creation test");
		document.save();
		oos.close();
		boolean del=f.delete();
		System.out.println("del vaut "+del);
		return document;
	}
	
	public static void deleteDocument(DmDocument document) throws RepositoryException{
		document.destroyAllVersions();
	}
	
}

