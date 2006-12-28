package com.google.enterprise.connector.dctm;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;


import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmCollection;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmFormat;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLoginInfo;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmQuery;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSession;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSysObject;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmTime;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmValue;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.ILocalClient;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.Value;
import com.google.enterprise.connector.spi.ValueType;
import com.google.enterprise.connector.spi.ResultSet;

public class DctmQueryTraversalManager implements QueryTraversalManager{
	
	IClient client;
	String sessionID;
	///ILocalClient localClient;
	
	ISession session;
	
	public static String QUERY_CLASS_NAME="com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmQuery";
	
	private String unboundedTraversalQuery;
	private String boundedTraversalQuery;
	
	
	protected void setClient(IClient client){
		this.client=client;
	}
	
	protected IClient getClient(){
		return client;
	}

	protected void setSessionID(String sessionID){
		this.sessionID=sessionID;
	}
	
	protected String getSessionID(){
		return sessionID;
	}

	private void setSession(){
		ILocalClient localClient=client.getLocalClientEx();
		session=localClient.findSession(sessionID); 
	}
	
	protected ISession getSession(){
		return session;
	}
	
	
	
	
	
	
	public DctmQueryTraversalManager(IClient client,String sessionID) {
		setClient(client);
		setSessionID(sessionID);
		setSession();
		DctmInstantiator.initialize();
		this.unboundedTraversalQuery = DctmInstantiator.QUERY_STRING_UNBOUNDED_DEFAULT;
		this.boundedTraversalQuery = DctmInstantiator.QUERY_STRING_BOUNDED_DEFAULT;
	}
	

	
	/**
	 * Starts (or restarts) traversal from the beginning. This action will return
	 * objects starting from the very oldest, or with the smallest IDs, or
	 * whatever natural order the implementation prefers. The caller may consume
	 * as many or as few of the results as it wants, but it gurantees to call
	 * {@link #checkpoint(PropertyMap)} passing in the past object is has
	 * successfully processed.
	 * 
	 * @return A ResultSet of documents from the repository in natural order
	 * @throws RepositoryException if the Repository is unreachable or similar
	 *           exceptional condition.
	 */
	
	
	
	public ResultSet startTraversal() throws RepositoryException{
		IQuery query=null;
		ResultSet resu=null;
		
		query=makeCheckpointQuery(unboundedTraversalQuery);
		System.out.println("query vaut "+unboundedTraversalQuery);
		System.out.println("query vaut "+query);
		resu=execQuery(query);
		return resu; 
	}
	
	
	
	/**
	 * Continues traversal from a supplied checkpoint. The checkPoint parameter
	 * will have been created by a call to the {@link #checkpoint(PropertyMap)}
	 * method. The ResultSet object returns objects from the repository in natural
	 * order starting just after the document that was used to create the
	 * checkpoint string.
	 * 
	 * @param checkPoint String that indicates from where to resume traversal.
	 * @return ResultSet object that returns documents starting just after the
	 *         checkpoint.
	 * @throws RepositoryException
	 */
	public ResultSet resumeTraversal(String checkPoint)
	throws RepositoryException{
		
		System.out.println("checkpoint vaut "+checkPoint);
		//{"uuid":"0900045780030e40","lastModified":"2006-09-27"}
		JSONObject jo = null;
		ResultSet resu=null;

		try {
			jo = new JSONObject(checkPoint);
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"checkPoint string does not parse as JSON: " + checkPoint);
		}
		String uuid = extractDocidFromCheckpoint(jo, checkPoint);
		Calendar c = extractCalendarFromCheckpoint(jo, checkPoint);
		String queryString = makeCheckpointQueryString(uuid, c);
		System.out.println("queryString vaut "+queryString);
		
		IQuery query=makeCheckpointQuery(queryString);
		resu=execQuery(query);

