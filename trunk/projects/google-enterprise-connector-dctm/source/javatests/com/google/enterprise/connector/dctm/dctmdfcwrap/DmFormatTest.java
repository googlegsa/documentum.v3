package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
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

public class DmFormatTest extends TestCase {

	IClientX dctmClientX;
	IClient localClient;
	ISessionManager sessionManager; 
	ISession session;
	ILoginInfo loginInfo;
	
	public void setUp() throws Exception{
		super.setUp();
		dctmClientX = new DmClientX();
		localClient = dctmClientX.getLocalClient();
		sessionManager = localClient.newSessionManager();
		loginInfo = dctmClientX.getLoginInfo();
		String user=DmInitialize.DM_LOGIN_OK1;
		String password=DmInitialize.DM_PWD_OK1;
		String docbase=DmInitialize.DM_DOCBASE;
		loginInfo.setUser(user);
		loginInfo.setPassword(password);
		sessionManager.setIdentity(docbase, loginInfo);
		session = sessionManager.getSession(docbase);
		Assert.assertNotNull(session);
		Assert.assertTrue(session instanceof DmSession);	
		
	}
	

	
	public void testCanIndexExcel() throws DfException, RepositoryException{
		String idString = DmInitialize.getAnExistingExcelObjectId(session);
		IId id = dctmClientX.getId(idString);
		ISysObject object = session.getObject(id);
		IFormat dctmForm = (DmFormat) object.getFormat();
		Assert.assertNotNull(dctmForm);
		boolean rep=dctmForm.canIndex();
		Assert.assertTrue(rep);
	}

	public void testCanIndexAccess() throws DfException, RepositoryException{
		String idString = DmInitialize.getAnExistingAccessObjectId(session);
		IId id = dctmClientX.getId(idString);
		ISysObject object = session.getObject(id);
		IFormat dctmForm = (DmFormat) object.getFormat();
		Assert.assertNotNull(dctmForm);
		boolean rep=dctmForm.canIndex();
		Assert.assertFalse(rep);
	}
	
	public void testCanIndexPDF() throws DfException, RepositoryException{
		String idString = DmInitialize.getAnExistingPDFObjectId(session);
		IId id = dctmClientX.getId(idString);
		ISysObject object = session.getObject(id);
		IFormat dctmForm = (DmFormat) object.getFormat();
		Assert.assertNotNull(dctmForm);
		boolean rep=dctmForm.canIndex();
		Assert.assertTrue(rep);
	}
	
	public void testGetPDFMIMEType() throws DfException, RepositoryException{
		String idString = DmInitialize.getAnExistingPDFObjectId(session);
		IId id = dctmClientX.getId(idString);
		ISysObject object = session.getObject(id);
		IFormat dctmForm = (DmFormat) object.getFormat();
		String mimetype=dctmForm.getMIMEType();
		Assert.assertEquals(mimetype,"application/pdf");
	}
	
	public void testGetExcelMIMEType() throws DfException, RepositoryException{
		String idString = DmInitialize.getAnExistingExcelObjectId(session);
		IId id = dctmClientX.getId(idString);
		ISysObject object = session.getObject(id);
		IFormat dctmForm = (DmFormat) object.getFormat();
		String mimetype=dctmForm.getMIMEType();
		Assert.assertEquals(mimetype,"application/vnd.ms-excel");
	}	
	
	
	public void testGetWordMIMEType() throws DfException, RepositoryException{
		String idString = DmInitialize.getAnExistingWordObjectId(session);
		IId id = dctmClientX.getId(idString);
		ISysObject object = session.getObject(id);
		IFormat dctmForm = (DmFormat) object.getFormat();
		String mimetype=dctmForm.getMIMEType();
		Assert.assertEquals(mimetype,"application/msword");
	}
	
	
	
}
