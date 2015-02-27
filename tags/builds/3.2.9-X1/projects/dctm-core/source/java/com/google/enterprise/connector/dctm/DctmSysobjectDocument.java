// Copyright 2007 Google Inc.
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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
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
import com.google.enterprise.connector.spi.SkippedDocumentException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.SpiConstants.ActionType;
import com.google.enterprise.connector.spi.TraversalContext;
import com.google.enterprise.connector.spi.Value;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DctmSysobjectDocument implements Document {
  private static final Logger logger =
      Logger.getLogger(DctmSysobjectDocument.class.getName());

  /** The maximum content size that will be allowed. */
  private static final long MAX_CONTENT_SIZE = 30L * 1024 * 1024;

  /* @VisibleForTesting */
  static final String OBJECT_ID_NAME = "r_object_id";

  /**
   * Optional properties from Documentum that are not truly attributes.
   * Values include "r_object_id" and "google:folder".
   */
  public static final Set<String> EXTENDED_PROPERTIES =
      ImmutableSet.of(OBJECT_ID_NAME, SpiConstants.PROPNAME_FOLDER);

  /**
   * A record of logged requests for unsupported SPI properties so we
   * don't spam the logs.
   */
  @VisibleForTesting
  static Set<String> UNSUPPORTED_PROPNAMES = new HashSet<String>();

  private final DctmTraversalManager traversalManager;
  private final ISession session;
  private final String docId;
  private String versionId;
  private ITime timeStamp;
  private final ActionType action;
  private final Checkpoint checkpoint;

  private ISysObject object;

  public DctmSysobjectDocument(DctmTraversalManager traversalManager,
      ISession session, String docid, String commonVersionID, ITime timeStamp,
      ActionType action, Checkpoint checkpoint) {
    this.traversalManager = traversalManager;
    this.session = session;
    this.docId = docid;
    this.versionId = commonVersionID;
    this.timeStamp = timeStamp;
    this.action = action;
    this.checkpoint = checkpoint;
  }

  private void fetch() throws RepositoryDocumentException,
      RepositoryLoginException, RepositoryException {
    if (object != null || !ActionType.ADD.equals(action)) {
      return;
    }
    try {
      IId id = traversalManager.getClientX().getId(docId);

      object = (ISysObject) session.getObject(id);
      if (versionId == null || versionId.length() == 0) {
        versionId = object.getId("i_chronicle_id").getId();
      }
      logger.fine("i_chronicle_id of the fetched object is " + versionId);

      if (timeStamp == null) {
        timeStamp = object.getTime("r_modify_date");
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
      logger.warning("Lost connectivity to server: " + e);
      return true;
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation looks up the properties when this method
   * is called.
   */
  @Override
  public Property findProperty(String name) throws RepositoryDocumentException,
      RepositoryLoginException, RepositoryException {
    if (name == null || name.length() == 0)
      return null;

    List<Value> values = new LinkedList<Value>();

    if (logger.isLoggable(Level.FINEST))
      logger.finest("In findProperty; name: " + name);

    if (ActionType.ADD.equals(action)) {
      fetch();
      boolean found = findCoreProperty(name, values);
      if (!found)
        found = findAddProperty(name, values);
      if (!found)
        return null;
    } else {
      boolean found = findCoreProperty(name, values);
      if (!found)
        return null;
    }

    if (logger.isLoggable(Level.FINE)) {
      logger.fine("property " + name + " has the values " + values);
    }
    return values.isEmpty() ? null : new SimpleProperty(values);
  }

  /**
   * Adds the values for the named property to the list. The
   * properties handled by this method are always available, for both
   * add and delete actions.
   *
   * @param name a property name
   * @param values the empty list to add values to
   * @return true if the property exists
   * @throws RepositoryException if an unexpected error occurs
   */
  private boolean findCoreProperty(String name, List<Value> values)
      throws RepositoryException {
    if (SpiConstants.PROPNAME_ACTION.equals(name)) {
      values.add(Value.getStringValue(action.toString()));
    } else if (name.equals(SpiConstants.PROPNAME_DOCID)) {
      values.add(Value.getStringValue(versionId));
    } else if (name.equals(SpiConstants.PROPNAME_LASTMODIFIED)) {
      if (timeStamp == null) {
        return false;
      }
      values.add(Value.getDateValue(
          getCalendarFromDate(timeStamp.getDate())));
    } else {
      // No property by that name found.
      return false;
    }
    return true;
  }

  /**
   * Adds the values for the named property to the list. The
   * properties handled by this method are available only for the add
   * action. A fetched SysObject is used to obtain the values.
   *
   * @param name a property name
   * @param values the empty list to add values to
   * @return true if the property exists
   * @throws RepositoryException if an unexpected error occurs
   */
  /* TODO: Should we unify the RepositoryDocumentException handling? */
  private boolean findAddProperty(String name, List<Value> values)
      throws RepositoryException {
    if (SpiConstants.PROPNAME_CONTENT.equals(name)) {
      try {
        if (canIndex(true)) {
          values.add(Value.getBinaryValue(object.getContent()));
        }
      } catch (RepositoryDocumentException e) {
        // FIXME: In the unlikely event the user only has BROWSE
        // permission for a document, we'll end up here. That's
        // fine, but the google:mimetype property will not be reset
        // to "text/plain" in that case.
        logger.warning("RepositoryDocumentException thrown: " + e
            + " on getting property: " + name);
      }
    } else if (SpiConstants.PROPNAME_DISPLAYURL.equals(name)) {
      String displayUrl = traversalManager.getServerUrl() + docId;
      values.add(Value.getStringValue(displayUrl));
    } else if (SpiConstants.PROPNAME_FOLDER.equals(name)) {
      return findFolderProperty(name, values);
    } else if (SpiConstants.PROPNAME_ISPUBLIC.equals(name)) {
      values.add(Value.getBooleanValue(traversalManager.isPublic()));
    } else if (SpiConstants.PROPNAME_MIMETYPE.equals(name)) {
      try {
        IFormat dctmForm = object.getFormat();
        String mimetype = dctmForm.getMIMEType();
        logger.fine("mimetype of the document " + versionId + ": " + mimetype);
        // The GSA will not index empty documents with binary content types,
        // so omit the content type if the document cannot be indexed.
        if (canIndex(false)) {
          values.add(Value.getStringValue(mimetype));
        }
      } catch (RepositoryDocumentException e) {
        logger.warning("RepositoryDocumentException thrown: " + e
            + " on getting property: " + name);
      }
    } else if (SpiConstants.PROPNAME_TITLE.equals(name)) {
      values.add(Value.getStringValue(object.getObjectName()));
    } else if (SpiConstants.PROPNAME_ACLINHERITFROM_DOCID.equals(name)) {
      values.add(Value.getStringValue(object.getAclId().getId()));
    } else if (name.startsWith(SpiConstants.RESERVED_PROPNAME_PREFIX)) {
      if (UNSUPPORTED_PROPNAMES.add(name)) {
        logger.finest("Ignoring unsupported SPI property " + name);
      }
      return false;
    } else {
      return findDctmAttribute(name, values);
    }

    return true;
  }

  /**
   * Adds the values for the folder paths to the list.
   *
   * @param name a property name
   * @param values the empty list to add values to
   * @return true if the property exists
   * @throws RepositoryException if an unexpected error occurs
   */
  private boolean findFolderProperty(String name, List<Value> values)
      throws RepositoryException {
    // We have already done a fetch of this object, so we read
    // i_folder_id directly rather than doing a subquery or join
    // on the object ID.
    int count = object.getValueCount("i_folder_id");
    if (count == 0)
      return false;

    StringBuilder dql = new StringBuilder();
    dql.append("select r_folder_path from dm_folder ");
    dql.append("where r_folder_path is not null and r_object_id in (");
    for (int i = 0; i < count; i++) {
      dql.append('\'');
      dql.append(object.getRepeatingValue("i_folder_id", i).asString());
      dql.append("',");
    }
    dql.setCharAt(dql.length() - 1, ')');
    dql.append(" ENABLE (row_based)");

    IQuery query = traversalManager.getClientX().getQuery();
    query.setDQL(dql.toString());
    try {
      ICollection collec = query.execute(session, IQuery.READ_QUERY);
      try {
        while (collec.next()) {
          values.add(
              Value.getStringValue(collec.getString("r_folder_path")));
        }
      } finally {
        collec.close();
      }
    } catch (RepositoryException e) {
      // Note that we're catching RepositoryException here, because
      // we're using ICollection and not ISysObject.
      logger.warning("RepositoryException thrown: " + e
          + " on getting property: " + name);
    }

    return true;
  }

  /**
   * Adds the values for a Documentum attribute to the list.
   *
   * @param name a attribute name
   * @param values the empty list to add values to
   * @return true if the attribute exists
   * @throws RepositoryException if an unexpected error occurs
   */
  private boolean findDctmAttribute(String name, List<Value> values)
      throws RepositoryException {
    if (OBJECT_ID_NAME.equals(name)) {
      values.add(Value.getStringValue(docId));
    } else if (name.equals("r_object_type")) {
      // Retrieves object type and its super type(s).
      for (IType value = object.getType();
           value != null;
           value = getSuperType(value)) {
        String typeName = value.getName();
        values.add(Value.getStringValue(typeName));
      }
    } else {
      // TODO: We could store the data types for each attribute in
      // the type attributes cache, and save about 2% of the
      // traversal time here by avoiding the calls to findAttrIndex
      // and getAttr.
      int attrIndex = object.findAttrIndex(name);
      if (attrIndex != -1) {
        IAttr attr = object.getAttr(attrIndex);
        getDctmAttribute(name, attr.getDataType(), values);
      } else {
        // No property by that name found.
        return false;
      }
    }

    return true;
  }

  /**
   * Helper method that values for a Documentum attribute to the list.
   *
   * @param name an attribute name
   * @param dataType the data type of the attribute
   * @param values the empty list to add values to
   * @throws RepositoryException if an unexpected error occurs
   */
  private void getDctmAttribute(String name, int dataType, List<Value> values)
      throws RepositoryException {
    for (int i = 0, n = object.getValueCount(name); i < n; i++) {
      IValue val = object.getRepeatingValue(name, i);
      try {
        switch (dataType) {
          case IAttr.DM_BOOLEAN:
            values.add(Value.getBooleanValue(val.asBoolean()));
            break;
          case IAttr.DM_DOUBLE:
            values.add(Value.getDoubleValue(val.asDouble()));
            break;
          case IAttr.DM_ID:
            // TODO: Should we check for null here?
            values.add(Value.getStringValue(val.asId().getId()));
            break;
          case IAttr.DM_INTEGER:
            values.add(Value.getLongValue(val.asInteger()));
            break;
          case IAttr.DM_STRING:
            values.add(Value.getStringValue(val.asString()));
            break;
          case IAttr.DM_TIME:
            Date date = val.asTime().getDate();
            if (date != null) {
              values.add(Value.getDateValue(getCalendarFromDate(date)));
            }
            break;
          default:
            // TODO: Should this be an exception, or just logged
            // directly as a warning?
            throw new AssertionError(String.valueOf(dataType));
        }
      } catch (Exception e) {
        logger.log(Level.WARNING, "error getting the value of index "
            + i + " of the attribute " + name, e);
      }
    }
  }

  /**
   * Return the supertype for the supplied type.  Caches result to
   * avoid frequent round-trips to server.
   *
   * @return superType for supplied type, or null if type is root type.
   */
  private IType getSuperType(IType type) throws RepositoryException {
    if (type == null)
      return null;
    Map<String, IType> superTypes = traversalManager.getSuperTypeCache();
    String typeName = type.getName();
    if (superTypes.containsKey(typeName))
      return superTypes.get(typeName);
    else {
      IType superType = type.getSuperType();
      superTypes.put(typeName, superType);
      return superType;
    }
  }

  /**
   * Return true if the obect's content should be supplied for indexing.
   */
  private boolean canIndex(boolean logging) throws RepositoryException {
    // Don't send content that is too big or too small.
    long contentSize = object.getContentSize();
    TraversalContext traversalContext = traversalManager.getTraversalContext();

    // Don't send content whose mimetype is not supported.
    IFormat format = object.getFormat();
    String mimetype = format.getMIMEType();
    if (traversalContext != null) {
      int supportLevel = traversalContext.mimeTypeSupportLevel(mimetype);
      if (supportLevel < 0) {
        if (logging) {
          logger.fine("excluded content format: " + format.getName());
        }
        throw new SkippedDocumentException("Excluded by content type: "
                                           + mimetype);
      }
      if (supportLevel == 0) {
        if (logging) {
          logger.fine("unindexable content format: " + format.getName());
        }
        return false;
      }
      if (contentSize > traversalContext.maxDocumentSize()) {
        if (logging) {
          logger.fine("content is too large: " + contentSize);
        }
        return false;
      }
    } else {
      if (!format.canIndex()) {
        if (logging) {
          logger.fine("unindexable content format: " + format.getName());
        }
        return false;
      }
      if (contentSize > MAX_CONTENT_SIZE) {
        if (logging) {
          logger.fine("content is too large: " + contentSize);
        }
        return false;
      }
    }
    if (contentSize <= 0) {
      if (logging) {
        logger.fine("this object has no content");
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

  /**
   * Return the attributes for the supplied type.  Caches result to
   * avoid frequent round-trips to server.
   *
   * @param type the object type to get attributes for
   * @return type attributes for the supplied type
   * @throws RepositoryException if an error occurs retrieving the attributes
   */
  private List<String> getTypeAttributes(IType type)
      throws RepositoryException {
    Map<String, List<String>> cache = traversalManager.getTypeAttributesCache();
    String typeName = type.getName();
    if (cache.containsKey(typeName)) {
      return cache.get(typeName);
    } else {
      if (logger.isLoggable(Level.FINER)) {
        logger.finer("Processing attributes for type " + typeName);
      }

      int count = type.getTypeAttrCount();
      ArrayList<String> typeAttributes = new ArrayList<String>(count);

      Set<String> includedMeta = traversalManager.getIncludedMeta();
      Set<String> excludedMeta = traversalManager.getExcludedMeta();
      try {
        for (int i = 0; i < count; i++) {
          String name = type.getTypeAttrNameAt(i);
          addIfIncluded(typeAttributes, name, includedMeta, excludedMeta);
        }

        for (String name : EXTENDED_PROPERTIES) {
          addIfIncluded(typeAttributes, name, includedMeta, excludedMeta);
        }
      } catch (RepositoryDocumentException e) {
        logger.log(Level.WARNING, "Error fetching property names", e);
      }

      cache.put(typeName, typeAttributes);
      return typeAttributes;
    }
  }

  private void addIfIncluded(List<String> typeAttributes, String name,
      Set<String> includedMeta, Set<String> excludedMeta) {
    if ((includedMeta.isEmpty() || includedMeta.contains(name)) &&
        !excludedMeta.contains(name)) {
      typeAttributes.add(name);
      if (logger.isLoggable(Level.FINEST)) {
        logger.finest("attribute " + name + " added to the properties");
      }
    } else {
      if (logger.isLoggable(Level.FINEST)) {
        logger.finest("attribute " + name
            + " excluded from the properties");
      }
    }
  }

  @Override
  public Set<String> getPropertyNames() throws RepositoryException {
    Set<String> properties;
    if (ActionType.ADD.equals(action)) {
      fetch();
      properties = new HashSet<String>();
      properties.add(SpiConstants.PROPNAME_DISPLAYURL);
      properties.add(SpiConstants.PROPNAME_ISPUBLIC);
      properties.add(SpiConstants.PROPNAME_LASTMODIFIED);
      properties.add(SpiConstants.PROPNAME_MIMETYPE);
      properties.add(SpiConstants.PROPNAME_TITLE);
      properties.add(SpiConstants.PROPNAME_ACLINHERITFROM_DOCID);

      List<String> typeAttributes = getTypeAttributes(object.getType());
      properties.addAll(typeAttributes);
    } else {
      // XXX: This is dead code. The CM never asks for the property
      // names for delete actions. Does it matter?
      properties = new HashSet<String>();
      properties.add(SpiConstants.PROPNAME_ACTION);
      properties.add(SpiConstants.PROPNAME_DOCID);
      properties.add(SpiConstants.PROPNAME_LASTMODIFIED);
    }
    return properties;
  }
}
