package com.google.enterprise.connector.dctm;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spiimpl.BinaryValue;
import com.google.enterprise.connector.spiimpl.BooleanValue;
import com.google.enterprise.connector.spiimpl.DoubleValue;
import com.google.enterprise.connector.spiimpl.LongValue;
import com.google.enterprise.connector.spiimpl.StringValue;

public class DctmSysobjectDocument extends HashMap implements Document {

	private static final long serialVersionUID = 126421624L;

	private String docId;

	private ISysObject object = null;

	private ISessionManager sessionManager = null;

	private IClientX clientX;

	private String isPublic = "false";

	private String versionId;

	private HashSet included_meta;

	private HashSet excluded_meta;

	private String object_id_name = "r_object_id";

	public DctmSysobjectDocument(String docid, ISessionManager sessionManager,
			IClientX clientX, String isPublic, HashSet included_meta,
			HashSet excluded_meta) {
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

	public Property findProperty(String name) throws RepositoryException {
		IFormat dctmForm = null;
		String mimetype = "";
		fetch();
		HashSet hashSet;
		hashSet = new HashSet();
		if (name.equals(SpiConstants.PROPNAME_DOCID)) {
			hashSet.add(new StringValue(versionId));
			return new DctmSysobjectProperty(name, hashSet);
		} else if (SpiConstants.PROPNAME_CONTENT.equals(name)) {
			hashSet.add(new BinaryValue(object.getContent()));
			return new DctmSysobjectProperty(name, hashSet);
		} else if (SpiConstants.PROPNAME_DISPLAYURL.equals(name)) {
			hashSet.add(new StringValue(sessionManager.getServerUrl() + docId));
			return new DctmSysobjectProperty(name, hashSet);
		} else if (SpiConstants.PROPNAME_SECURITYTOKEN.equals(name)) {
			hashSet.add(new StringValue(object.getACLDomain() + " "
					+ object.getACLName()));
			return new DctmSysobjectProperty(name, hashSet);
		} else if (SpiConstants.PROPNAME_ISPUBLIC.equals(name)) {
			hashSet.add(BooleanValue.getBooleanValue(this.isPublic.equals("true")));
			return new DctmSysobjectProperty(name, hashSet);
		} else if (SpiConstants.PROPNAME_LASTMODIFIED.equals(name)) {
			hashSet.add(new DctmDateValue(getDate("r_modify_date")));
			return new DctmSysobjectProperty(name, hashSet);
		} else if (SpiConstants.PROPNAME_MIMETYPE.equals(name)) {
			dctmForm = object.getFormat();
			mimetype = dctmForm.getMIMEType();
			hashSet.add(new StringValue(mimetype));
			return new DctmSysobjectProperty(name, hashSet);
		} else if (SpiConstants.PROPNAME_SEARCHURL.equals(name)) {
			return null;
		} else if (object_id_name.equals(name)) {
			hashSet.add(new StringValue(docId));
			return new DctmSysobjectProperty(name, hashSet);
		}

		IAttr attr = object.getAttr(object.findAttrIndex(name));
		int i = object.getValueCount(name);
		IValue val = null;
		for (int j = 0; j < i; j++) {
			val = object.getRepeatingValue(name, j);
			if (attr.getDataType() == IAttr.DM_BOOLEAN) {
				hashSet.add(BooleanValue.getBooleanValue(val.asBoolean()));
			} else if (attr.getDataType() == IAttr.DM_DOUBLE) {
				hashSet.add(new DoubleValue(val.asDouble()));
			} else if (attr.getDataType() == IAttr.DM_ID) {
				hashSet.add(new StringValue(object.getId(name).getId()));
			} else if (attr.getDataType() == IAttr.DM_INTEGER) {
				hashSet.add(new LongValue(val.asInteger()));
			} else if (attr.getDataType() == IAttr.DM_STRING) {
				hashSet.add(new StringValue(val.asString()));
			} else if (attr.getDataType() == IAttr.DM_TIME) {
				hashSet.add(new DctmDateValue(getCalendarFromDate(val.asTime()
						.getDate())));
			}
		}
		return new DctmSysobjectProperty(name, hashSet);

	}

	private Calendar getCalendarFromDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public Set getPropertyNames() throws RepositoryException {
		fetch();
		HashSet properties = new HashSet();

		for (int i = 0; i < object.getAttrCount(); i++) {
			IAttr curAttr = object.getAttr(i);
			String name = curAttr.getName();
			if (!excluded_meta.contains(name) || included_meta.contains(name)) {
				properties.add(name);
			}
		}
		return properties;
	}

	public Calendar getDate(String name) throws IllegalArgumentException,
			RepositoryException {

		Date date = object.getTime(name).getDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
}
