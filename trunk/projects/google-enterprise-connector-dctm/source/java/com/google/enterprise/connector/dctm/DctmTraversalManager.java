package com.google.enterprise.connector.dctm;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.PropertyMapList;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.TraversalManager;
import com.google.enterprise.connector.spi.Value;

public class DctmTraversalManager implements TraversalManager {

	private IClientX clientX;

	private String order_by = " order by r_modify_date,r_object_id";

	// TODO: add possibility for an administrator to change it
	private String tableName = "dm_document";

	private String whereBoundedClause = " and ((r_modify_date = ''{0}''  and r_object_id > ''{1}'') OR ( r_modify_date > ''{2}''))";

	private String serverUrl;

	private int batchHint = -1;

	private ISessionManager sessionManager;

	protected String additionalWhereClause;

	private boolean isPublic;

	private static Logger logger = null;

	static {
		logger = Logger.getLogger(DctmTraversalManager.class.getName());
		logger.setLevel(Level.ALL);
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

	public DctmTraversalManager(IClientX clientX, String webtopServerUrl,
			String additionalWhereClause, boolean isPublic)
			throws RepositoryException {
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 4) {
			OutputPerformances.setPerfFlag("qtm", "Valuate IClient", null);
		}
		this.additionalWhereClause = additionalWhereClause;

		setClientX(clientX);

		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 4) {
			OutputPerformances.endFlag("qtm", "Valuate IClient");
		}

		setSessionManager(clientX.getSessionManager());

		this.serverUrl = webtopServerUrl;
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
			logger.info("webtop url " + serverUrl);
		}

		this.isPublic = isPublic;

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
	public PropertyMapList startTraversal() throws RepositoryException {
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
			logger.info("Pull process started");
		}
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
	public PropertyMapList resumeTraversal(String checkPoint)
			throws RepositoryException {
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
			logger.info("value of checkpoint  " + checkPoint);
		}

		PropertyMapList propertyMapList = null;
		IQuery query = makeCheckpointQuery(buildQueryString(checkPoint));
		propertyMapList = execQuery(query);
		return propertyMapList;
	}

	/**
	 * Checkpoints the traversal process. The caller passes in a property map
	 * taken from the {@link ResultSet} object that it obtained from either the
	 * startTraversal or resumeTraversal methods. This property map is the last
	 * document that the caller successfully processed. This is NOT necessarily
	 * the last object from the result set - the caller may consume as much or
	 * as little of a result set as it chooses. If the implementation wants the
	 * caller to persist the traversal state, then it should write a string
	 * representation of that state and return it. If the implementation prefers
	 * to maintain state itself, it should use this call as a signal to commit
	 * its state, up to the document passed in.
	 * 
	 * @param pm
	 *            A property map obtained from a ResultSet obtained from either
	 *            {@link #startTraversal()} or {link
	 *            {@link #resumeTraversal(String)}.
	 * @return A string that can be used by a subsequent call to the
	 *         {@link #resumeTraversal(String)} method.
	 * @throws RepositoryException
	 */
	public String checkpoint(PropertyMap pm) throws RepositoryException {
		String uuid = fetchAndVerifyValueForCheckpoint(pm,
				SpiConstants.PROPNAME_DOCID).getString();

		Value value = fetchAndVerifyValueForCheckpoint(pm,
				SpiConstants.PROPNAME_LASTMODIFIED);

		String dateString = DctmSysobjectValue.calendarToIso8601(value
				.getDate());

		String result = null;
		try {
			JSONObject jo = new JSONObject();
			jo.put("uuid", uuid);
			jo.put("lastModified", dateString);
			result = jo.toString();
		} catch (JSONException e) {
			if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
				logger.severe("Unexpected JSON problem");
			}
			throw new RepositoryException("Unexpected JSON problem", e);
		}
		return result;
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
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 1) {
			logger.info("batchHint of " + batchHint);
		}
		this.batchHint = batchHint;
	}

	public PropertyMapList execQuery(IQuery query) throws RepositoryException {
		sessionManager.setServerUrl(serverUrl);
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 4) {
			OutputPerformances.setPerfFlag("qtm", "Processing query", null);
		}

		ICollection collec = query.execute(sessionManager, IQuery.READ_QUERY);
		PropertyMapList propertyMapList = new DctmResultSet(collec,
				sessionManager, clientX, isPublic);

		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 4) {
			OutputPerformances.endFlag("qtm", "ResultSet built.");
		}
		return propertyMapList;
	}

	public Value fetchAndVerifyValueForCheckpoint(PropertyMap pm, String pName)
			throws RepositoryException {
		Property property = pm.getProperty(pName);
		if (property == null) {
			throw new IllegalArgumentException("checkpoint must have a "
					+ pName + " property");
		}
		Value value = property.getValue();
		if (value == null) {
			throw new IllegalArgumentException("checkpoint " + pName
					+ " property must have a non-null value");
		}
		return value;
	}

	private IQuery makeCheckpointQuery(String queryString)
			throws RepositoryException {
		IQuery query = null;
		query = clientX.getQuery();
		query.setDQL(queryString);
		return query;
	}

	public String extractDocidFromCheckpoint(JSONObject jo, String checkPoint) {
		String uuid = null;
		try {
			uuid = jo.getString("uuid");
		} catch (JSONException e) {
			if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
				logger.severe("could not get uuid from checkPoint string: "
						+ checkPoint);
			}
			throw new IllegalArgumentException(
					"could not get uuid from checkPoint string: " + checkPoint);
		}
		return uuid;
	}

	public String extractNativeDateFromCheckpoint(JSONObject jo,
			String checkPoint) {
		String dateString = null;
		try {
			dateString = jo.getString("lastModified");
		} catch (JSONException e) {
			if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
				logger
						.severe("could not get lastmodify from checkPoint string: "
								+ checkPoint);
			}
			throw new IllegalArgumentException(
					"could not get lastmodify from checkPoint string: "
							+ checkPoint);
		}

		return dateString;
	}

	public String makeCheckpointQueryString(String uuid, String c)
			throws RepositoryException {
		Object[] arguments = { c, uuid, c };

		String statement = MessageFormat.format(whereBoundedClause, arguments);

		return statement;
	}

	private String buildQueryString(String checkpoint)
			throws RepositoryException {

		StringBuffer query = new StringBuffer(
				"select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject");
		query.append(" where r_object_type='");
		query.append(tableName);
		query.append("' ");
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
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
			logger.info(query.toString());
		}

		return query.toString();
	}

	private String getCheckpointClause(String checkPoint)
			throws RepositoryException {
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
			logger.info("value of checkpoint" + checkPoint);
		}
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
