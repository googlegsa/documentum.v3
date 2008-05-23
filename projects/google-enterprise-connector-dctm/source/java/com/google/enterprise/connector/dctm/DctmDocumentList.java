package com.google.enterprise.connector.dctm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spiimpl.StringValue;

public class DctmDocumentList extends LinkedList implements DocumentList {

	private static final long serialVersionUID = 9081981L;

	ICollection collec;

	IClientX clientX;

	ISessionManager sessMag;

	private boolean isPublic;

	private HashSet included_meta;

	private HashSet excluded_meta;
	

	private static Logger logger;

	static {
		logger = Logger.getLogger(DctmDocumentList.class.getName());
	}

	private DctmSysobjectDocument dctmSysobjectDocument;

	public DctmDocumentList() {
		super();
	}

	public DctmDocumentList(ICollection co, ISessionManager sessMag,
			IClientX clientX, boolean isPublic, HashSet included_meta,
			HashSet excluded_meta) {
		this.collec = co;
		this.clientX = clientX;
		this.sessMag = sessMag;
		this.isPublic = isPublic;
		this.included_meta = included_meta;
		this.excluded_meta = excluded_meta;

	}

	public Document nextDocument() throws RepositoryException {
		if (collec.getState() == ICollection.DF_CLOSED_STATE) {
			return null;
		}
		
		logger.fine("The collection is not in a closed state");
		
		
		try {
			if (collec.next()) {

				logger.fine("Looking throw the collection");
				
				String crID = "";
				try {
					crID = collec.getString("r_object_id");
					
					logger.fine("r_object_id is "+crID);
					
				} catch (RepositoryException e) {
					logger.severe("impossible to get the r_object_id of the document");
					return null;
				}
				
				dctmSysobjectDocument = new DctmSysobjectDocument(crID, sessMag,
						clientX, isPublic ? "true" : "false", included_meta,
						excluded_meta);
				
				logger.fine("Creation of a new dctmSysobjectDocument");
				return dctmSysobjectDocument;
			}
		} catch (RepositoryException re) {
			logger.severe("Error while trying to get next document : "+re);
			checkpoint();		
		}
		
		try{
			if (collec.getState() != ICollection.DF_CLOSED_STATE) {
				collec.close();
				logger.fine("collection closed");
				sessMag.release(collec.getSession());
				logger.fine("collection session released");
			}
		}catch (RepositoryException re1){
			logger.severe("Error while closing in nextDocument()"+re1);
		}
		
		return null;
	}

	public String checkpoint() throws RepositoryException {
		logger.fine("Creation of the checkpoint");
		DctmSysobjectProperty prop = ((DctmSysobjectProperty) (dctmSysobjectDocument
				.findProperty("r_object_id")));
		String uuid = ((StringValue) prop.nextValue()).toString();
		logger.fine("uuid of the checkpoint is "+uuid);
		prop = (DctmSysobjectProperty) dctmSysobjectDocument
				.findProperty(SpiConstants.PROPNAME_LASTMODIFIED);
		String dateString = ((DctmDateValue) prop.nextValue()).toDctmFormat();
		logger.fine("dateString of the checkpoint is "+dateString);
		String result = null;
		try {
			JSONObject jo = new JSONObject();
			jo.put("uuid", uuid);
			jo.put("lastModified", dateString);
			result = jo.toString();
		} catch (JSONException e) {
			logger.severe("Unexpected JSON problem");
			StackTraceElement[] test = e.getStackTrace();
			for (int i = 0; i < test.length; i++) {
				System.out.println(test[i].toString());
			}
			collec.close();
			logger.fine("Collection is closed after JSON problem");
			throw new RepositoryException("Unexpected JSON problem", e);
		} finally {
			if (collec.getState() != ICollection.DF_CLOSED_STATE) {
				logger.finer("Verification of the Collection state : not closed");
				try {
					collec.close();
					
				} catch (RepositoryException e) {
					logger.severe("Error while closing the collection : " + e);
				}
				logger.fine("Collection closed");
			}
		}

		return result;
	}
}
