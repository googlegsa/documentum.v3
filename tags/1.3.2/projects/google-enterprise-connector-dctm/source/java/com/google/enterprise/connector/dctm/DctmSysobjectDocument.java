package com.google.enterprise.connector.dctm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryLoginException;
///import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spiimpl.BinaryValue;
import com.google.enterprise.connector.spiimpl.BooleanValue;
import com.google.enterprise.connector.spiimpl.DoubleValue;
import com.google.enterprise.connector.spiimpl.LongValue;
import com.google.enterprise.connector.spiimpl.StringValue;

public class DctmSysobjectDocument extends HashMap implements Document {

	private static final long serialVersionUID = 126421624L;

	/** The maximum content size that will be allowed. */
	private static final long MAX_CONTENT_SIZE = 30L * 1024 * 1024;

	private String docId;
	private String commonVersionID;
	
	private ITime timeStamp;

	private ISysObject object = null;

	private ISessionManager sessionManager = null;

	private IClientX clientX;

	private String isPublic = "false";

	private String versionId;

	private SpiConstants.ActionType action;

	private HashSet included_meta;

	private HashSet excluded_meta;

	private String object_id_name = "r_object_id";

	private ITime lastModifDate;

	private static Logger logger = null;
	
	static {
		logger = Logger.getLogger(DctmSysobjectDocument.class.getName());
	}

	public DctmSysobjectDocument(String docid, ITime lastModifDate, ISessionManager sessionManager,
			IClientX clientX, String isPublic, HashSet included_meta,
			HashSet excluded_meta,SpiConstants.ActionType action) {
		this.docId = docid;
		this.sessionManager = sessionManager;
		this.clientX = clientX;
		this.isPublic = isPublic;
		this.included_meta = included_meta;
		this.excluded_meta = excluded_meta;
		this.lastModifDate = lastModifDate;
		this.action = action;
	}

	public DctmSysobjectDocument(String docid, String commonVersionID, ITime timeStamp, ISessionManager sessionManager,
			IClientX clientX, String isPublic, HashSet included_meta,
			HashSet excluded_meta,SpiConstants.ActionType action) {
		this.docId = docid;
		this.versionId = commonVersionID;
		this.timeStamp = timeStamp;
		this.sessionManager = sessionManager;
		this.clientX = clientX;
		this.isPublic = isPublic;
		this.included_meta = included_meta;
		this.excluded_meta = excluded_meta;
		this.action = action;
	}

	private void fetch() throws RepositoryDocumentException, RepositoryLoginException, RepositoryException{
		if (object != null) {
			return;
		}
		ISession session = null;
		
		try {
			String docbaseName = sessionManager.getDocbaseName();
			session = sessionManager.getSession(docbaseName);
			
			if (SpiConstants.ActionType.ADD.equals(action)) {
				logger.info("Get a session for the docbase "+docbaseName);

				IId id = clientX.getId(docId);

				logger.info("r_object_id of the fetched object is "+docId);

				object = session.getObject(id);

				versionId = object.getId("i_chronicle_id").getId();

				logger.info("i_chronicle_id of the fetched object is "+versionId);

				object.setSessionManager(sessionManager);
			}
		} finally {
			if (session != null) {
				sessionManager.release(session);
				logger.fine("session released");
			}
		}
		
	}

