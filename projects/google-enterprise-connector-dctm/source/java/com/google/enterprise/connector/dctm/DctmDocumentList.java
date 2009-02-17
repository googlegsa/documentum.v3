package com.google.enterprise.connector.dctm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spiimpl.StringValue;

public class DctmDocumentList extends LinkedList implements DocumentList {

	private static final long serialVersionUID = 9081981L;

	private static Logger logger;
	
	ICollection collectionToAdd;
	
	ICollection collectionToDel;

	IClientX clientX;

	ISessionManager sessMag;

	private boolean isPublic;

	private HashSet included_meta;

	private String lastCheckPoint;
	private String dateFirstPush;
	
	static {
		logger = Logger.getLogger(DctmDocumentList.class.getName());
	}

	private DctmSysobjectDocument dctmSysobjectDocument;
	private DctmSysobjectDocument dctmSysobjectDocumentToDel;

	public DctmDocumentList() {
		super();
	}

	public DctmDocumentList(ICollection collToAdd,ICollection collToDel, ISessionManager sessMag,
			IClientX clientX, boolean isPublic, HashSet included_meta, String dateFirstPush,String lastCheckPoint) {
		this.collectionToAdd = collToAdd;
		this.collectionToDel = collToDel;
		this.clientX = clientX;
		this.sessMag = sessMag;
		this.isPublic = isPublic;
		this.included_meta = included_meta;
		this.dateFirstPush = dateFirstPush;
		this.lastCheckPoint = lastCheckPoint;
	}

	public Document nextDocument() throws RepositoryException {
		if (collectionToAdd.getState() == ICollection.DF_CLOSED_STATE) {
			logger.fine("state of collectionToAdd : closed");
			return null;
		}
		
		logger.fine("The collection is not in a closed state");
		logger.fine("The collection state :" + collectionToAdd.getState());
		logger.fine("The collection delete state :"+ collectionToAdd.getState());
		
		Document retDoc = null;
		try {
			if (collectionToAdd.next()) {
				logger.fine("Looking through the collection");
				
				String crID = "";
				ITime lastModifDate = null;
				try {
					crID = collectionToAdd.getString("r_object_id");
					
					///
					lastModifDate = collectionToAdd.getTime("r_modify_date");
					logger.fine("lastModifDate is "+lastModifDate);
					///
					logger.fine("r_object_id is "+crID);
				} catch (RepositoryException e) {
					logger.severe("impossible to get the r_object_id of the document");
					return null;
				}
				
				dctmSysobjectDocument = new DctmSysobjectDocument(crID, lastModifDate, sessMag,
						clientX, isPublic ? "true" : "false", included_meta,
						SpiConstants.ActionType.ADD);
				
				logger.fine("Creation of a new dctmSysobjectDocument");
				retDoc = dctmSysobjectDocument;
			} else if (collectionToDel != null && collectionToDel.next()) {
				logger.fine("Looking through the collection of document to remove");
				
				String crID = "";
				String commonVersionID = "";
				ITime lastDeleteDate = null;
				try {
					crID = collectionToDel.getString("r_object_id");
					
					commonVersionID = collectionToDel.getString("chronicle_id");
					lastDeleteDate = collectionToDel.getTime("time_stamp");
					
					logger.fine("r_object_id is "+crID);
				} catch (RepositoryException e) {
					logger.warning("impossible to get the r_object_id of the document");
					return null;
				}
				
				dctmSysobjectDocumentToDel = new DctmSysobjectDocument(crID, commonVersionID, lastDeleteDate, sessMag,
						clientX, isPublic ? "true" : "false", included_meta,
						SpiConstants.ActionType.DELETE);
				
				logger.fine("Creation of a new dctmSysobjectDocumentToDel");
				retDoc = dctmSysobjectDocumentToDel;
			} else {
				logger.warning("End of document list");
			}
		} catch (RepositoryException re) {
			logger.warning("Error while trying to get next document : "+re);
			checkpoint();		
		} finally {
			if (retDoc == null) {
				logger.fine("retDoc is null before finalize");
				finalize();
			}
		}
		return retDoc;
	}