		return resu; 
	}
	
	/**
	 * Checkpoints the traversal process. The caller passes in a property map
	 * taken from the {@link ResultSet} object that it obtained from either the
	 * startTraversal or resumeTraversal methods. This property map is the last
	 * document that the caller successfully processed. This is NOT necessarily
	 * the last object from the result set - the caller may consume as much or as
	 * little of a result set as it chooses. If the implementation wants the
	 * caller to persist the traversal state, then it should write a string
	 * representation of that state and return it. If the implementation prefers
	 * to maintain state itself, it should use this call as a signal to commit its
	 * state, up to the document passed in.
	 * 
	 * @param pm A property map obtained from a ResultSet obtained from either
	 *          {@link #startTraversal()} or {link
	 *          {@link #resumeTraversal(String)}.
	 * @return A string that can be used by a subsequent call to the
	 *         {@link #resumeTraversal(String)} method.
	 * @throws RepositoryException
	 */
	public String checkpoint(PropertyMap pm) throws RepositoryException {
		String uuid =
			fetchAndVerifyValueForCheckpoint(pm, SpiConstants.PROPNAME_DOCID)
			.getString();
		Calendar c =
			fetchAndVerifyValueForCheckpoint(pm, SpiConstants.PROPNAME_LASTMODIFY)
			.getDate();
		String dateString = DctmValue.calendarToIso8601(c);
		String result = null;
		try {
			JSONObject jo = new JSONObject();
			jo.put("uuid", uuid);
			jo.put("lastModified", dateString);
			result = jo.toString();
		} catch (JSONException e) {
			throw new RepositoryException("Unexpected JSON problem", e);
		}
		return result;
	}
	
	
	/**
	 * Sets the preferred batch size. The caller advises the implementation that
	 * the result sets returned by startTraversal or resumeTraversal need not be
	 * larger than this number. The implementation may ignore this call or do its
	 * best to return approximately this number.
	 * 
	 * @param batchHint
	 * @throws RepositoryException
	 */
	public void setBatchHint(int batchHint) throws RepositoryException{
		;
	}
	
	
	
	public ResultSet execQuery(IQuery query) {
		ICollection dctmCollection = null; // Collection for the result
		//dctmCollection = query.execute(session, IDctmQuery.DF_READ_QUERY);
		dctmCollection = query.execute(session, 0);
		ResultSet rs = dctmCollection.buildResulSetFromCollection(session);
		return rs;
	}
	
	
	
	
	private Value fetchAndVerifyValueForCheckpoint(PropertyMap pm, String pName)
	throws RepositoryException {
		Property property = pm.getProperty(pName);
		if (property == null) {
			throw new IllegalArgumentException("checkpoint must have a " + pName
					+ " property");
		}
		Value value = property.getValue();
		if (value == null) {
			throw new IllegalArgumentException("checkpoint " + pName
					+ " property must have a non-null value");
		}
		return value;
	}
	
	private IQuery makeCheckpointQuery(String queryString) throws RepositoryException {
		IQuery query = null;
		
		//query=(IQuery)Class.forName(QUERY_CLASS_NAME).newInstance();
		
		
		//query=DctmInstantiator.getIQueryObject();
		//query.setDQL(queryString);
		
		query=client.getQuery();
		query.setDQL(queryString);
		
		return query;
	}
	
	String extractDocidFromCheckpoint(JSONObject jo, String checkPoint) {
		String uuid = null;
		try {
			uuid = jo.getString("uuid");
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"could not get uuid from checkPoint string: " + checkPoint);
		}
		return uuid;
	}
	
	Calendar extractCalendarFromCheckpoint(JSONObject jo, String checkPoint) {
		String dateString = null;
		try {
			dateString = jo.getString("lastModified");
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"could not get lastmodify from checkPoint string: " + checkPoint);
		}
		Calendar c = null;
		try {
			c = DctmValue.iso8601ToCalendar(dateString);
		} catch (ParseException e) {
			throw new IllegalArgumentException(
					"could not parse date string from checkPoint string: " + dateString);
		}
		return c;
	}
	
	private String makeCheckpointQueryString(String uuid, Calendar c)
	throws RepositoryException {
		
		String time = DctmValue.calendarToIso8601(c);
		Object[] arguments = { time };
		System.out.println(boundedTraversalQuery);
		String statement = MessageFormat.format(boundedTraversalQuery,arguments);
		return statement;
	}
	
}