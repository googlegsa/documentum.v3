package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.DebugFinalData;
import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmFormatATest extends TestCase {

	DmInitialize initializer = new DmInitialize(true);

	IClientX dctmClientX;

	IClient localClient;

	ISessionManager sessionManager;

	ISession session;

	ILoginInfo loginInfo;

	String docbase;

	public void setUp() throws Exception {
		super.setUp();
		dctmClientX = (IClientX) Class.forName(DmInitialize.DM_CLIENTX)
				.newInstance();
		localClient = dctmClientX.getLocalClient();
		sessionManager = localClient.newSessionManager();
		loginInfo = dctmClientX.getLoginInfo();
		String user = DmInitialize.DM_LOGIN_OK2;

		String password = DmInitialize.DM_PWD_OK2;

		docbase = DmInitialize.DM_DOCBASE;
		loginInfo.setUser(user);
		loginInfo.setPassword(password);
		sessionManager.setIdentity(docbase, loginInfo);
	}

	public void testCanIndexExcel() throws DfException, RepositoryException {
		session = sessionManager.getSession(docbase);
		Assert.assertNotNull(session);
		Assert.assertTrue(session instanceof DmSession);
		String idString = getAnExistingExcelObjectId(session);
		IId id = dctmClientX.getId(idString);
		ISysObject object = session.getObject(id);
		IFormat dctmForm = (DmFormat) object.getFormat();
		Assert.assertNotNull(dctmForm);
		boolean rep = dctmForm.canIndex();
		Assert.assertTrue(rep);
		sessionManager.release(session);
	}

	public void testCanIndexAccess() throws DfException, RepositoryException {
		session = sessionManager.getSession(docbase);
		Assert.assertNotNull(session);
		Assert.assertTrue(session instanceof DmSession);
		String idString = getAnExistingAccessObjectId(session);
		IId id = dctmClientX.getId(idString);
		ISysObject object = session.getObject(id);
		IFormat dctmForm = (DmFormat) object.getFormat();
		Assert.assertNotNull(dctmForm);
		boolean rep = dctmForm.canIndex();
		Assert.assertFalse(rep);
		sessionManager.release(session);
	}

	public void testCanIndexPDF() throws DfException, RepositoryException {
		session = sessionManager.getSession(docbase);
		Assert.assertNotNull(session);
		Assert.assertTrue(session instanceof DmSession);
		String idString = getAnExistingPDFObjectId(session);
		IId id = dctmClientX.getId(idString);
		ISysObject object = session.getObject(id);
		IFormat dctmForm = (DmFormat) object.getFormat();
		Assert.assertNotNull(dctmForm);
		boolean rep = dctmForm.canIndex();
		Assert.assertTrue(rep);
		sessionManager.release(session);
	}

	public void testGetPDFMIMEType() throws DfException, RepositoryException {
		session = sessionManager.getSession(docbase);
		Assert.assertNotNull(session);
		Assert.assertTrue(session instanceof DmSession);
		String idString = getAnExistingPDFObjectId(session);
		IId id = dctmClientX.getId(idString);
		ISysObject object = session.getObject(id);
		IFormat dctmForm = (DmFormat) object.getFormat();
		String mimetype = dctmForm.getMIMEType();
		Assert.assertEquals(mimetype, "application/pdf");
		sessionManager.release(session);
	}

	public void testGetExcelMIMEType() throws DfException, RepositoryException {
		session = sessionManager.getSession(docbase);
		Assert.assertNotNull(session);
		Assert.assertTrue(session instanceof DmSession);
		String idString = getAnExistingExcelObjectId(session);
		IId id = dctmClientX.getId(idString);
		ISysObject object = session.getObject(id);
		IFormat dctmForm = (DmFormat) object.getFormat();
		String mimetype = dctmForm.getMIMEType();
		Assert.assertEquals(mimetype, "application/vnd.ms-excel");
		sessionManager.release(session);
	}

	public void testGetWordMIMEType() throws DfException, RepositoryException {
		session = sessionManager.getSession(docbase);
		Assert.assertNotNull(session);
		Assert.assertTrue(session instanceof DmSession);
		String idString = getAnExistingWordObjectId(session);
		IId id = dctmClientX.getId(idString);
		ISysObject object = session.getObject(id);
		IFormat dctmForm = (DmFormat) object.getFormat();
		String mimetype = dctmForm.getMIMEType();
		Assert.assertEquals(mimetype, "application/msword");
		sessionManager.release(session);
	}

	private String getAnExistingExcelObjectId(ISession session)
			throws DfException {
		String idString;
		DmSession dctmSession = (DmSession) session;
		IDfSession dfSession = dctmSession.getDfSession();
		IDfId id = dfSession
				.getIdByQualification("dm_sysobject where a_content_type = 'excel8book'");
		idString = id.toString();

		return idString;
	}

	private String getAnExistingPDFObjectId(ISession session)
			throws DfException {
		// move into real DFC to find a docid that's in this docbase
		String idString;
		DmSession dctmSession = (DmSession) session;
		IDfSession dfSession = dctmSession.getDfSession();
		IDfId id = dfSession
				.getIdByQualification("dm_sysobject where a_content_type = 'pdf'");
		idString = id.toString();
		if (DebugFinalData.debugInEclipse) {
			System.out.println("idString getAnExistingPDFObjectId vaut "
					+ idString);
		}
		return idString;

	}

	private String getAnExistingAccessObjectId(ISession session)
			throws DfException {
		// move into real DFC to find a docid that's in this docbase
		String idString;
		DmSession dctmSession = (DmSession) session;
		IDfSession dfSession = dctmSession.getDfSession();
		IDfId id = dfSession
				.getIdByQualification("dm_sysobject where a_content_type = 'ms_access7'");
		idString = id.toString();

		return idString;
	}

	private String getAnExistingWordObjectId(ISession session)
			throws DfException {
		// move into real DFC to find a docid that's in this docbase
		String idString;
		DmSession dctmSession = (DmSession) session;
		IDfSession dfSession = dctmSession.getDfSession();
		IDfId id = dfSession
				.getIdByQualification("dm_sysobject where a_content_type = 'msw8'");
		idString = id.toString();

		return idString;
	}

}
