package com.google.enterprise.connector.dctm;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSession;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;

public class DctmSysobjectValueTest extends TestCase {
	
	public void testTraversal() throws LoginException, RepositoryException, DfException {
		IClientX clientX = new DmClientX();
		IClient localClient = clientX.getLocalClient();
		
		ISessionManager sessionManager = localClient.newSessionManager();
		
		String user="queryUser";
		String password="p@ssw0rd";
		String docbase="gsadctm";
		
		ILoginInfo loginInfo = localClient.getLoginInfo();
		loginInfo.setUser(user);
		loginInfo.setPassword(password);
		
		sessionManager.setIdentity(docbase, loginInfo);
		
		ISession session = null;
		try {
			session = sessionManager.getSession(docbase);
			String idString = getAnExistingObjectId(session);
			System.out.println("idString " + idString);
			IId id = clientX.getId(idString);
			ISysObject object = session.getObject(id);
			
			DctmSysobjectValue value = new DctmSysobjectValue(object, "r_object_id");
			Assert.assertEquals(idString, value.getString());
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}	
	}
	
	private String getAnExistingObjectId(ISession session) throws DfException {
		// move into real DFC to find a docid that's in this docbase
		String idString;
		DmSession dctmSession = (DmSession) session;
		IDfSession dfSession = dctmSession.getDfSession();
		IDfId id = dfSession.getIdByQualification("dm_sysobject");
		idString = id.toString();
		return idString;
	}	
	
}

