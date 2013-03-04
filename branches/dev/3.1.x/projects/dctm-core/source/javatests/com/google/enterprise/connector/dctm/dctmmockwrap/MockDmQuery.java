// Copyright 2006 Google Inc.
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.mock.jcr.MockJcrQueryManager;
import com.google.enterprise.connector.mock.jcr.MockJcrQueryResult;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.spi.RepositoryException;

public class MockDmQuery implements IQuery {
  /* FIXME: Should these be in DmInitialize? */
  public static final String TRUE_WHERE_CLAUSE = "1=1";

  /*
   * For easier implementation, this should be a superstring of
   * TRUE_WHERE_CLAUSE.
   */
  public static final String ALT_TRUE_WHERE_CLAUSE = "11=11";

  public static final String FALSE_WHERE_CLAUSE = "1=0";

  private static final String XPATH_QUERY_STRING_UNBOUNDED_DEFAULT = "//*[@jcr:primaryType='nt:resource'] order by @jcr:lastModified, @jcr:uuid";

  private static final String XPATH_QUERY_STRING_BOUNDED_DEFAULT = "//*[@jcr:primaryType = 'nt:resource' and @jcr:lastModified >= ''{0}''] order by @jcr:lastModified, @jcr:uuid";

  private String query;

  public MockDmQuery() {
    query = "";
  }

  public void setDQL(String dqlStatement) {
    String goodQuery = "";
    if (dqlStatement.indexOf(
            "select i_chronicle_id, r_object_id, r_modify_date from ") != -1) {
      if (dqlStatement.indexOf(" r_modify_date > ") != -1) {
        goodQuery = makeBoundedQuery(dqlStatement);
      } else {
        goodQuery = XPATH_QUERY_STRING_UNBOUNDED_DEFAULT;
      }
      this.query = goodQuery;
    } else {
      this.query = dqlStatement; // Will be parsed later.
    }
  }

  private String makeBoundedQuery(String dqlStatement) {
    int bound1 = dqlStatement.indexOf("r_modify_date = date('")
        + "r_modify_date = date('".length();
    int bound2 = dqlStatement.indexOf("','yyyy-mm-dd hh:mi:ss')  and");
    int bound3 = bound2 + "','yyyy-mm-dd hh:mi:ss')  and".length();

    String date = dqlStatement.substring(bound1, bound2);

    SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    String formattedDate = "";
    try {
      Date d1 = df.parse(date);

      MockDmTime dateTime = new MockDmTime(d1);
      formattedDate = dateTime.getFormattedDate();
    } catch (ParseException e) {
      // TODO: Why is this exception ignored?
    }

    String id = dqlStatement.substring(bound3, dqlStatement
        .lastIndexOf("'"));
    return MessageFormat.format(XPATH_QUERY_STRING_BOUNDED_DEFAULT,
        new Object[] { formattedDate, id });
  }

  public ICollection execute(ISession session, int queryType)
      throws RepositoryException {
    if (query.equals("") || query.indexOf("dm_audittrail") != -1) {
      return null;
    } else if (query.startsWith(XPATH_QUERY_STRING_UNBOUNDED_DEFAULT
        .substring(0, 15))) {
      try {
        MockRepositoryDocumentStore a = null;
        a = ((MockDmSession) session).getStore();
        MockJcrQueryManager mrQueryMger = new MockJcrQueryManager(a);

        Query q = mrQueryMger.createQuery(this.query, "xpath");

        QueryResult qr = q.execute();
        MockDmCollection co = new MockDmCollection(qr);
        return co;
      } catch (javax.jcr.RepositoryException e) {
        throw new RepositoryException(e);
      }
    } else if (query.indexOf("return_top 1") != -1) { // WHERE clause test...
      return new MockBooleanCollection(query.indexOf(TRUE_WHERE_CLAUSE) != -1);
    } else if (query.indexOf("dm_group") != -1) {
      String[] ids = {};
      List<MockRepositoryDocument> emptyResults =
          new MockMockList(ids, session.getSessionManager(),
              session.getDocbaseName());
      QueryResult qr = new MockJcrQueryResult(emptyResults);
      return new MockDmCollection(qr);
    } else { // Authorize query...
      String[] ids = this.query.split("','");
      ids[0] = ids[0].substring(ids[0].lastIndexOf("'") + 1, ids[0]
          .length());
      ids[ids.length - 1] = ids[ids.length - 1].substring(0,
          ids[ids.length - 1].length() - 2);

      List<MockRepositoryDocument> filteredResults =
          new MockMockList(ids, session.getSessionManager(),
              session.getDocbaseName());
      if (filteredResults != null) {
        QueryResult filteredQR = new MockJcrQueryResult(filteredResults);
        MockDmCollection finalCollection = new MockDmCollection(
            filteredQR);
        return finalCollection;
      } else {
        return null; // null value is tested in DctmAuthorizationManager
        // and won't lead to any NullPointerException
      }
    }
  }
}
