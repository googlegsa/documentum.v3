package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.DctmResultSet;
import com.google.enterprise.connector.dctm.DctmSysobjectPropertyMap;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
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

		String crID = null;
		DctmSysobjectPropertyMap pm = null;
		DctmResultSet resu = new DctmResultSet();
		ICollection col = new DmCollection(idfCollection);
		System.out.println("--- docbasename vaut "
				+ sessionManager.getDocbaseName() + " ---");
		DmSession session = (DmSession) sessionManager
				.getSession(sessionManager.getDocbaseName());

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

}
