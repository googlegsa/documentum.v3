package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.util.List;

import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.mock.MockRepositoryDocumentStore;
import com.google.enterprise.connector.mock.jcr.MockJcrQueryManager;
import com.google.enterprise.connector.mock.jcr.MockJcrQueryResult;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;

public class MockDmQuery implements IQuery {
	private String query;

	private static final String XPATH_QUERY_STRING_UNBOUNDED_DEFAULT = "//*[@jcr:primaryType='nt:resource'] order by @jcr:lastModified, @jcr:uuid";

	public MockDmQuery() {
		query = "";
	}

	public ICollection execute(ISessionManager sessionManager, int queryType)
			throws RepositoryException {
		if (query.equals("")) {
			return null;
		} else if (query.startsWith(XPATH_QUERY_STRING_UNBOUNDED_DEFAULT
				.substring(0, 15))) {
			try {
				MockRepositoryDocumentStore a = null;
				a = ((MockDmSession) sessionManager.getSession(sessionManager
						.getDocbaseName())).getStore();
				MockJcrQueryManager mrQueryMger = new MockJcrQueryManager(a);
				Query q = mrQueryMger.createQuery(this.query, "xpath");
				QueryResult qr = q.execute();
				MockDmCollection co = new MockDmCollection(qr);
				return co;
			} catch (javax.jcr.RepositoryException e) {
				throw new RepositoryException(e);
			}
		} else {// Authorize query...
			String[] ids = this.query.split("', '");
			ids[0] = ids[0].substring(ids[0].lastIndexOf("'") + 1, ids[0]
					.length());
			List filteredResults = new MockMockList(ids, sessionManager);
			if (filteredResults != null) {
				QueryResult filteredQR = new MockJcrQueryResult(filteredResults);
				MockDmCollection finalCollection = new MockDmCollection(
						filteredQR);
				return finalCollection;
			} else {
				return null;// null value is tested in DctmAuthorizationManager
				// and won't lead to any NullPointerException
			}
		}
	}

	public void setDQL(String dqlStatement) {
		this.query = dqlStatement;
	}

	public ResultSet execute(ISessionManager sessionManager, int queryType, IClientX clientX) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

}
