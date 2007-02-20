package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;

import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmSysObject extends DmPersistentObject implements ISysObject {

	IDfSysObject idfSysObject;

	public DmSysObject(IDfSysObject idfSysObject) {
		super((IDfPersistentObject) idfSysObject);
		this.idfSysObject = idfSysObject;
	}

	public IFormat getFormat() throws RepositoryException {

		IDfFormat idfFormat = null;

		try {

			idfFormat = idfSysObject.getFormat();

		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}

		return new DmFormat(idfFormat);
	}

	public long getContentSize() throws RepositoryException {
		long contentSize = 0;
		try {
			contentSize = idfSysObject.getContentSize();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
		return contentSize;

	}

	public ByteArrayInputStream getContent() throws RepositoryException {
		ByteArrayInputStream content = null;
		try {
			content = idfSysObject.getContent();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
		return content;
	}

	public Enumeration enumAttrs() throws RepositoryException {
		Enumeration attrs = null;

		try {
			attrs = idfSysObject.enumAttrs();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
		if (attrs != null) {
			return attrs;
		}
		return null;
	}

	public String getACLDomain() throws RepositoryException {
		try {
			return idfSysObject.getACLDomain();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}

	}

	public String getACLName() throws RepositoryException {
		try {
			return idfSysObject.getACLName();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
	}

	public String getString(String name) throws RepositoryException {
		try {
			return idfSysObject.getString(name);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}

	}

	public boolean getBoolean(String name) throws RepositoryException {
		try {
			return idfSysObject.getBoolean(name);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}

	}

	public double getDouble(String name) throws RepositoryException {
		try {
			return idfSysObject.getDouble(name);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
	}

	public IId getId(String name) throws RepositoryException {
		try {
			return new DmId(idfSysObject.getId(name));
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
	}

	public int getInt(String name) throws RepositoryException {
		try {
			return idfSysObject.getInt(name);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
	}

	public ITime getTime(String name) throws RepositoryException {
		try {
			return new DmTime(idfSysObject.getTime(name));
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
	}

	public int getAttrDataType(String name) throws RepositoryException {
		try {
			return idfSysObject.getAttrDataType(name);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
	}

	public int getAttrCount() throws RepositoryException {
		try {
			return idfSysObject.getAttrCount();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
	}

	public IAttr getAttr(int attrIndex) throws RepositoryException {
		try {
			return new DmAttr(idfSysObject.getAttr(attrIndex));
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e);
			throw re;
		}
	}

}
