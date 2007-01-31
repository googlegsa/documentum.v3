package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmSession implements ISession {

	IDfSession idfSession;

	public DmSession(IDfSession DfSession) {
		this.idfSession = DfSession;
	}

	public ISysObject getObject(IId objectId) throws RepositoryException {
		System.out.println("--- DmSession getObject ---");
		if (!(objectId instanceof DmId)) {
			throw new IllegalArgumentException();
		}
		DmId dctmId = (DmId) objectId;
		System.out.println("--- DmSession getObject avant dctmId.getidfId() ---");
		IDfId idfId = dctmId.getidfId();
		System.out.println("--- DmSession getObject - idfId vaut "+idfId.getId()+" ---");
		IDfSysObject idfSysObject = null;
		try {
			idfSysObject = (IDfSysObject) idfSession.getObject(idfId);
		} catch (DfException de) {
			RepositoryException re = new RepositoryException(de);
			re.setStackTrace(de.getStackTrace());
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

	public String getLoginTicketForUser(String username) throws RepositoryException {
		String ticket = null;
		try {
			ticket = this.idfSession.getLoginTicketForUser(username);
		} catch (DfException de) {
			RepositoryException re = new RepositoryException(de);
			re.setStackTrace(de.getStackTrace());
			throw re;
		}
		return ticket;
	}


}
