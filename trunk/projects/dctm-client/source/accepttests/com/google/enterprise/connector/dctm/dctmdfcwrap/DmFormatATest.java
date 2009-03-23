// Copyright (C) 2006-2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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

public class DmFormatATest extends TestCase {
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
    session = sessionManager.getSession(docbase);
  }

  public void testCanIndexExcel() throws DfException, RepositoryException {
    String idString = getAnExistingExcelObjectId(session);
    IId id = dctmClientX.getId(idString);
    ISysObject object = session.getObject(id);
    IFormat dctmForm = (DmFormat) object.getFormat();

    Assert.assertNotNull(dctmForm);
    Assert.assertTrue(dctmForm.canIndex());

  }

  public void testCanIndexAccess() throws DfException, RepositoryException {
    String idString = getAnExistingAccessObjectId(session);

    IId id = dctmClientX.getId(idString);
    ISysObject object = session.getObject(id);
    IFormat dctmForm = (DmFormat) object.getFormat();

    Assert.assertNotNull(dctmForm);
    Assert.assertFalse(dctmForm.canIndex());
  }

  public void testCanIndexPDF() throws DfException, RepositoryException {
    String idString = getAnExistingPDFObjectId(session);
    IId id = dctmClientX.getId(idString);
    ISysObject object = session.getObject(id);
    IFormat dctmForm = (DmFormat) object.getFormat();

    Assert.assertNotNull(dctmForm);
    Assert.assertTrue(dctmForm.canIndex());
  }

  public void testGetPDFMIMEType() throws DfException, RepositoryException {
    String idString = getAnExistingPDFObjectId(session);
    IId id = dctmClientX.getId(idString);
    ISysObject object = session.getObject(id);
    IFormat dctmForm = (DmFormat) object.getFormat();

    Assert.assertEquals(dctmForm.getMIMEType(), "application/pdf");
  }

  public void testGetExcelMIMEType() throws DfException, RepositoryException {
    String idString = getAnExistingExcelObjectId(session);
    IId id = dctmClientX.getId(idString);
    ISysObject object = session.getObject(id);
    IFormat dctmForm = (DmFormat) object.getFormat();

    Assert.assertEquals(dctmForm.getMIMEType(), "application/vnd.ms-excel");
  }

  public void testGetWordMIMEType() throws DfException, RepositoryException {
    String idString = getAnExistingWordObjectId(session);
    IId id = dctmClientX.getId(idString);
    ISysObject object = session.getObject(id);
    IFormat dctmForm = (DmFormat) object.getFormat();

    Assert.assertEquals(dctmForm.getMIMEType(), "application/msword");
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

  protected void tearDown() throws Exception {
    if (session != null)
      sessionManager.release(session);
  }
}
