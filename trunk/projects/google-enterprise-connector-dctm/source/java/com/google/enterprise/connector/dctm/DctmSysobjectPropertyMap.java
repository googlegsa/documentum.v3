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

	private static final long serialVersionUID = 126421624L;

	private String docId;

	private ISysObject object = null;

	private ISessionManager sessionManager = null;

	private IClientX clientX;

	private String isPublic = "false";

	private String versionId;

	private HashSet included_meta;

	private HashSet excluded_meta;

	public DctmSysobjectPropertyMap(String docid,
			ISessionManager sessionManager, IClientX clientX, String isPublic,
			HashSet included_meta, HashSet excluded_meta) {
		this.docId = docid;
		this.sessionManager = sessionManager;
		this.clientX = clientX;
		this.isPublic = isPublic;
		this.included_meta = included_meta;
		this.excluded_meta = excluded_meta;
	}

	private void fetch() throws RepositoryException {

		if (object != null) {
			return;
		}
		ISession session = null;
		try {
			String docbaseName = sessionManager.getDocbaseName();
			session = sessionManager.getSession(docbaseName);

			IId id = clientX.getId(docId);
			object = session.getObject(id);
			versionId = object.getId("i_chronicle_id").getId();
			object.setSessionManager(sessionManager);
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
					ValueType.STRING, versionId));
		} else if (SpiConstants.PROPNAME_CONTENT.equals(name)) {
			return new DctmSysobjectProperty(name, new DctmSysobjectValue(
					object, "", ValueType.BINARY));
		} else if (SpiConstants.PROPNAME_DISPLAYURL.equals(name)) {
			return new DctmSysobjectProperty(name, new DctmSysobjectValue(
					ValueType.STRING, sessionManager.getServerUrl() + docId));
		} else if (SpiConstants.PROPNAME_SECURITYTOKEN.equals(name)) {
			return new DctmSysobjectProperty(name, new DctmSysobjectValue(
					ValueType.STRING, object.getACLDomain() + " "
							+ object.getACLName()));
		} else if (SpiConstants.PROPNAME_ISPUBLIC.equals(name)) {
			return new DctmSysobjectProperty(name, new DctmSysobjectValue(
					ValueType.BOOLEAN, this.isPublic));
		} else if (SpiConstants.PROPNAME_LASTMODIFIED.equals(name)) {
			return new DctmSysobjectProperty(name, new DctmSysobjectValue(
					object, "r_modify_date", ValueType.DATE));
		} else if (SpiConstants.PROPNAME_MIMETYPE.equals(name)) {
			dctmForm = object.getFormat();
			mimetype = dctmForm.getMIMEType();
			return new DctmSysobjectProperty(name, new DctmSysobjectValue(
					ValueType.STRING, mimetype));
		} else if (SpiConstants.PROPNAME_SEARCHURL.equals(name)) {
			return null;
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
		DctmSysobjectProperty dctmProps = null;
		for (int i = 0; i < object.getAttrCount(); i++) {
			IAttr curAttr = object.getAttr(i);
			String name = curAttr.getName();
			if (!excluded_meta.contains(name) || included_meta.contains(name)) {
				dctmProps = new DctmSysobjectProperty(name,
						new DctmSysobjectValue(object, name));
				if (dctmProps.getValue().getString() != null
						&& !dctmProps.getValue().getString().equals(""))
					properties.add(dctmProps);
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
