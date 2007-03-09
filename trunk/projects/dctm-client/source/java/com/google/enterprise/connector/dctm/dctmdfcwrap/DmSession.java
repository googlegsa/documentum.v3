package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmSession implements ISession {

	IDfSession idfSession;

	public DmSession(IDfSession DfSession) {
		this.idfSession = DfSession;
	}

	public ISysObject getObject(IId objectId) throws RepositoryException {

		if (!(objectId instanceof DmId)) {
			throw new IllegalArgumentException();
		}
		DmId dctmId = (DmId) objectId;

		IDfId idfId = dctmId.getidfId();

		IDfSysObject idfSysObject = null;
		try {
			idfSysObject = (IDfSysObject) idfSession.getObject(idfId);
		} catch (DfException de) {
			RepositoryException re = new RepositoryException(de);
			throw re;
		}
		return new DmSysObject(idfSysObject);
	}

	public IDfSession getDfSession() {
		return idfSession;
	}

	public void setDfSession(IDfSession dfSession) {
		idfSession = dfSession;
	}

	public String getLoginTicketForUser(String username)
			throws RepositoryException {
		String ticket = null;
		try {
			ticket = this.idfSession.getLoginTicketForUser(username);
		} catch (DfException de) {
			RepositoryException re = new RepositoryException(de);
			throw re;
		}
		return ticket;
	}

	public DmDocument newObject() throws RepositoryException {
		IDfDocument document = null;
		try {
			document = (IDfDocument) this.idfSession.newObject("dm_document");
		} catch (DfException de) {
			RepositoryException re = new RepositoryException(de);
			throw re;
		}
		return new DmDocument(document);
	}

}
