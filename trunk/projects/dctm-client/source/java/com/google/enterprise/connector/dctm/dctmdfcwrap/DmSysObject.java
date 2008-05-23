package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmSysObject implements ISysObject {

	IDfSysObject idfSysObject;

	private static Logger logger = Logger.getLogger(DmSysObject.class
			.getName());
	
	public DmSysObject(IDfSysObject idfSysObject) {
		this.idfSysObject = idfSysObject;
	}

	public IFormat getFormat() throws RepositoryException {

		IDfFormat idfFormat = null;

		try {

			idfFormat = idfSysObject.getFormat();

		} catch (DfException e) {
			throw new RepositoryException(e);
		}

		return new DmFormat(idfFormat);
	}

	public long getContentSize() throws RepositoryException {
		long contentSize = 0;
		try {
			contentSize = idfSysObject.getContentSize();
		
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
		return contentSize;

	}

	public ByteArrayInputStream getContent() throws RepositoryException {
		ByteArrayInputStream content = null;

		try {
			content = idfSysObject.getContent();
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
		return content;
	}

	public String getACLDomain() throws RepositoryException {
		try {
			return idfSysObject.getACLDomain();
		} catch (DfException e) {
			throw new RepositoryException(e);
		}

	}

	public String getACLName() throws RepositoryException {
		try {
			return idfSysObject.getACLName();
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

	public String getString(String name) throws RepositoryException {
		try {
			if (name.equals("r_object_id") || name.equals("i_chronicle_id")) {
				return idfSysObject.getString(name);
			}
			if (idfSysObject.getAttrDataType(name) == IDfAttr.DM_TIME) {
				return this.getTime(name).getDate().toString();
			} else if (idfSysObject.getAttrDataType(name) == IDfAttr.DM_ID) {
				return this.getId(name).toString();
			}
			return idfSysObject.getString(name);
		} catch (DfException e) {
			// if the attribute name does not exist for the type
			if (e.getMessage().indexOf("DM_API_E_BADATTRNAME") != -1) {
				logger.finest("in the case of DM_API_E_BADATTRNAME");
				return "";
			}
			throw new RepositoryException(e);
		}

	}

	public boolean getBoolean(String name) throws RepositoryException {
		try {
			return idfSysObject.getBoolean(name);
		} catch (DfException e) {
			throw new RepositoryException(e);
		}

	}

	public double getDouble(String name) throws RepositoryException {

		try {
			return idfSysObject.getDouble(name);
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

	public IId getId(String name) throws RepositoryException {

		try {
			return new DmId(idfSysObject.getId(name));
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

	public int getInt(String name) throws RepositoryException {

		try {
			return idfSysObject.getInt(name);
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

	public ITime getTime(String name) throws RepositoryException {
		try {
			return new DmTime(idfSysObject.getTime(name));
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

	public int getAttrDataType(String name) throws RepositoryException {

		try {
			return idfSysObject.getAttrDataType(name);
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

	public int getAttrCount() throws RepositoryException {

		try {
			return idfSysObject.getAttrCount();
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

	public IAttr getAttr(int attrIndex) throws RepositoryException {

		try {
			return new DmAttr(idfSysObject.getAttr(attrIndex));
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

	public void setSessionManager(ISessionManager sessionManager)
			throws RepositoryException {

		DmSessionManager dmSessionManager = (DmSessionManager) sessionManager;
		try {
			this.idfSysObject.setSessionManager(dmSessionManager
					.getSessionManager());
		} catch (DfException e) {
			throw new RepositoryException(e);
		}

	}

	public IValue getRepeatingValue(String name, int index)
			throws RepositoryException {
		try {
			return new DmValue(idfSysObject.getRepeatingValue(name, index));
		} catch (DfException e) {
			throw new RepositoryException(e);
		}

	}

	public int findAttrIndex(String name) throws RepositoryException {
		try {
			return idfSysObject.findAttrIndex(name);
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

	public int getValueCount(String name) throws RepositoryException {

		try {
			return idfSysObject.getValueCount(name);
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
	}

}
