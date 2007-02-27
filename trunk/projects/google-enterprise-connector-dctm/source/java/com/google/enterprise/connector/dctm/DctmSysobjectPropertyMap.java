package com.google.enterprise.connector.dctm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.google.enterprise.connector.dctm.dfcwrap.IAttr;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.ValueType;

public class DctmSysobjectPropertyMap extends HashMap implements PropertyMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = 126421624L;

	private String docid;

	private ISysObject object = null;

	private ISessionManager sessionManager = null;

	private IClientX clientX;

	private String isPublic = "false";

	private static HashSet specmeta = null;
	static {
		specmeta = new HashSet();
		specmeta.add("object_name");
		specmeta.add("r_object_type");
		specmeta.add("title");
		specmeta.add("subject");
		specmeta.add("keywords");
		specmeta.add("authors");
		specmeta.add("r_creation_date");
	}

	private static HashSet sysmeta = null;
	static {
		sysmeta = new HashSet();
		sysmeta.add("object_name");
		sysmeta.add("r_object_type");
		sysmeta.add("title");
		sysmeta.add("subject");
		sysmeta.add("keywords");
		sysmeta.add("i_vstamp");
		sysmeta.add("i_is_replica");
		sysmeta.add("i_retainer_id");
		sysmeta.add("r_aspect_name");
		sysmeta.add("i_retain_until");
		sysmeta.add("a_last_review_date");
		sysmeta.add("a_is_signed");
		sysmeta.add("a_extended_properties");
		sysmeta.add("r_full_content_size");
		sysmeta.add("a_controlling_app");
		sysmeta.add("a_is_template");
		sysmeta.add("language_code");
		sysmeta.add("a_category");
		sysmeta.add("a_effective_flag");
		sysmeta.add("a_effective_flag");
		sysmeta.add("a_effective_label");
		sysmeta.add("a_publish_formats");
		sysmeta.add("a_expiration_date");
		sysmeta.add("a_effective_date");
		sysmeta.add("r_alias_set_id");
		sysmeta.add("r_current_state");
		sysmeta.add("r_resume_state");
		sysmeta.add("r_policy_id");
		sysmeta.add("r_is_public");
		sysmeta.add("r_creator_name");
		sysmeta.add("a_special_app");
		sysmeta.add("i_is_reference");
		sysmeta.add("acl_name");
		sysmeta.add("acl_domain");
		sysmeta.add("r_has_events");
		sysmeta.add("r_frozen_flag");
		sysmeta.add("r_immutable_flag");
		sysmeta.add("i_branch_cnt");
		sysmeta.add("i_direct_dsc");
		sysmeta.add("r_version_label");
		sysmeta.add("log_entry");
		sysmeta.add("r_lock_machine");
		sysmeta.add("r_lock_date");
		sysmeta.add("r_lock_owner");
		sysmeta.add("i_latest_flag");
		sysmeta.add("i_chronicle_id");
		sysmeta.add("group_permit");
		sysmeta.add("world_permit");
		sysmeta.add("object_name");
		sysmeta.add("i_antecedent_id");
		sysmeta.add("group_name");
		sysmeta.add("owner_permit");
		sysmeta.add("owner_name");
		sysmeta.add("i_cabinet_id");
		sysmeta.add("a_storage_type");
		sysmeta.add("object_name");
		sysmeta.add("a_full_text");
		sysmeta.add("r_content_size");
		sysmeta.add("r_page_cnt");
		sysmeta.add("a_content_type");
		sysmeta.add("i_contents_id");
		sysmeta.add("r_is_virtual_doc");
		sysmeta.add("resolution_label");
		sysmeta.add("r_has_frzn_assembly");
		sysmeta.add("r_frzn_assembly_cnt");
		sysmeta.add("r_assembled_from_id");
		sysmeta.add("r_link_high_cnt");
		sysmeta.add("r_link_cnt");
		sysmeta.add("r_order_no");
		sysmeta.add("r_composite_label");
		sysmeta.add("r_component_label");
		sysmeta.add("r_composite_id");
		sysmeta.add("i_folder_id");
		sysmeta.add("i_has_folder");
		sysmeta.add("a_link_resolved");
		sysmeta.add("i_reference_cnt");
		sysmeta.add("a_compound_architecture");
		sysmeta.add("a_archive");
		sysmeta.add("i_is_deleted");
		sysmeta.add("a_retention_date");
		sysmeta.add("a_is_hidden");
		sysmeta.add("r_access_date");
		sysmeta.add("r_modifier");
		sysmeta.add("r_modify_date");
		sysmeta.add("r_creation_date");
		sysmeta.add("a_status");
		sysmeta.add("a_application_type");
	}

	public DctmSysobjectPropertyMap(String docid,
			ISessionManager sessionManager, IClientX clientX) {
		this.docid = docid;
		this.sessionManager = sessionManager;
		this.clientX = clientX;
	}

	private void fetch() throws RepositoryException {
		if (object != null) {
			return;
		}
		ISession session = null;
		try {
			String docbaseName = sessionManager.getDocbaseName();
			session = sessionManager.getSession(docbaseName);
			IId id = clientX.getId(docid);
			object = session.getObject(id);
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}
	}

	public Property getProperty(String name) throws RepositoryException {
		IFormat dctmForm = null;
		String mimetype = "";
		fetch();
		if (name.equals(SpiConstants.PROPNAME_DOCID)) {
			return new DctmSysobjectProperty(name, new DctmSysobjectValue(
					ValueType.STRING, docid));
		} else if (SpiConstants.PROPNAME_CONTENT.equals(name)) {
			return new DctmSysobjectProperty(name, new DctmSysobjectValue(
					object, "", ValueType.BINARY));
		} else if (SpiConstants.PROPNAME_DISPLAYURL.equals(name)) {
			return new DctmSysobjectProperty(name, new DctmSysobjectValue(
					ValueType.STRING, sessionManager.getServerUrl() + docid));
		} else if (SpiConstants.PROPNAME_SECURITYTOKEN.equals(name)) {
			return new DctmSysobjectProperty(name, new DctmSysobjectValue(
					ValueType.STRING, object.getACLDomain() + " "
							+ object.getACLName()));
		} else if (SpiConstants.PROPNAME_ISPUBLIC.equals(name)) {
			return new DctmSysobjectProperty(name, new DctmSysobjectValue(
					ValueType.BOOLEAN, this.isPublic));
		} else if (SpiConstants.PROPNAME_LASTMODIFY.equals(name)) {
			return new DctmSysobjectProperty(name, new DctmSysobjectValue(
					object, "r_modify_date", ValueType.DATE));
		} else if (SpiConstants.PROPNAME_MIMETYPE.equals(name)) {

			dctmForm = object.getFormat();
			mimetype = dctmForm.getMIMEType();
			return new DctmSysobjectProperty(name, new DctmSysobjectValue(
					ValueType.STRING, mimetype));
		} else if (SpiConstants.PROPNAME_SEARCHURL.equals(name)) {
			return null;
		} else if (SpiConstants.PROPNAME_AUTH_VIEWPERMIT.equals(name)) {
			return (DctmSysobjectProperty) this.get(name);
		}
		return new DctmSysobjectProperty(name, new DctmSysobjectValue(object,
				name, ValueType.STRING));
	}

	public Iterator getProperties() throws RepositoryException {
		// get the list of atrrs from the sysobject
		// for each one - check whether you want to omit it
		// add the ones you like to a
		// HashSet propNames = new HashSet();
		// propNames.add(thisone);
		// return propNames.iterator();
		fetch();
		HashSet properties = new HashSet();

		for (int i = 0; i < object.getAttrCount(); i++) {
			IAttr curAttr = object.getAttr(i);
			String name = curAttr.getName();
			if (!sysmeta.contains(name) || specmeta.contains(name)) {
				properties.add(new DctmSysobjectProperty(name,
						new DctmSysobjectValue(object, name)));
			}
		}
		return properties.iterator();
	}

	public Property putProperty(Property p) throws RepositoryException {
		if (p == null) {
			throw new IllegalArgumentException();
		}
		String name = p.getName();
		if (name == null) {
			throw new IllegalArgumentException();
		}
		return (Property) this.put(name, p);
	}

}
