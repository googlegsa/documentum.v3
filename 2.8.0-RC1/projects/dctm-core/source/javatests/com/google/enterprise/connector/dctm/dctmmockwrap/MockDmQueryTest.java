// Copyright 2007 Google Inc.
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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.text.MessageFormat;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

public class MockDmQueryTest extends TestCase {
  private static final String XPATH_QUERY_STRING_BOUNDED_DEFAULT = "//*[@jcr:primaryType = 'nt:resource' and @jcr:lastModified >= ''{0}'' and @jcr:uuid >= ''{1}''] order by @jcr:lastModified, @jcr:uuid";

  IClientX dctmClientX;

  IClient localClient;

  ISessionManager sessionManager;

  ISession sess7;

  IQuery query;

  public void setUp() throws Exception {
    super.setUp();

    dctmClientX = new MockDmClientX();
    localClient = dctmClientX.getLocalClient();
    sessionManager = localClient.newSessionManager();
    ILoginInfo ili = new MockDmLoginInfo();
    ili.setUser("mark");
    ili.setPassword("mark");
    sessionManager.setIdentity("MockRepositoryEventLog7.txt", ili);
    sess7 = sessionManager.getSession("MockRepositoryEventLog7.txt");
    query = localClient.getQuery();
  }

  public void testMakeBoundedQuery() {
    String dqlStatement = "select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document' and r_modify_date >= 'ThisIsATestDate' and i_chronicle_id >= 'ThisIsATestId'";
    int bound1 = dqlStatement.indexOf(" and r_modify_date >= '")
        + " and r_modify_date >= '".length();
    int bound2 = dqlStatement.indexOf("' and i_chronicle_id >= '");
    int bound3 = bound2 + "' and i_chronicle_id >= '".length();
    String date = dqlStatement.substring(bound1, bound2);
    String id = dqlStatement.substring(bound3, dqlStatement
        .lastIndexOf("'"));
    String test = MessageFormat.format(XPATH_QUERY_STRING_BOUNDED_DEFAULT,
        new Object[] { date, id });
    assertEquals(
        test,
        "//*[@jcr:primaryType = nt:resource and @jcr:lastModified >= 'ThisIsATestDate' and @jcr:uuid >= 'ThisIsATestId'] order by @jcr:lastModified, @jcr:uuid");
  }

  public void testSetDQLExecute() {
    query.setDQL(DmInitialize.DM_QUERY_STRING_ENABLE);
    ICollection collec = null;
    try {
      collec = query.execute(sess7, IQuery.READ_QUERY);
    } catch (RepositoryException e) {
      fail(e.getMessage());
    }
    assertNotNull(collec);
  }
}