	public Property findProperty(String name) throws RepositoryDocumentException, RepositoryLoginException, RepositoryException{
		IFormat dctmForm = null;
		String mimetype = "";
		String dosExtension= "";
		long contentSize=0;
		HashSet hashSet;
		hashSet = new HashSet();
		String timeSt = null;

		logger.fine("In findProperty; name : " + name);
		logger.fine("action : "+action);

		if (SpiConstants.ActionType.ADD.equals(action)) {
				fetch();
				if (SpiConstants.PROPNAME_ACTION.equals(name)) {
					hashSet.add(new StringValue(action.toString()));
					return new DctmSysobjectProperty(name, hashSet);
				}else if (name.equals(SpiConstants.PROPNAME_DOCID)) {
					hashSet.add(new StringValue(versionId));
					logger.fine("property "+SpiConstants.PROPNAME_DOCID+" has the value "+versionId);
					return new DctmSysobjectProperty(name, hashSet);
				} else if (SpiConstants.PROPNAME_CONTENT.equals(name)) {
					logger.fine("getting the property "+SpiConstants.PROPNAME_CONTENT);
					try {
						contentSize = object.getContentSize();
						if (contentSize == 0) {
							hashSet.add(null);
							logger.fine("this object has no content");
						} else if (contentSize > MAX_CONTENT_SIZE) {
							hashSet.add(null);
							logger.fine("content is too large: " + contentSize);
						} else {
							IFormat format = object.getFormat();
							if (!format.canIndex()) {
								hashSet.add(null);
								logger.fine("unindexable content format: " + format.getName());
							} else {
								hashSet.add(new BinaryValue(object.getContent()));
								logger.fine("property "+SpiConstants.PROPNAME_CONTENT+" after getContent");
							 }
						}
					} catch (RepositoryDocumentException e) {
						// TODO Auto-generated catch block
						logger.warning("RepositoryDocumentException thrown : "+ e+" on getting property : "+name);
						hashSet.add(null);
					}
					
					return new DctmSysobjectProperty(name, hashSet);
	
				} else if (SpiConstants.PROPNAME_DISPLAYURL.equals(name)) {
					logger.fine("getting the property "+SpiConstants.PROPNAME_DISPLAYURL);
					hashSet.add(new StringValue(sessionManager.getServerUrl() + docId));
					logger.fine("property "+SpiConstants.PROPNAME_DISPLAYURL+" has the value "+sessionManager.getServerUrl() + docId);
					return new DctmSysobjectProperty(name, hashSet);
				} else if (SpiConstants.PROPNAME_SECURITYTOKEN.equals(name)) {
						logger.fine("getting the property "+SpiConstants.PROPNAME_SECURITYTOKEN);
						try {
							hashSet.add(new StringValue(object.getACLDomain() + " " + object.getACLName()));
							logger.fine("property "+SpiConstants.PROPNAME_SECURITYTOKEN+" has the value "+object.getACLDomain() + " "+ object.getACLName());
                        }catch (RepositoryDocumentException e) {
							// TODO Auto-generated catch block
                        	logger.warning("RepositoryDocumentException thrown : "+ e+" on getting property : "+name);
                        	hashSet.add(null);
						}
					return new DctmSysobjectProperty(name, hashSet);
				} else if (SpiConstants.PROPNAME_ISPUBLIC.equals(name)) {
					logger.fine("getting the property "+SpiConstants.PROPNAME_ISPUBLIC);
					hashSet.add(BooleanValue.makeBooleanValue(this.isPublic
							.equals("true")));
					logger.fine("property "+SpiConstants.PROPNAME_ISPUBLIC+" set to true");
					return new DctmSysobjectProperty(name, hashSet);
				} else if (SpiConstants.PROPNAME_LASTMODIFIED.equals(name)) {
				
						logger.fine("getting the property "+SpiConstants.PROPNAME_LASTMODIFIED);
						
						hashSet.add(new DctmDateValue(getDate("r_modify_date")));
						logger.fine("property "+SpiConstants.PROPNAME_LASTMODIFIED+" has the value "+getDate("r_modify_date"));
						
					return new DctmSysobjectProperty(name, hashSet);
				} else if (SpiConstants.PROPNAME_MIMETYPE.equals(name)) {
					
						logger.fine("getting the property "+SpiConstants.PROPNAME_MIMETYPE);
						try {
							dctmForm = object.getFormat();
							mimetype = dctmForm.getMIMEType();
							dosExtension = dctmForm.getDOSExtension();
							contentSize= object.getContentSize();
							///modification in order to index empty documents
							if (contentSize == 0 || contentSize > MAX_CONTENT_SIZE || !dctmForm.canIndex()) {
								hashSet.add(null);
							} else {
								hashSet.add(new StringValue(mimetype));
								logger.fine("property "+SpiConstants.PROPNAME_MIMETYPE+" has the value "+mimetype);
							}
							logger.fine("mimetype of the document "+versionId+" : "+mimetype);
							logger.fine("dosExtension of the document "+versionId+" : "+dosExtension);
							logger.fine("contentSize of the document "+versionId+" : "+contentSize);
						} catch (RepositoryDocumentException e) {
							// TODO Auto-generated catch block
							logger.warning("RepositoryDocumentException thrown : "+ e+" on getting property : "+name);
							hashSet.add(null);
						}
					return new DctmSysobjectProperty(name, hashSet);
				} else if (SpiConstants.PROPNAME_SEARCHURL.equals(name)) {
					return null;
				} else if (object_id_name.equals(name)) {
					logger.fine("getting the property "+object_id_name);
					hashSet.add(new StringValue(docId));
					logger.fine("property "+object_id_name+" has the value "+docId);
					return new DctmSysobjectProperty(name, hashSet);
				} else if (SpiConstants.PROPNAME_TITLE.equals(name)) {
					logger.fine("getting the property " + SpiConstants.PROPNAME_TITLE);
					hashSet.add(new StringValue(object.getObjectName()));
					logger.fine("property " + SpiConstants.PROPNAME_TITLE + " has the value " + object.getObjectName());
					return new DctmSysobjectProperty(name, hashSet);
				}

			
					
				if (object.findAttrIndex(name)!=-1){
					IAttr attr = object.getAttr(object.findAttrIndex(name));
					logger.finer("the attribute "+ name + " is in the position "+ object.findAttrIndex(name)+ " in the list of attributes of the fetched object");
	
					int i = object.getValueCount(name);
					logger.finer("the attribute "+ name + " stores "+ i + " values ");
	
					IValue val = null;
					for (int j = 0; j < i; j++) {
						val = object.getRepeatingValue(name, j);
						logger.finer("getting the value of index "+ j +" of the attribute "+ name);
						try {				
							if (attr.getDataType() == IAttr.DM_BOOLEAN) {
								logger.finer("the attribute of index "+ j +" is of boolean type");
								hashSet.add(BooleanValue.makeBooleanValue(val.asBoolean()));
							} else if (attr.getDataType() == IAttr.DM_DOUBLE) {
								logger.finer("the attribute of index "+ j +" is of double type");
								hashSet.add(new DoubleValue(val.asDouble()));
							} else if (attr.getDataType() == IAttr.DM_ID) {
								logger.finer("the attribute of index "+ j +" is of ID type");
								hashSet.add(new StringValue(val.asId().getId()));
							} else if (attr.getDataType() == IAttr.DM_INTEGER) {
								logger.finer("the attribute of index "+ j +" is of integer type");
								hashSet.add(new LongValue(val.asInteger()));
							} else if (attr.getDataType() == IAttr.DM_STRING) {
								logger.finer("the attribute of index "+ j +" is of String type");
								hashSet.add(new StringValue(val.asString()));
							} else if (attr.getDataType() == IAttr.DM_TIME) {
								logger.finer("the attribute of index "+ j +" is of date type");
								hashSet.add(new DctmDateValue(getCalendarFromDate(val.asTime().getDate())));
							}
	
						} catch (Exception e) {
							logger.warning("exception is thrown when getting the value of index "+ j +" of the attribute "+ name);
							logger.warning("exception "+e.getMessage());
							hashSet.add(null);
							///logger.fine("null value added to the hashset");
						}
	
					}
				
				}
			

		}else{
			logger.fine("Else delete document; name : " + name);
			if (SpiConstants.PROPNAME_ACTION.equals(name)) {
				hashSet.add(new StringValue(action.toString()));
				return new DctmSysobjectProperty(name, hashSet);
			}else if (name.equals(SpiConstants.PROPNAME_DOCID)) {
				hashSet.add(new StringValue(versionId));
				logger.fine("property "+SpiConstants.PROPNAME_DOCID+" has the value "+versionId);
				return new DctmSysobjectProperty(name, hashSet);
			}else if (name.equals(SpiConstants.PROPNAME_LASTMODIFIED)) {
				logger.fine("LastModifiedDate for the deleteCollection");
				Calendar tmpCal = Calendar.getInstance();
				try {
					logger.fine("time stamp" + timeStamp);
					logger.fine("pattern44 : "+timeStamp.getTime_pattern44());
					timeSt = timeStamp.asString(timeStamp.getTime_pattern44());
					logger.fine("timeSt ="+timeSt);
					Date tmpDt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(timeSt);
					tmpCal.setTime(tmpDt);
					logger.fine("tmpDt is "+tmpDt);
				} catch (ParseException e) {
					logger.fine("Error: wrong last modified date");
					tmpCal.setTime(new Date());
				}
				hashSet.add(new DctmDateValue(tmpCal));
				logger.fine("property "+SpiConstants.PROPNAME_LASTMODIFIED+" has the value "+timeStamp);
				return new DctmSysobjectProperty(name, hashSet);
			}else if (object_id_name.equals(name)) {
				logger.fine("getting the property "+object_id_name);
				hashSet.add(new StringValue(docId));
				logger.fine("property "+object_id_name+" has the value "+docId);
				return new DctmSysobjectProperty(name, hashSet);
			}
		}		



		return new DctmSysobjectProperty(name, hashSet);

	}

