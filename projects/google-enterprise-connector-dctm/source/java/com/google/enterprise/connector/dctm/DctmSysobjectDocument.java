// Copyright (C) 2006-2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.dctm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.SimpleProperty;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.SpiConstants.ActionType;
import com.google.enterprise.connector.spi.TraversalContext;
import com.google.enterprise.connector.spi.Value;

public class DctmSysobjectDocument extends HashMap implements Document {
  private static final long serialVersionUID = 126421624L;
  private static final Logger logger =
      Logger.getLogger(DctmSysobjectDocument.class.getName());

  /** The maximum content size that will be allowed. */
  private static final long MAX_CONTENT_SIZE = 30L * 1024 * 1024;

  private String object_id_name = "r_object_id";
  private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

  private String docId;
  private String commonVersionID;
  private ITime timeStamp;

  private ISysObject object = null;
  private ISessionManager sessionManager = null;
  private IClientX clientX;

  private boolean isPublic = false;
  private String versionId;
  private final ActionType action;
  private final Set<String> included_meta;
  private final Checkpoint checkpoint;
  private final TraversalContext traversalContext;

  public DctmSysobjectDocument(String docid, String commonVersionID,
      ITime timeStamp, ISessionManager sessionManager, IClientX clientX,
      boolean isPublic, Set<String> included_meta, ActionType action,
      Checkpoint checkpoint, TraversalContext traversalContext) {
    this.docId = docid;
    this.versionId = commonVersionID;
    this.timeStamp = timeStamp;
    this.sessionManager = sessionManager;
    this.clientX = clientX;
    this.isPublic = isPublic;
    this.included_meta = included_meta;
    this.action = action;
    this.checkpoint = checkpoint;
    this.traversalContext = traversalContext;
  }

  private void fetch() throws RepositoryDocumentException,
      RepositoryLoginException, RepositoryException {

    if (object != null) {
      return;
    }
    ISession session = null;
    try {
      String docbaseName = sessionManager.getDocbaseName();
      session = sessionManager.getSession(docbaseName);
      if (ActionType.ADD.equals(action)) {
        logger.info("Get a session for the docbase " + docbaseName);

        IId id = clientX.getId(docId);
        logger.info("r_object_id of the fetched object is " + docId);

        object = session.getObject(id);
        versionId = object.getId("i_chronicle_id").getId();
        logger.info("i_chronicle_id of the fetched object is " + versionId);

        object.setSessionManager(sessionManager);
      }
    } catch (RepositoryDocumentException rde) {
      // Propagate unmolested.
      throw rde;
    } catch (RepositoryException re) {
      if (checkpoint != null && lostConnection(session)) {
        // We have lost connectivity with the server.
        // Rollback the checkpoint and retry this document later.
        checkpoint.restore();
      }
      throw re;
    } finally {
      if (session != null) {
        sessionManager.release(session);
        logger.fine("session released");
      }
    }
  }

  /**
   * Test connectivity to server.  If we have a session and
   * can verify that session isConnected(), return false.
   * If we cannot verify the session is connected return true.
   */
  private boolean lostConnection(ISession session) {
    try {
      return !session.isConnected();
    } catch (Exception e) {
      logger.warning("Lost connectivity to server : " + e);
      return true;
    }
  }

