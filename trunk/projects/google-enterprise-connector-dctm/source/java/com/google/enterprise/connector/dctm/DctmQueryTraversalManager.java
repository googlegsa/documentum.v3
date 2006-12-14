package com.google.enterprise.connector.dctm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmCollection;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmFormat;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLoginInfo;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmQuery;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSession;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.SimpleProperty;
import com.google.enterprise.connector.spi.SimplePropertyMap;
import com.google.enterprise.connector.spi.SimpleResultSet;
import com.google.enterprise.connector.spi.SimpleValue;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.Value;
import com.google.enterprise.connector.spi.ValueType;

public class DctmQueryTraversalManager implements QueryTraversalManager{
	IDctmSession idctmses;
	
	
	 private static final String QUERY_STRING_UNBOUNDED_DEFAULT = "select i_chronicle_id from dm_sysobject where r_object_type='dm_document' and r_creator_name!='Administrator' order by r_modify_date, i_chronicle_id";
		  
	 private static final String QUERY_STRING_BOUNDED_DEFAULT = 
		 "select i_chronicle_id from dm_sysobject where r_object_type='dm_document' and r_creator_name!='Administrator' and r_modify_date >= "+ 
		 "''{0}'' "+
		 "order by r_modify_date, i_chronicle_id";
	
	 private String unboundedTraversalQuery;
	 private String boundedTraversalQuery;
	 
	 
	 public DctmQueryTraversalManager() {
		    this.unboundedTraversalQuery = QUERY_STRING_UNBOUNDED_DEFAULT;
		    this.boundedTraversalQuery = QUERY_STRING_BOUNDED_DEFAULT;
	 }
	 
	 
	 public DctmQueryTraversalManager(IDctmSession iDctmSes) {
		 	setIDctmSession(iDctmSes);
		    this.unboundedTraversalQuery = QUERY_STRING_UNBOUNDED_DEFAULT;
		    this.boundedTraversalQuery = QUERY_STRING_BOUNDED_DEFAULT;
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
		  
		  ResultSet resu=null;
		  IQuery query=null;
		  ICollection col=null;
		  byte[]buf=null;
		  int count = 0;
		  
		  String modifDate=null;
		  String crID=null;
		  String mimetype=null;
		  
		  SimpleValue vlDate=null;
		  SimpleValue vlID=null;
		  SimpleValue vlMime=null;
		  SimpleValue vlCont=null;
		  
		  SimplePropertyMap pm=null;
		  
		  ByteArrayInputStream content=null;
		  
		  int size=0;
		  byte[] bufContent;
		  
		  ISession dctmSes = getIdctmses();
		  ISysObject dctmSysObj = null;
		  IFormat dctmForm = null;
		  
		  
		  query=makeCheckpointQuery(unboundedTraversalQuery);
		 
		  col=execQuery(query);
		  
		  resu=new SimpleResultSet(); 
		  
			  while (col.next()){
				  pm=new SimplePropertyMap();
				 
				  crID = col.getValue("i_chronicle_id").asString();
				  vlID=new SimpleValue(ValueType.STRING,crID);
				  pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_DOCID,vlID));
				
				  System.out.println(col.getValue("r_modify_date").toString());
				  //modifDate = col.getValue("r_modify_date").asString();
				  modifDate = col.getValue("r_modify_date").toString();
				  
				  vlDate=new SimpleValue(ValueType.DATE,modifDate);
				  pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_LASTMODIFY,vlDate)); 
				  
				  dctmSysObj = (IDctmSysObject)dctmSes.getObjectByQualification("dm_document where i_chronicle_id = '" + crID + "'");
				  dctmForm = (IDctmFormat)dctmSysObj.getFormat();
				  
				  if(dctmForm.canIndex()){
					  content=dctmSysObj.getContent();
					  mimetype=dctmForm.getMIMEType();
					  size=new Long(dctmSysObj.getContentSize()).intValue();
						 
					   bufContent = new byte[size];
						ByteArrayOutputStream output=new ByteArrayOutputStream(); 
						 try{
							 
							 while ((count = content.read(bufContent)) > -1){
							 
								 output.write(bufContent, 0, count);
							 }
							 content.close();
						 }catch(IOException ie){
							 System.out.println(ie.getMessage());
						 }
						 //content.
						 if(bufContent.length>0){
							 vlCont=new SimpleValue(ValueType.BINARY,bufContent);
							 pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_CONTENT,vlCont));
						 }else{
							 vlCont=new SimpleValue(ValueType.BINARY,"");
							 pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_CONTENT,vlCont));
						 }
				  }
				  
				  vlMime=new SimpleValue(ValueType.STRING,mimetype);
				  pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_MIMETYPE,vlMime));
					 
			  }
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
		  ResultSet resu=null;
		  JSONObject jo = null;
		    try {
		      jo = new JSONObject(checkPoint);
		    } catch (JSONException e) {
		      throw new IllegalArgumentException(
		          "checkPoint string does not parse as JSON: " + checkPoint);
		    }
		    String uuid = extractDocidFromCheckpoint(jo, checkPoint);
		    Calendar c = extractCalendarFromCheckpoint(jo, checkPoint);
		    String queryString = makeCheckpointQueryString(uuid, c);
		  
		  
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
		    String dateString = SimpleValue.calendarToIso8601(c);
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
		  
	  }
	  
	  private String CreateQuery(){
			String query;
			query="select i_chronicle_id,from dm_sysobject where r_object_type='dm_document' and r_creator_name!='Administrator' order by r_modify_date, i_chronicle_id";
			return(query);
	  }
	  
	  public ICollection execQuery(IQuery query) {
		  	ICollection dctmCollection = null; // Collection for the result
			dctmCollection = (IDctmCollection)query.execute(idctmses, IDctmQuery.DF_READ_QUERY);
			return dctmCollection;
		}

	public IDctmSession getIdctmses() {
		return idctmses;
	}

	public void setIDctmSession(IDctmSession idctmses) {
		this.idctmses = idctmses;
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
		    query=new IDctmQuery();
		    System.out.println(queryString);
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
	      c = SimpleValue.iso8601ToCalendar(dateString);
	    } catch (ParseException e) {
	      throw new IllegalArgumentException(
	          "could not parse date string from checkPoint string: " + dateString);
	    }
	    return c;
	  }
	  
	  private String makeCheckpointQueryString(String uuid, Calendar c)
      throws RepositoryException {

		  String time = SimpleValue.calendarToIso8601(c);
		  Object[] arguments = { time };
		  String statement = MessageFormat.format(boundedTraversalQuery,arguments);
		  return statement;
	  }

	  
}
