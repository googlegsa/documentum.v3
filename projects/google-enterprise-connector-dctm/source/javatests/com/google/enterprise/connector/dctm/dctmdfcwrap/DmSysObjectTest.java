package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;
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

public class DmSysObjectTest extends TestCase {

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
	
	public void testGetFormat() throws DfException,RepositoryException{
		String idString=DmInitialize.getAnExistingPDFObjectId(session);
		IId id = dctmClientX.getId(idString);
		ISysObject object = session.getObject(id);
		IFormat dctmForm = (DmFormat) object.getFormat();
		String mimetype=dctmForm.getMIMEType();
		Assert.assertEquals(mimetype,"application/pdf");
	}

	
	public void testGetContentSize() throws DfException, RepositoryException, IOException{
	
		DmDocument document=DmInitialize.CreateNewDocument(session);
		long size = ((DmSysObject)document).getContentSize();
		Assert.assertEquals(size,102);
		DmInitialize.deleteDocument(document);
		
	}

	public void testGetContent() throws DfException, RepositoryException, IOException{
		
		DmDocument document=DmInitialize.CreateNewDocument(session);
		ByteArrayInputStream content = ((DmSysObject)document).getContent();
		Assert.assertNotNull(content);
		DmInitialize.deleteDocument(document);
		
		
	}

	
	public void testEnumAttrs() throws DfException, RepositoryException, IOException{
		DmDocument document=DmInitialize.CreateNewDocument(session);
		Enumeration attrs = ((DmSysObject)document).enumAttrs();
		Assert.assertNotNull(attrs);
		while (attrs.hasMoreElements()){
			IDfAttr curAttr = (IDfAttr) attrs.nextElement();
			String name = curAttr.getName();
			System.out.println("name vaut "+name);
			if (name.equals("object_name")){
				String object_name=document.getString("object_name");
				Assert.assertEquals(object_name,"Document creation test");
				//break;
			}
			//break;
		}
		DmInitialize.deleteDocument(document);
	}

	
	public void testGetACLDomain() throws DfException, RepositoryException, IOException{
		DmDocument document=DmInitialize.CreateNewDocument(session);
		String ACLDomain = ((DmSysObject)document).getACLDomain();
		System.out.println("acldomain vaut "+ACLDomain);
		Assert.assertNotNull(ACLDomain);
		Assert.assertEquals(ACLDomain,DmInitialize.DM_LOGIN_OK1);
		DmInitialize.deleteDocument(document);
	}

	
	public void testGetACLName() throws DfException, RepositoryException, IOException{
		DmDocument document=DmInitialize.CreateNewDocument(session);
		String ACLName = ((DmSysObject)document).getACLName();
		System.out.println("aclname vaut "+ACLName);
		Assert.assertNotNull(ACLName);
		DmInitialize.deleteDocument(document);
	}


}