	private Calendar getCalendarFromDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public Set getPropertyNames() throws RepositoryDocumentException, RepositoryLoginException, RepositoryException{
		HashSet properties=null;
		if (SpiConstants.ActionType.ADD.equals(action)) {
			logger.fine("fetching the object");
			fetch();
			properties = new HashSet();
			properties.add(SpiConstants.PROPNAME_DISPLAYURL);
			properties.add(SpiConstants.PROPNAME_ISPUBLIC);
			properties.add(SpiConstants.PROPNAME_LASTMODIFIED);
			properties.add(SpiConstants.PROPNAME_MIMETYPE);
			properties.add(SpiConstants.PROPNAME_TITLE);

			try {
				for (int i = 0; i < object.getAttrCount(); i++) {
					IAttr curAttr = object.getAttr(i);
					String name = curAttr.getName();
					logger.finest("pass the attribute "+name);
					if (!excluded_meta.contains(name) || included_meta.contains(name)) {
						properties.add(name);
						logger.finest("attribute "+name+" added to the properties");
					}
				}
			} catch (RepositoryDocumentException e) {
				// TODO Auto-generated catch block
				logger.warning("RepositoryDocumentException thrown : "+ e);
			}

		} else {
			properties = new HashSet();
			properties.add(SpiConstants.PROPNAME_ACTION);
			properties.add(SpiConstants.PROPNAME_DOCID);
			properties.add("r_object_id");
		}
		return properties;
	}

	public Calendar getDate(String name) throws RepositoryDocumentException {
		logger.finest("in getDate");
		if (object != null){
			Date date = object.getTime(name).getDate();
			logger.finest("Date : "+date);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar;
		}else{
			throw new RepositoryDocumentException();
			
		}
		
	}

	protected ITime getLastModifDate() {
		return lastModifDate;
	}
}