	public String checkpoint() throws RepositoryException {
		logger.fine("Creation of the checkpoint");
		logger.fine("The collection state :" + collectionToAdd.getState());
		//Last Document added to the GSA
		String uuid = "";
		String dateString = "";
		if (dctmSysobjectDocument != null) {
			logger.fine("in dctmSysobjectDocument not null");
			DctmSysobjectProperty prop = ((DctmSysobjectProperty) (dctmSysobjectDocument
					.findProperty("r_object_id")));
			uuid = ((StringValue) prop.nextValue()).toString();
			logger.fine("uuid of the checkpoint is "+uuid);
			
			prop = (DctmSysobjectProperty) dctmSysobjectDocument.findProperty(SpiConstants.PROPNAME_LASTMODIFIED);
			
			DctmDateValue nextValAdd =  ((DctmDateValue) prop.nextValue());
			
			if (nextValAdd != null) {
				logger.fine("in nextValAdd not null");
				dateString = nextValAdd.toDctmFormat();
			} else {
				logger.fine("in nextValAdd null");
				ITime crDate = dctmSysobjectDocument.getLastModifDate();
				logger.finest("crDate is "+crDate);
				Date tmpDt  = crDate.getDate();
				logger.finest("tmpDt is "+tmpDt);
				dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tmpDt);	
			}
			logger.fine("dateString of the checkpoint is "+dateString);
		} else {
			logger.fine("in dctmSysobjectDocument null");
			JSONObject jo;
			try {
				jo = new JSONObject(lastCheckPoint);
				uuid = jo.getString("uuid");
				dateString = jo.getString("lastModified");
			} catch (JSONException e) {
				logger.severe("JSON exception, while getting last checkpoint.");
			}	
		}
		
		String dateStringDocToDel = "";
		String uuidDocToDel = "";
		if (dctmSysobjectDocumentToDel != null)
		{
			logger.fine("in dctmSysobjectDocumentToDel not null");
			DctmSysobjectProperty propDocToDel = ((DctmSysobjectProperty) (dctmSysobjectDocumentToDel.findProperty("r_object_id")));
			
			StringValue nextVal = ((StringValue) propDocToDel.nextValue());
			if (nextVal != null) {
				uuidDocToDel = nextVal.toString();
			}
			
			logger.fine("uuid of the checkpoint of deleted document is "+uuidDocToDel);
			propDocToDel = (DctmSysobjectProperty) dctmSysobjectDocumentToDel.findProperty(SpiConstants.PROPNAME_LASTMODIFIED);
			
			DctmDateValue nextVal2 = ((DctmDateValue) propDocToDel.nextValue());
			if (nextVal2 != null) {
				dateStringDocToDel = nextVal2.toDctmFormat();
			} else {
				logger.fine("in nextVal null");
				ITime crDateDocToDel = collectionToDel.getTime("time_stamp");
				logger.fine("ITime crDateDocToDel is "+crDateDocToDel);
				Date tmpDtDocToDel  = crDateDocToDel.getDate();
				logger.fine("Date tmpDtDocToDel is "+tmpDtDocToDel);
				dateStringDocToDel = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tmpDtDocToDel);	
				logger.fine("String dateStringDocToDel is "+dateStringDocToDel);
			}
			
			logger.fine("dateString of the checkpoint of deleted document is "+dateStringDocToDel);
		} else if (lastCheckPoint != null) {
			logger.fine("in dctmSysobjectDocumentToDel null");
			//Get the last modify date in the previous checkpoint; if exists
			JSONObject jo;
			try {
				jo = new JSONObject(lastCheckPoint);
				dateStringDocToDel = jo.getString("lastRemoveDate");
			} catch (JSONException e) {
				logger.severe("JSON exception, while getting last removed date.");
			}
			
		} else {//last modified object = null, set the del modified date to first push
			//first push date	
			logger.fine("in firstpush");
			dateStringDocToDel = dateFirstPush;
		}
		
		String result = null;
		try {
			logger.fine("checkpoint construction");
			JSONObject jo = new JSONObject();
			jo.put("uuid", uuid);
			
			jo.put("lastModified", dateString);
			
			jo.put("uuidToRemove", uuidDocToDel);
			
			jo.put("lastRemoveDate", dateStringDocToDel);
			
			result = jo.toString();
			logger.fine("result : "+result);
		} catch (JSONException e) {
			logger.severe("Unexpected JSON problem");
			StackTraceElement[] test = e.getStackTrace();
			for (int i = 0; i < test.length; i++) {
				System.out.println(test[i].toString());
			}
			throw new RepositoryException("Unexpected JSON problem", e);
		} catch (Exception e) {
			logger.severe("Collection is closed after problem...");
		} finally {
			finalize();
		}

		return result;
	}

  // Last chance to make sure the collections are closed and their sessions
  // are released.
  public void finalize() {
    if ((collectionToAdd != null) &&
        (collectionToAdd.getState() != ICollection.DF_CLOSED_STATE)) {
    	try {
	        collectionToAdd.close();
	        logger.fine("collection of documents to add closed");
	        sessMag.releaseSessionAdd(); 
	        logger.fine("collection session released");
	} catch (RepositoryException e) {
	        logger.severe("Error while closing the collection of documents to add: " + e);
	}
    }

    if ((collectionToDel != null) &&
        (collectionToDel.getState() != ICollection.DF_CLOSED_STATE)) {
    	try {
	        collectionToDel.close();
	        logger.fine("collection of documents to delete closed");
	        sessMag.releaseSessionDel(); 
	        logger.fine("collection session released");
	} catch (RepositoryException e) {
	        logger.severe("Error while closing the collection of documents to delete: " + e);
	}
    }
  }
}
