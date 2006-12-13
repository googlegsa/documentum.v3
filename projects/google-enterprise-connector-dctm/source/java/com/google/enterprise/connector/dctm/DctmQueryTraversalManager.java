package com.google.enterprise.connector.dctm;

import java.io.ByteArrayInputStream;

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
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.SimpleProperty;
import com.google.enterprise.connector.spi.SimplePropertyMap;
import com.google.enterprise.connector.spi.SimpleResultSet;
import com.google.enterprise.connector.spi.SimpleValue;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.ValueType;

public class DctmQueryTraversalManager implements QueryTraversalManager{
	IDctmSession idctmses;
	
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
		  String query=null;
		  IDctmCollection col=null;
		  byte[]buf=null;
		  int count = 0;
		  
		  String modifDate=null;
		  String crID=null;
		  String mimetype=null;
		  
		  SimpleValue vlDate=null;
		  SimpleValue vlID=null;
		  SimpleValue vlMime=null;
		  
		  SimplePropertyMap pm=null;
		  
		  ByteArrayInputStream content=null;
		  
		  IDctmSession dctmSes = getIdctmses();
		  IDctmSysObject dctmSysObj = null;
		  IDctmFormat dctmForm = null;
		  query=CreateQuery();
		  col=execQuery(query);
		  
		  resu=new SimpleResultSet(); 
		  
			  while (col.next()){
				  pm=new SimplePropertyMap();
				 
				  crID = col.getValue("i_chronicle_id").asString();
				  vlID=new SimpleValue(ValueType.STRING,crID);
				  pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_DOCID,vlID));
					 
				  modifDate = col.getValue("r_modify_date").asString();
				  vlDate=new SimpleValue(ValueType.DATE,modifDate);
				  pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_LASTMODIFY,vlDate)); 
				  
				  dctmSysObj = (IDctmSysObject)dctmSes.getObjectByQualification("dm_document where i_chronicle_id = '" + crID + "'");
				  dctmForm = (IDctmFormat)dctmSysObj.getFormat();
				  
				  if(dctmForm.canIndex()){
					  content=dctmSysObj.getContent();
					  mimetype=dctmForm.getMIMEType();
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
	  public String checkpoint(PropertyMap pm) throws RepositoryException{
		  String resu=null;
		  return resu;
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
	  
	  public IDctmCollection execQuery(String queryString) {
		  	IDctmCollection dctmCollection = null; // Collection for the result
			IDctmQuery dctmQuery = new IDctmQuery(); // Create query object
			dctmQuery.setDQL(queryString); // Give it the query
			dctmCollection = (IDctmCollection)dctmQuery.execute(idctmses, IDctmQuery.DF_READ_QUERY);
			return dctmCollection;
		}

	public IDctmSession getIdctmses() {
		return idctmses;
	}

	public void setIdctmses(IDctmSession idctmses) {
		this.idctmses = idctmses;
	}

	
	  
}