  public Property findProperty(String name) throws RepositoryDocumentException,
      RepositoryLoginException, RepositoryException {

    LinkedList<Value> values = new LinkedList<Value>();

    logger.fine("In findProperty; name : " + name);
    logger.fine("action : " + action);

    if (ActionType.ADD.equals(action)) {
      fetch();
      if (SpiConstants.PROPNAME_ACTION.equals(name)) {
        values.add(Value.getStringValue(action.toString()));
      } else if (name.equals(SpiConstants.PROPNAME_DOCID)) {
        values.add(Value.getStringValue(versionId));
        logger.fine("property " + SpiConstants.PROPNAME_DOCID + " has the value " + versionId);
      } else if (SpiConstants.PROPNAME_CONTENT.equals(name)) {
        logger.fine("getting the property " + SpiConstants.PROPNAME_CONTENT);
        try {
          if (canIndex(true)) {
            values.add(Value.getBinaryValue(object.getContent()));
            logger.fine("property " + SpiConstants.PROPNAME_CONTENT + " after getContent");
          }
        } catch (RepositoryDocumentException e) {
          // FIXME: In the unlikely event the user only has BROWSE
          // permission for a document, we'll end up here. That's
          // fine, but the google:mimetype property will not be reset
          // to "text/plain" in that case.
          logger.warning("RepositoryDocumentException thrown : " + e + " on getting property : " + name);
        }
      } else if (SpiConstants.PROPNAME_DISPLAYURL.equals(name)) {
        logger.fine("getting the property " + SpiConstants.PROPNAME_DISPLAYURL);
        values.add(Value.getStringValue(sessionManager.getServerUrl() + docId));
        logger.fine("property " + SpiConstants.PROPNAME_DISPLAYURL + " has the value " + sessionManager.getServerUrl() + docId);
      } else if (SpiConstants.PROPNAME_SECURITYTOKEN.equals(name)) {
        logger.fine("getting the property " + SpiConstants.PROPNAME_SECURITYTOKEN);
        try {
          values.add(Value.getStringValue(object.getACLDomain() + " " + object.getACLName()));
          logger.fine("property " + SpiConstants.PROPNAME_SECURITYTOKEN + " has the value " + object.getACLDomain() + " " + object.getACLName());
        } catch (RepositoryDocumentException e) {
          // TODO Auto-generated catch block
          logger.warning("RepositoryDocumentException thrown : " + e + " on getting property : " + name);
        }
      } else if (SpiConstants.PROPNAME_ISPUBLIC.equals(name)) {
        logger.fine("getting the property " + SpiConstants.PROPNAME_ISPUBLIC);
        values.add(Value.getBooleanValue(isPublic));
        logger.fine("property " + SpiConstants.PROPNAME_ISPUBLIC + " set to " + isPublic);
      } else if (SpiConstants.PROPNAME_LASTMODIFIED.equals(name)) {
        logger.fine("getting the property " + SpiConstants.PROPNAME_LASTMODIFIED);
        values.add(Value.getDateValue(getDate("r_modify_date")));
        logger.fine("property " + SpiConstants.PROPNAME_LASTMODIFIED + " has the value " + getDate("r_modify_date"));
      } else if (SpiConstants.PROPNAME_MIMETYPE.equals(name)) {
        logger.fine("getting the property " + SpiConstants.PROPNAME_MIMETYPE);
        try {
          IFormat dctmForm = object.getFormat();
          String mimetype = dctmForm.getMIMEType();
          logger.fine("mimetype of the document " + versionId + " : " + mimetype);
          // Modification in order to index empty documents.
          if (canIndex(false)) {
            values.add(Value.getStringValue(mimetype));
            logger.fine("property " + SpiConstants.PROPNAME_MIMETYPE + " has the value " + mimetype);
          }
        } catch (RepositoryDocumentException e) {
          // TODO Auto-generated catch block
          logger.warning("RepositoryDocumentException thrown : " + e + " on getting property : " + name);
        }
      } else if (object_id_name.equals(name)) {
        logger.fine("getting the property " + object_id_name);
        values.add(Value.getStringValue(docId));
        logger.fine("property " + object_id_name + " has the value " + docId);
      } else if (SpiConstants.PROPNAME_TITLE.equals(name)) {
        logger.fine("getting the property " + SpiConstants.PROPNAME_TITLE);
        values.add(Value.getStringValue(object.getObjectName()));
        logger.fine("property " + SpiConstants.PROPNAME_TITLE + " has the value " + object.getObjectName());
      } else if (name.equals("r_object_type")) {
        logger.fine("getting the property " + name);
        // Retrieves object type and its super type(s).
        for (IType value = object.getType(); value != null; value = value.getSuperType()) {
          String typeName = value.getName();
          logger.fine("property " + name + " has the value " + typeName);
          values.add(Value.getStringValue(typeName));
        }
      } else if (object.findAttrIndex(name) != -1) {
        IAttr attr = object.getAttr(object.findAttrIndex(name));
        logger.finer("the attribute " + name + " is in the position " + object.findAttrIndex(name)+ " in the list of attributes of the fetched object");

        int i = object.getValueCount(name);
        logger.finer("the attribute " + name + " stores " + i + " values ");

        IValue val = null;
        for (int j = 0; j < i; j++) {
          val = object.getRepeatingValue(name, j);
          logger.finer("getting the value of index " + j +" of the attribute " + name);
          try {
            if (attr.getDataType() == IAttr.DM_BOOLEAN) {
              logger.finer("the attribute of index " + j +" is of boolean type");
              values.add(Value.getBooleanValue(val.asBoolean()));
            } else if (attr.getDataType() == IAttr.DM_DOUBLE) {
              logger.finer("the attribute of index " + j +" is of double type");
              values.add(Value.getDoubleValue(val.asDouble()));
            } else if (attr.getDataType() == IAttr.DM_ID) {
              logger.finer("the attribute of index " + j +" is of ID type");
              values.add(Value.getStringValue(val.asId().getId()));
            } else if (attr.getDataType() == IAttr.DM_INTEGER) {
              logger.finer("the attribute of index " + j +" is of integer type");
              values.add(Value.getLongValue(val.asInteger()));
            } else if (attr.getDataType() == IAttr.DM_STRING) {
              logger.finer("the attribute of index " + j +" is of String type");
              values.add(Value.getStringValue(val.asString()));
            } else if (attr.getDataType() == IAttr.DM_TIME) {
              logger.finer("the attribute of index " + j +" is of date type");
              Date date = val.asTime().getDate();
              if (date != null) {
                values.add(Value.getDateValue(getCalendarFromDate(date)));
              }
            }
          } catch (Exception e) {
            logger.warning("exception is thrown when getting the value of index " + j +" of the attribute " + name);
            logger.warning("exception " + e.getMessage());
          }
        }
      } else {
        // No property by that name found.
        return null;
      }
    } else {
      logger.fine("Else delete document; name : " + name);
      if (SpiConstants.PROPNAME_ACTION.equals(name)) {
        values.add(Value.getStringValue(action.toString()));
      } else if (name.equals(SpiConstants.PROPNAME_DOCID)) {
        values.add(Value.getStringValue(versionId));
        logger.fine("property " + SpiConstants.PROPNAME_DOCID + " has the value " + versionId);
      } else if (name.equals(SpiConstants.PROPNAME_LASTMODIFIED)) {
        logger.fine("LastModifiedDate for the deleteCollection");
        Calendar tmpCal = Calendar.getInstance();
        try {
          logger.fine("time stamp" + timeStamp);
          logger.fine("pattern44 : " + timeStamp.getTime_pattern44());
          String timeSt = timeStamp.asString(timeStamp.getTime_pattern44());
          logger.fine("timeSt =" + timeSt);

          Date tmpDt = dateFormat.parse(timeSt);
          tmpCal.setTime(tmpDt);
          logger.fine("tmpDt is " + tmpDt);

        } catch (ParseException e) {
          logger.fine("Error: wrong last modified date");
          tmpCal.setTime(new Date());
        }
        values.add(Value.getDateValue(tmpCal));
        logger.fine("property " + SpiConstants.PROPNAME_LASTMODIFIED + " has the value " + timeStamp);
      } else if (object_id_name.equals(name)) {
        logger.fine("getting the property " + object_id_name);
        values.add(Value.getStringValue(docId));
        logger.fine("property " + object_id_name + " has the value " + docId);
      } else {
        // No property by that name found.
        return null;
      }
    }

    return new SimpleProperty(values);
  }

