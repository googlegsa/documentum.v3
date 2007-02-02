package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.util.Vector;

import com.google.enterprise.connector.dctm.DctmResultSet;
import com.google.enterprise.connector.dctm.DctmSysobjectPropertyMap;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ITypedObject;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfValue;

public class DmCollection extends DmTypedObject implements ICollection {

	IDfCollection idfCollection;

	public DmCollection(IDfCollection idfCollection) {
		super(idfCollection);

		this.idfCollection = idfCollection;

	}

	public IValue getValue(String attrName) throws RepositoryException {
		IDfValue dfValue = null;
		try {
			dfValue = idfCollection.getValue(attrName);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
		return new DmValue(dfValue);
	}

	public boolean next() throws RepositoryException {
		boolean rep = false;
		try {
			rep = idfCollection.next();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
		return (rep);
	}

	public ITypedObject getTypedObject() throws RepositoryException {
		IDfTypedObject dfTypedObj = null;
		try {
			dfTypedObj = idfCollection.getTypedObject();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
		return new DmTypedObject(dfTypedObj);
	}

	public IDfCollection getIDfCollection() {
		return idfCollection;
	}

	public String getString(String colName) throws RepositoryException {
		try {
			return this.idfCollection.getString(colName);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e.getMessage(), e
					.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
	}

	public ResultSet buildResulSetFromCollection(
			ISessionManager sessionManager, IClientX clientX)
			throws RepositoryException {
		System.out.println("--- DmCollection buildResulSetFromCollection ---");
		String modifDate = null;
		String crID = null;
		String mimetype = null;
		DctmSysobjectPropertyMap pm = null;
		DmSysObject dctmSysObj = null;
		IFormat dctmForm = null;
		DmValue val = null;
		DctmResultSet resu = new DctmResultSet();
		ICollection col = new DmCollection(idfCollection);

		System.out.println("--- docbasename vaut "
				+ sessionManager.getDocbaseName() + " ---");
		DmSession session = (DmSession) sessionManager
				.getSession(sessionManager.getDocbaseName());

		Vector notCustomMeta = getSysMeta();
		Vector specifiedMeta = getSpecMeta();

		System.out
				.println("--- DmCollection buildResulSetFromCollection after getSpecMeta---");
		while (col.next()) {
			System.out
					.println("--- DmCollection buildResulSetFromCollection in while---");

			crID = col.getValue("r_object_id").asString();

			pm = new DctmSysobjectPropertyMap(crID, sessionManager, clientX);

			resu.add(pm);
		}
		sessionManager.release(session);
		return resu;
	}

	private Vector getSpecMeta() {
		// System.out.println("--- DmCollection getSpecMeta ---");
		Vector specProps = new Vector();
		specProps.addElement("object_name");
		specProps.addElement("r_object_type");
		specProps.addElement("title");
		specProps.addElement("subject");
		specProps.addElement("keywords");
		specProps.addElement("authors");
		specProps.addElement("r_creation_date");
		return specProps;
	}

	private Vector getSysMeta() {
		// System.out.println("--- DmCollection getSysMeta ---");
		Vector sysObjectProps = new Vector();
		sysObjectProps.addElement("object_name");
		sysObjectProps.addElement("r_object_type");
		sysObjectProps.addElement("title");
		sysObjectProps.addElement("subject");
		sysObjectProps.addElement("keywords");
		sysObjectProps.addElement("authors");
		sysObjectProps.addElement("i_vstamp");
		sysObjectProps.addElement("i_is_replica");
		sysObjectProps.addElement("i_retainer_id");
		sysObjectProps.addElement("r_aspect_name");
		sysObjectProps.addElement("i_retain_until");
		sysObjectProps.addElement("a_last_review_date");
		sysObjectProps.addElement("a_is_signed");
		sysObjectProps.addElement("a_extended_properties");
		sysObjectProps.addElement("r_full_content_size");
		sysObjectProps.addElement("a_controlling_app");
		sysObjectProps.addElement("a_is_template");
		sysObjectProps.addElement("language_code");
		sysObjectProps.addElement("a_category");
		sysObjectProps.addElement("a_effective_flag");
		sysObjectProps.addElement("a_effective_flag");
		sysObjectProps.addElement("a_effective_label");
		sysObjectProps.addElement("a_publish_formats");
		sysObjectProps.addElement("a_expiration_date");
		sysObjectProps.addElement("a_effective_date");
		sysObjectProps.addElement("r_alias_set_id");
		sysObjectProps.addElement("r_current_state");
		sysObjectProps.addElement("r_resume_state");
		sysObjectProps.addElement("r_policy_id");
		sysObjectProps.addElement("r_is_public");
		sysObjectProps.addElement("r_creator_name");
		sysObjectProps.addElement("a_special_app");
		sysObjectProps.addElement("i_is_reference");
		sysObjectProps.addElement("acl_name");
		sysObjectProps.addElement("acl_domain");
		sysObjectProps.addElement("r_has_events");
		sysObjectProps.addElement("r_frozen_flag");
		sysObjectProps.addElement("r_immutable_flag");
		sysObjectProps.addElement("i_branch_cnt");
		sysObjectProps.addElement("i_direct_dsc");
		sysObjectProps.addElement("r_version_label");
		sysObjectProps.addElement("log_entry");
		sysObjectProps.addElement("r_lock_machine");
		sysObjectProps.addElement("r_lock_date");
		sysObjectProps.addElement("r_lock_owner");
		sysObjectProps.addElement("i_latest_flag");
		sysObjectProps.addElement("i_chronicle_id");
		sysObjectProps.addElement("group_permit");
		sysObjectProps.addElement("world_permit");
		sysObjectProps.addElement("object_name");
		sysObjectProps.addElement("i_antecedent_id");
		sysObjectProps.addElement("group_name");
		sysObjectProps.addElement("owner_permit");
		sysObjectProps.addElement("owner_name");
		sysObjectProps.addElement("i_cabinet_id");
		sysObjectProps.addElement("a_storage_type");
		sysObjectProps.addElement("object_name");
		sysObjectProps.addElement("a_full_text");
		sysObjectProps.addElement("r_content_size");
		sysObjectProps.addElement("r_page_cnt");
		sysObjectProps.addElement("a_content_type");
		sysObjectProps.addElement("i_contents_id");
		sysObjectProps.addElement("r_is_virtual_doc");
		sysObjectProps.addElement("resolution_label");
		sysObjectProps.addElement("r_has_frzn_assembly");
		sysObjectProps.addElement("r_frzn_assembly_cnt");
		sysObjectProps.addElement("r_assembled_from_id");
		sysObjectProps.addElement("r_link_high_cnt");
		sysObjectProps.addElement("r_link_cnt");
		sysObjectProps.addElement("r_order_no");
		sysObjectProps.addElement("r_composite_label");
		sysObjectProps.addElement("r_component_label");
		sysObjectProps.addElement("r_composite_id");
		sysObjectProps.addElement("i_folder_id");
		sysObjectProps.addElement("i_has_folder");
		sysObjectProps.addElement("a_link_resolved");
		sysObjectProps.addElement("i_reference_cnt");
		sysObjectProps.addElement("a_compound_architecture");
		sysObjectProps.addElement("a_archive");
		sysObjectProps.addElement("i_is_deleted");
		sysObjectProps.addElement("a_retention_date");
		sysObjectProps.addElement("a_is_hidden");
		sysObjectProps.addElement("r_access_date");
		sysObjectProps.addElement("r_modifier");
		sysObjectProps.addElement("r_modify_date");
		sysObjectProps.addElement("r_creation_date");
		sysObjectProps.addElement("a_status");
		sysObjectProps.addElement("a_application_type");
		return sysObjectProps;
	}

	// public ResultSet buildResulSetFromCollection(ISession session) {
	// String modifDate=null;
	// String crID=null;
	// String mimetype=null;
	// DctmValue vlDate=null;
	// DctmValue vlID=null;
	// DctmValue vlMime=null;
	// DctmPropertyMap pm=null;
	// ByteArrayInputStream content=null;
	// int size=0;
	// byte[] bufContent;
	// //ISession dctmSes = getIdctmses();
	// ISysObject dctmSysObj = null;
	// IFormat dctmForm = null;
	// DmValue val=null;
	// ITime itime=null;
	// DctmResultSet resu=new DctmResultSet();
	// //Building the DmCollection for error management only
	// ICollection col = new DmCollection(idfCollection);
	// try{
	// while (col.next()){
	// pm=new DctmPropertyMap();
	// crID = col.getValue("i_chronicle_id").asString();
	// int rep_Id=col.getValue("i_chronicle_id").getDataType();
	// vlID=new DctmValue(ValueType.STRING,crID);
	// pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_DOCID,vlID));
	//	
	// val=(DmValue)col.getValue("r_modify_date");
	// int rep=val.getDataType();
	// itime=val.asTime();
	// modifDate = itime.asString(DmTime.DF_TIME_PATTERN45);
	// Date mydate=itime.getDate();
	// ///System.out.println("modifdate vaut "+modifDate);
	// vlDate=new DctmValue(ValueType.DATE,modifDate);
	// pm.putProperty(new
	// DctmProperty(SpiConstants.PROPNAME_LASTMODIFY,vlDate));
	// dctmSysObj =
	// (DmSysObject)session.getObjectByQualification("dm_document where
	// i_chronicle_id = '" + crID + "'");
	// dctmForm = (DmFormat)dctmSysObj.getFormat();
	// if(dctmForm.canIndex()){
	// content=dctmSysObj.getContent();
	// mimetype=dctmForm.getMIMEType();
	// size=new Long(dctmSysObj.getContentSize()).intValue();
	// bufContent = new byte[size];
	// ByteArrayOutputStream output=new ByteArrayOutputStream();
	// try{
	// int count=-2;
	// while ((count = content.read(bufContent)) > -1){
	// output.write(bufContent, 0, count);
	// }
	// content.close();
	// }catch(IOException ie){
	// System.out.println(ie.getMessage());
	// }
	//	
	// DctmValue vlCont=null;
	// if(bufContent.length>0){
	// vlCont=new DctmValue(ValueType.BINARY,bufContent);
	// pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_CONTENT,vlCont));
	// }else{
	// vlCont=new DctmValue(ValueType.BINARY,"");
	// pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_CONTENT,vlCont));
	// }
	// }
	// vlMime=new DctmValue(ValueType.STRING,mimetype);
	// pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_MIMETYPE,vlMime));
	// resu.add(pm);
	// }
	// }catch(RepositoryException re){
	// System.out.println(re.getMessage());
	// }
	// int nb=resu.size();
	// System.out.println("nb vaut "+nb);
	// return resu;
	// }
	// }
}
