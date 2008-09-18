package com.google.enterprise.connector.dctm;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.methods.DeleteMethod;
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

	private String whereBoundedClause = " and ((r_modify_date = date(''{0}'',''yyyy-mm-dd hh:mi:ss'')  and r_object_id > ''{1}'') OR ( r_modify_date > date(''{0}'',''yyyy-mm-dd hh:mi:ss'')))";
	 

	private String whereBoundedClauseRemove = " and ((time_stamp = date(''{0}'',''yyyy-mm-dd hh:mi:ss'') and (r_object_id > ''{1}'')) OR ( time_stamp > date(''{0}'',''yyyy-mm-dd hh:mi:ss'')))";
	private String whereBoundedClauseRemoveDateOnly = " and ( time_stamp > date(''{0}'',''yyyy-mm-dd hh:mi:ss''))";

	private String serverUrl;

	private int batchHint = -1;

	private ISessionManager sessionManager;

	protected String additionalWhereClause;

	private boolean isPublic;

	private String included_meta;

	private HashSet excluded_meta;

	private String included_object_type;
	
	private HashSet hash_included_object_type;
	
	private HashSet hash_included_meta;

	private String root_object_type;

	private static Logger logger = null;

	private static String dateFirstPush ;

	static {
		logger = Logger.getLogger(DctmTraversalManager.class.getName());
	}

	public DctmTraversalManager(IClientX clientX, String webtopServerUrl,
			String additionalWhereClause, boolean isPublic,
			String included_meta, HashSet excluded_meta,
			String included_object_type, String root_object_type)
	throws RepositoryException {
		this.additionalWhereClause = additionalWhereClause;
		setClientX(clientX);
		setSessionManager(clientX.getSessionManager());
		
		this.serverUrl = webtopServerUrl;
		this.isPublic = isPublic;
		this.included_meta = included_meta;
		this.excluded_meta = excluded_meta;
		this.included_object_type = included_object_type;
		setHash_included_object_type(included_object_type);
		setHash_included_meta(included_meta);
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
		IQuery queryGetDocToDel = makeCheckpointQuery(buildQueryStringToDel(null));

		
		return execQuery(query,queryGetDocToDel,null);
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
		IQuery queryGetDocToDel = makeCheckpointQuery(buildQueryStringToDel(checkPoint));
		
		documentList = execQuery(query,queryGetDocToDel,checkPoint);

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

	/**
	 * Execute queries to retrieve the documents to add to the GSA and the document to remove from the GSA.
	 * @param query
	 * @param queryDocToDel
	 * @return
	 * @throws RepositoryException
	 */
	protected DocumentList execQuery(IQuery query,IQuery queryDocToDel,String checkPoint) throws RepositoryException {
		sessionManager.setServerUrl(serverUrl);
		ICollection collecToAdd = null;
		ICollection collecToDel = null;

		DocumentList documentList = null;
		try {
			if (query != null) {
				collecToAdd = query.execute(sessionManager, IQuery.EXECUTE_READ_QUERY);
				logger.fine("execution of the query returns a collection of document to add");
			}
			if (queryDocToDel != null) {
				collecToDel = queryDocToDel.execute(sessionManager, IQuery.EXECUTE_READ_QUERY);
				logger.fine("execution of the query returns a collection of document to delete");
			}
			if ((collecToAdd != null && collecToAdd.hasNext()) ||
					(collecToDel != null && collecToDel.hasNext())) {
				documentList = new DctmDocumentList(collecToAdd, collecToDel, sessionManager,
						clientX, isPublic, hash_included_meta, excluded_meta, dateFirstPush, checkPoint);
			}
			return documentList;
		} finally {
			// No documents to add or delete.	 Return a null DocumentList,
			// but close the collections first!
			if (documentList == null) {
				if (collecToAdd != null) {
					try {
						collecToAdd.close();
						logger.fine("collection of documents to add closed");
						sessionManager.release(collecToAdd.getSession());
						logger.fine("collection session released");
					} catch (RepositoryException e) {
						logger.severe("Error while closing the collection of documents to add: " + e);
					}
				}
				if (collecToDel != null) {
					try {
						collecToDel.close();
						logger.fine("collection of documents to delete closed");
						sessionManager.release(collecToDel.getSession());
						logger.fine("collection session released");
					} catch (RepositoryException e) {
						logger.severe("Error while closing the collection of documents to delete: " + e);
					}
				}
			}
		}
	}

	/**
	 * Overload execQuery to test checkAdditionalWhereClause
	 * @param query
	 * @return
	 * @throws RepositoryException
	 */
	protected DocumentList execQuery(IQuery query) throws RepositoryException {
		return this.execQuery(query, null, null);
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

	/**
	 * Retrieve the last object_id removed from the GSA.
	 * 
	 * @param jo
	 * @param checkPoint
	 * @return uuid
	 */
	protected String extractDocidFromCheckpointRemove(JSONObject jo, String checkPoint) {
		String uuid = null;
		try {
			uuid = jo.getString("uuidToRemove");
		} catch (JSONException e) {
			logger.severe("could not get uuid from checkPoint string: "
					+ checkPoint);
			throw new IllegalArgumentException(
					"could not get uuid from checkPoint string: " + checkPoint);
		}
		return uuid;
	}

	/**
	 * Retrieve the last remove date of the last document removed from the GSA.
	 * @param jo
	 * @param checkPoint
	 * @return dateString
	 */
	protected String extractNativeDateFromCheckpointRemove(JSONObject jo,
			String checkPoint) {
		String dateString = null;
		try {
			dateString = jo.getString("lastRemoveDate");
		} catch (JSONException e) {
			logger.severe("could not get lastmodify from checkPoint string: "
					+ checkPoint);
			throw new IllegalArgumentException(
					"could not get lastmodify from checkPoint string: "
					+ checkPoint);
		}
		return dateString;
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


	protected String makeCheckpointQueryRemoveString(String uuid, String c)
	throws RepositoryException {
		//to format the date (0-24h instead of 0-12h)
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date newDt= sdf.parse(c);
			c = sdf.format(newDt);
		} catch (ParseException e) {
			logger.fine("Error while converting string to date.");
		}
		Object[] arguments = { c, uuid};
		String statement = "";
		if(uuid.equals("")) {
			statement = MessageFormat.format(whereBoundedClauseRemoveDateOnly, arguments);
		}
		else{
			statement = MessageFormat.format(whereBoundedClauseRemove, arguments);
		}
		logger.fine("query after integration of the checkpoint " + statement);
		return statement;
	}

	protected String makeCheckpointQueryString(String uuid, String c)
	throws RepositoryException {
		//to format the date (0-24h instead of 0-12h)
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date newDt= sdf.parse(c);
			c = sdf.format(newDt);
		} catch (ParseException e) {
			logger.fine("Error while converting string to date.");
		}
		Object[] arguments = { c, uuid};
		String statement = MessageFormat.format(whereBoundedClause, arguments);
		logger.fine("query after integration of the checkpoint " + statement);
		return statement;
	}

	protected String buildQueryString(String checkpoint)
	throws RepositoryException {

		StringBuffer query = new StringBuffer(
				"select i_chronicle_id, r_object_id, r_modify_date from "
				+ this.root_object_type);
		if (!hash_included_object_type.isEmpty()) {
			query.append(" where (");
			Iterator iter = hash_included_object_type.iterator();
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
		if (this.additionalWhereClause != null && (!this.additionalWhereClause.equals(""))) {
			logger.fine("adding the additionalWhereClause to the query : "+additionalWhereClause);
			///adding the "and" operator if not present in the additional where clause (mandatory)
			if (!this.additionalWhereClause.toLowerCase().startsWith("and ")) {
				///throw new RepositoryException("[additional] ");
				logger.log(Level.INFO, "clause does not start with AND : ");
				this.additionalWhereClause="and ".concat(additionalWhereClause);
				logger.log(Level.INFO, "after adding AND : "
						+ additionalWhereClause);
			}
			
			query.append(additionalWhereClause);
		}
		if (checkpoint != null) {
			logger.fine("adding the checkpoint to the query : "+checkpoint);
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
		logger.fine("query completed : "+query.toString());
		return query.toString();
	}

	protected String buildQueryStringToDel(String checkpoint){
		StringBuffer query = new StringBuffer(
		"select r_object_id,  chronicle_id, time_stamp from dm_audittrail " );

		query.append(" where ");

		query.append(" event_name='dm_destroy' ");

		if (checkpoint != null) {
			logger.fine("adding the checkpoint to the query : "+checkpoint);
			try {
				String whereClause = getCheckpointRemoveClause(checkpoint);
				logger.info("whereclause : " + whereClause);
				if(whereClause.indexOf("date('',") == -1)
				{
					logger.info("if not date");
					query.append(whereClause);
				}
			} catch (Exception e) {
				logger.severe("Error while getting checkpoint clause"+e.getMessage());
			}
		}else{
			try {
				//Get the date of today, corresponding to the date of first push
				java.util.Date d = new java.util.Date();
				java.text.SimpleDateFormat dateStandard = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dateFirstPush = dateStandard.format(d);
				logger.info("Date of first push :"+ dateStandard.format(d));
								
				String whereClause =  " and ( time_stamp > date('" + dateFirstPush + "','yyyy-mm-dd hh:mi:ss'))";
				logger.info("whereclause : " + whereClause);
				
				if(whereClause.indexOf("date('',") == -1)
				{
					logger.info("if not date");
					query.append(whereClause);
				}
			} catch (Exception e) {
				logger.severe("Error while getting checkpoint clause"+e.getMessage());
			}
			
		}
		logger.info("query.toString()" + query.toString());
		return  query.toString();		
	}

	protected String getCheckpointRemoveClause(String checkPoint) throws RepositoryException{
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
		String queryString="";
		try {
			String uuid = extractDocidFromCheckpointRemove(jo, checkPoint);
			logger.fine("uuid is " + uuid);
			String c = extractNativeDateFromCheckpointRemove(jo, checkPoint);
			logger.fine("native date is " + c);	

			queryString = makeCheckpointQueryRemoveString(uuid, c);
			logger.fine("queryString is " + queryString);

		} catch (Exception e) {
			logger.severe("Error while retrieving " +
					"the parameters of the previous checkpoint. "+ e.getMessage() );
		}
		return queryString;	
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
		logger.fine("uuid is " + uuid);
		String c = extractNativeDateFromCheckpoint(jo, checkPoint);
		logger.fine("native date is " + c);
		String queryString = makeCheckpointQueryString(uuid, c);
		logger.fine("queryString is " + queryString);
		return queryString;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	
	protected void setHash_included_object_type(String included_object_type){
		hash_included_object_type = new HashSet();
		String[] hashTab= included_object_type.split(",");
		int i = 0;
		for(i=0;i<hashTab.length;i++){
			hash_included_object_type.add(hashTab[i]);
		}
		this.hash_included_object_type=hash_included_object_type;
	}

	
	protected void setHash_included_meta(String included_metadata){
		hash_included_meta = new HashSet();
		String[] hashTab= included_metadata.split(",");
		int i = 0;
		for(i=0;i<hashTab.length;i++){
			hash_included_meta.add(hashTab[i]);
		}
		this.hash_included_meta=hash_included_meta;
	}
}