  /**
   * Return true if the obect's content should be supplied for indexing.
   */
  private boolean canIndex(boolean logging) throws RepositoryException {
    // Don't send content that is too big or too small.
    long contentSize = object.getContentSize();
    long maxContentSize = (traversalContext != null) ? traversalContext.maxDocumentSize() : MAX_CONTENT_SIZE;
    if (contentSize <= 0) {
      if (logging) {
        logger.fine("this object has no content");
      }
      return false;
    }
    if (contentSize > maxContentSize) {
      if (logging) {
        logger.fine("content is too large: " + contentSize);
      }
      return false;
    }
    // Don't send content whose mimetype is not supported.
    IFormat format = object.getFormat();
    String mimetype = format.getMIMEType();
    if (traversalContext != null) {
      if (traversalContext.mimeTypeSupportLevel(mimetype) <= 0) {
        if (logging) {
          logger.fine("unindexable content format: " + format.getName());
        }
        return false;
      }
    } else if (!format.canIndex()) {
      if (logging) {
        logger.fine("unindexable content format: " + format.getName());
      }
      return false;
    }
    return true;
  }

  private Calendar getCalendarFromDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }

  public Set<String> getPropertyNames() throws RepositoryException {
    Set<String> properties = null;
    if (ActionType.ADD.equals(action)) {
      logger.fine("fetching the object");
      fetch();
      properties = new HashSet<String>();
      properties.add(SpiConstants.PROPNAME_DISPLAYURL);
      properties.add(SpiConstants.PROPNAME_ISPUBLIC);
      properties.add(SpiConstants.PROPNAME_LASTMODIFIED);
      properties.add(SpiConstants.PROPNAME_MIMETYPE);
      properties.add(SpiConstants.PROPNAME_TITLE);
      try {
        for (int i = 0; i < object.getAttrCount(); i++) {
          IAttr curAttr = object.getAttr(i);
          String name = curAttr.getName();
          logger.finest("pass the attribute " + name);
          if (included_meta.contains(name)) {
            properties.add(name);
            logger.finest("attribute " + name + " added to the properties");
          }
        }
      } catch (RepositoryDocumentException e) {
        // TODO Auto-generated catch block
        logger.log(Level.WARNING, "Error fetching property names", e);
      }
    } else {
      properties = new HashSet<String>();
      properties.add(SpiConstants.PROPNAME_ACTION);
      properties.add(SpiConstants.PROPNAME_DOCID);
      properties.add("r_object_id");
    }
    return properties;
  }

  public Calendar getDate(String name) throws RepositoryDocumentException {
    logger.finest("in getDate");
    if (object != null) {
      return getCalendarFromDate(object.getTime(name).getDate());
    } else {
      throw new RepositoryDocumentException();
    }
  }

  protected ITime getLastModifDate() {
    return timeStamp;
  }
}
