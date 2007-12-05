package com.google.enterprise.connector.dctm;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.TraversalManager;

public class DctmTraversalManager implements TraversalManager {

	private IClientX clientX;

	private String order_by = " order by r_modify_date,r_object_id";

	private String whereBoundedClause = " and ((r_modify_date = date(''{0}'',''yyyy-mm-dd hh:mi:ss'')  and r_object_id > ''{1}'') OR ( r_modify_date > date(''{2}'',''yyyy-mm-dd hh:mi:ss'')))";

	private String serverUrl;

	private int batchHint = -1;

	private ISessionManager sessionManager;

	protected String additionalWhereClause;

	private boolean isPublic;

	private HashSet included_meta;

	private HashSet excluded_meta;

	private HashSet included_object_type;

	private String root_object_type;

	private static Logger logger = null;

	static {
		logger = Logger.getLogger(DctmTraversalManager.class.getName());
	}

	public DctmTraversalManager(IClientX clientX, String webtopServerUrl,
			String additionalWhereClause, boolean isPublic,
			HashSet included_meta, HashSet excluded_meta,
			HashSet included_object_type, String root_object_type)
			throws RepositoryException {
		this.additionalWhereClause = additionalWhereClause;
		setClientX(clientX);
		setSessionManager(clientX.getSessionManager());
		this.serverUrl = webtopServerUrl;
		this.isPublic = isPublic;
		this.included_meta = included_meta;
		this.excluded_meta = excluded_meta;
		this.included_object_type = included_object_type;
		this.root_object_type = root_object_type;
	}

	protected void setClientX(IClientX clientX) {
		this.clientX = clientX;
	}

	protected IClientX getClientX() {
		return clientX;
	}

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	protected String getServerUrl() {
		return serverUrl;
	}

	/**
	 * Starts (or restarts) traversal from the beginning. This action will
	 * return objects starting from the very oldest, or with the smallest IDs,
	 * or whatever natural order the implementation prefers. The caller may
	 * consume as many or as few of the results as it wants, but it gurantees to
	 * call {@link #checkpoint(PropertyMap)} passing in the past object is has
	 * successfully processed.
	 * 
	 * @return A ResultSet of documents from the repository in natural order
	 * @throws RepositoryException
	 *             if the Repository is unreachable or similar exceptional
	 *             condition.
	 */
	public DocumentList startTraversal() throws RepositoryException {
		logger.info("Pull process started");
		IQuery query = makeCheckpointQuery(buildQueryString(null));
		return execQuery(query);
	}

	/**
	 * Continues traversal from a supplied checkpoint. The checkPoint parameter
	 * will have been created by a call to the {@link #checkpoint(PropertyMap)}
	 * method. The ResultSet object returns objects from the repository in
	 * natural order starting just after the document that was used to create
	 * the checkpoint string.
	 * 
	 * @param checkPoint
	 *            String that indicates from where to resume traversal.
	 * @return ResultSet object that returns documents starting just after the
	 *         checkpoint.
	 * @throws RepositoryException
	 */
	public DocumentList resumeTraversal(String checkPoint)
			throws RepositoryException {
		logger.info("value of checkpoint  " + checkPoint);
		DocumentList documentList = null;
		IQuery query = makeCheckpointQuery(buildQueryString(checkPoint));
		documentList = execQuery(query);
		return documentList;
	}

	/**
	 * Sets the preferred batch size. The caller advises the implementation that
	 * the result sets returned by startTraversal or resumeTraversal need not be
	 * larger than this number. The implementation may ignore this call or do
	 * its best to return approximately this number.
	 * 
	 * @param batchHint
	 * @throws RepositoryException
	 */
	public void setBatchHint(int batchHint) throws RepositoryException {
		logger.info("batchHint of " + batchHint);
		this.batchHint = batchHint;
	}

	protected DocumentList execQuery(IQuery query) throws RepositoryException {
		sessionManager.setServerUrl(serverUrl);
		ICollection collec = null;
		DocumentList documentList = null;
		collec = query.execute(sessionManager, IQuery.EXECUTE_READ_QUERY);

		documentList = new DctmDocumentList(collec, sessionManager, clientX,
				isPublic, included_meta, excluded_meta);

		return documentList;
	}

	protected IQuery makeCheckpointQuery(String queryString)
			throws RepositoryException {
		IQuery query = null;
		query = clientX.getQuery();
		query.setDQL(queryString);
		return query;
	}

	protected String extractDocidFromCheckpoint(JSONObject jo, String checkPoint) {
		String uuid = null;
		try {
			uuid = jo.getString("uuid");
		} catch (JSONException e) {
			logger.severe("could not get uuid from checkPoint string: "
					+ checkPoint);
			throw new IllegalArgumentException(
					"could not get uuid from checkPoint string: " + checkPoint);
		}
		return uuid;
	}

	protected String extractNativeDateFromCheckpoint(JSONObject jo,
			String checkPoint) {
		String dateString = null;
		try {
			dateString = jo.getString("lastModified");
		} catch (JSONException e) {
			logger.severe("could not get lastmodify from checkPoint string: "
					+ checkPoint);
			throw new IllegalArgumentException(
					"could not get lastmodify from checkPoint string: "
							+ checkPoint);
		}
		return dateString;
	}

	protected String makeCheckpointQueryString(String uuid, String c)
			throws RepositoryException {
		Object[] arguments = { c, uuid, c };
		String statement = MessageFormat.format(whereBoundedClause, arguments);
		return statement;
	}

	protected String buildQueryString(String checkpoint)
			throws RepositoryException {

		StringBuffer query = new StringBuffer(
				"select i_chronicle_id, r_object_id, r_modify_date from "
						+ this.root_object_type);
		if (!included_object_type.isEmpty()) {
			query.append(" where (");
			Iterator iter = this.included_object_type.iterator();
			String name = (String) iter.next();
			query.append(" r_object_type='" + name + "'");
			while (iter.hasNext()) {
				name = (String) iter.next();
				query.append(" OR r_object_type='" + name + "'");
			}
			query.append(")");
		} else {
			query.append(" where r_object_type='");
			query.append("dm_document");
			query.append("' ");
		}
		if (this.additionalWhereClause != null) {
			query.append(additionalWhereClause);
		}
		if (checkpoint != null) {
			query.append(getCheckpointClause(checkpoint));
		}

		query.append(order_by);

		if (batchHint > 0) {
			if (query.indexOf("ENABLE (return_top") == -1) {
				query.append(" ENABLE (return_top "
						+ Integer.toString(batchHint) + ")");
			} else {
				int a = query.indexOf(" ENABLE (return_top");
				query.replace(a, query.length(), " ENABLE (return_top "
						+ Integer.toString(batchHint) + ")");
			}
		}
		return query.toString();
	}

	protected String getCheckpointClause(String checkPoint)
			throws RepositoryException {
		logger.info("value of checkpoint" + checkPoint);
		JSONObject jo = null;
		try {
			jo = new JSONObject(checkPoint);
		} catch (JSONException e) {
			logger.severe("checkPoint string does not parse as JSON: "
					+ checkPoint);
			throw new IllegalArgumentException(
					"checkPoint string does not parse as JSON: " + checkPoint);
		}
		String uuid = extractDocidFromCheckpoint(jo, checkPoint);
		String c = extractNativeDateFromCheckpoint(jo, checkPoint);
		String queryString = makeCheckpointQueryString(uuid, c);
		return queryString;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

}
