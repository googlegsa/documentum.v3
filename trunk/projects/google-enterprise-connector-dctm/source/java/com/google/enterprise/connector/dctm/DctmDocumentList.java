package com.google.enterprise.connector.dctm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
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
	
	static{
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
		if(collec.getState() == ICollection.DF_CLOSED_STATE){
			return null;
		}
		if(collec.next()){
			
			String crID = "";
			try {
				crID = collec.getString("r_object_id");
			} catch (RepositoryException e) {
				return null;
			}
			dctmSysobjectDocument = new DctmSysobjectDocument(crID, sessMag, clientX,
					isPublic ? "true" : "false", included_meta, excluded_meta);
			return dctmSysobjectDocument;
		}
		collec.close();
		return null;
	}

	public String checkpoint() throws RepositoryException {
		DctmSysobjectProperty prop = ((DctmSysobjectProperty)(dctmSysobjectDocument.findProperty("r_object_id")));
		String uuid = ((StringValue)prop.nextValue()).toString();
		prop = (DctmSysobjectProperty) dctmSysobjectDocument.findProperty(SpiConstants.PROPNAME_LASTMODIFIED);
		String dateString = ((DctmDateValue)prop.nextValue()).toDctmFormat();
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
			collec.close();
			throw new RepositoryException("Unexpected JSON problem", e);
		}finally{
			if(collec.getState() != ICollection.DF_CLOSED_STATE){
				collec.close();
			}
		}
		
		return result;
	}
}
