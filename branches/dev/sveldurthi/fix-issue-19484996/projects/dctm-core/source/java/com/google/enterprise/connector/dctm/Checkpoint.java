// Copyright 2009 Google Inc.
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

import com.google.enterprise.connector.spi.RepositoryException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Create and parse the checkpoint strings passed back and forth between
 * the connector and the Google appliance traverser.
 */
class Checkpoint {
  /** The logger for this class. */
  private static final Logger LOGGER =
      Logger.getLogger(Checkpoint.class.getName());

  /** The array index for checkpoints supporting multiple where clauses. */
  private static final String INS_INDEX = "index";

  /** The JSON insertion ID, based on dm_sysobject.r_object_id. */
  private static final String INS_ID = "uuid";

  /** The JSON insertion timestamp, based on dm_sysobject.r_modify_date. */
  private static final String INS_DATE = "lastModified";

  /** The JSON deletion ID, based on dm_audittrail.r_object_id. */
  private static final String DEL_ID = "uuidToRemove";

  /** The JSON deletion timestamp, based on dm_audittrail.time_stamp_utc. */
  private static final String DEL_DATE = "lastRemoved";

  /** The old JSON deletion timestamp, based on dm_audittrail.time_stamp. */
  private static final String DEL_DATE_OLD = "lastRemoveDate";

  /** The JSON insertion ID, based on dm_acl.r_object_id. */
  private static final String ACL_ID = "aclid";

  /** The JSON insertion time stamp, based on 
   * dm_audittrail_acl.time_stamp_utc. */
  private static final String ACL_MODIFY_DATE = "aclLastModified";

  /** The JSON deletion ID, based on dm_audittrail_acl.r_object_id. */
  private static final String ACL_MODIFY_ID = "acluuid";

  /**
   * The offset for migrating deletion timestamps from time_stamp to
   * time_stamp_utc, where time_stamp + time_stamp_offset =
   * time_stamp_utc.
   *
   * The actual offset between time_stamp and time_stamp_utc is
   * somewhere between -14 hours (UTC+1400) and +10 hours (UTC-1000),
   * and it might be zero in the case of Documentum 6 with
   * dm_docbase_config.r_normal_tz = 0. Rather than computing the
   * correct offset, we are just offsetting the given time_stamp value
   * to be before any possible time_stamp_utc value. For correctness,
   * we could just subtract the largest positive UTC offset (+1400 in
   * Kiribati), but we're going back a full day to avoid trouble. In
   * the worst case, this will redo about 34 hours of deletions (for
   * the largest negative UTC offset of -1000 running Documentum 5).
   */
  private static final long TIME_STAMP_OFFSET = -24 * 60 * 60 * 1000L;

  /** The formatter used for the Date portions of checkpoints. */
  private final SimpleDateFormat dateFormat =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /** The index into the insert arrays for this checkpoint. */
  private int insertIndex;

  /** The starting index into the insert arrays for this checkpoint. */
  private int startInsertIndex;

  /** The size of the insert index cycle: the number of where clauses. */
  private final int insertIndexModulus;

  /** The index into the insert arrays for future checkpoints. */
  private int nextInsertIndex;

  /** r_object_id of the last ACL inserted. */
  private String aclId;

  /** r_object_id of the last item inserted. */
  private List<String> insertId;

  /** r_modify_date of the last item inserted. */
  private List<String> insertDate;

  /** dm_audittrail_acl time_stamp_utc of last ACL modification */
  private String aclModifyDate;

  /** r_object_id of the last item deleted. */
  private String deleteId;

  /** r_object_id of the last ACL modified */
  private String aclModifyId;

  /**
   * dm_audittrail time_stamp_utc of the last item deleted. Note that
   * the stored date is UTC, but the value of this field may not be,
   * due to the way DQL handles time zones.
   */
  private String deleteDate;

  /**
   * Backup versions of the ACL, insert and delete checkpoints,
   * so that we may restore a checkpoint when attempting to
   * retry failed items.
   */
  private String oldAclId;
  private String oldInsertId;
  private String oldInsertDate;
  private String oldDeleteId;
  private String oldDeleteDate;
  private String oldAclModifyId;
  private String oldAclModifyDate;

  private enum LastAction { NONE, ADD, DELETE }
  private LastAction lastAction = LastAction.NONE;

  private static int incrementModulo(int insertIndex, int size) {
    //TODO(Srinivas) check for (size > 0) is needed only for empty list of
    // where clause. check and fix the test cases with empty list and 
    // remove this additional check
    if (insertIndex == -1) {
      return 0;
    } else if (size > 0) {
      if (insertIndex + 1 == size) {
        return -1;
      } else {
        return (insertIndex + 1) % size;
      }
    } else {
      return -1;
    }

  }

  /** Constructs a mutable list containing only the specified object. */
  private static List<String> mutableList(String value) {
    List<String> list = new ArrayList<String>(1);
    list.add(value);
    return list;
  }

  /**
   * Constructor for an empty checkpoint.
   *
   * @param whereClause the list of additional where clauses, used to
   * control the modulus of the insert indexes
   */
  Checkpoint(List<String> whereClause) {
    insertIndex = -1;
    startInsertIndex = -1;
    aclId = null;
    insertId = mutableList(null);
    insertDate = mutableList(null);
    insertIndexModulus = whereClause.size();
    nextInsertIndex = incrementModulo(insertIndex, insertIndexModulus);
  }

  /**
   * Constructor given a checkpoint string.  Checkpoints are passed
   * between the Connector Manager and the Connector in the form of
   * a string.  This parses the checkpoint string.
   *
   * @param whereClause the list of additional where clauses, used to
   * control the modulus of the insert indexes
   * @param checkpoint a checkpoint String.
   * @throws RepositoryException if checkpoint is invalid.
   */
  Checkpoint(List<String> whereClause, String checkpoint)
      throws RepositoryException {
    LOGGER.fine("Parse Checkpoint: " + checkpoint);
    try {
      JSONObject jo = new JSONObject(checkpoint);

      aclId = getJsonString(jo, ACL_ID);
      aclModifyId = getJsonString(jo, ACL_MODIFY_ID);
      aclModifyDate = getJsonString(jo, ACL_MODIFY_DATE);

      if (jo.has(INS_INDEX)) {
        insertIndex = jo.getInt(INS_INDEX);
        JSONArray ids = jo.getJSONArray(INS_ID);
        JSONArray dates = jo.getJSONArray(INS_DATE);
        if (ids.length() != dates.length() || insertIndex > ids.length()
            || (insertIndex > 0 && insertIndex >= whereClause.size())) {
          throw new IllegalArgumentException();
        }
        insertId = new ArrayList<String>(ids.length());
        for (int i = 0; i < ids.length(); i++) {
          insertId.add(getJsonString(ids, i));
        }
        insertDate = new ArrayList<String>(dates.length());
        for (int i = 0; i < dates.length(); i++) {
          insertDate.add(getJsonString(dates, i));
        }
        if (insertIndex == insertId.size()) {
          insertId.add(null);
          insertDate.add(null);
        }
        if (insertIndex > 0 && insertIndex > insertId.size()) {
          throw new IllegalArgumentException();
        }
      } else {
        // process old style id and date which may not be in an array
        insertIndex = -1;
        insertId = mutableList(getJsonString(jo, INS_ID));
        insertDate = mutableList(getJsonString(jo, INS_DATE));
      }
      startInsertIndex = insertIndex;
      insertIndexModulus = whereClause.size();
      nextInsertIndex = incrementModulo(insertIndex, insertIndexModulus);

      deleteId = getJsonString(jo, DEL_ID);
      deleteDate = getJsonString(jo, DEL_DATE);
      if (deleteDate == null) {
        String oldStyle = getJsonString(jo, DEL_DATE_OLD);
        if (oldStyle != null) {
          // TODO: This migration will be repeated until the checkpoint
          // is actually returned to the connector manager and
          // persisted. We could force that to happen on the next call
          // to resumeTraversal by returning an empty DocumentList
          // instead of null.
          Date dateWithOffset = new Date(dateFormat.parse(oldStyle).getTime()
              + TIME_STAMP_OFFSET);
          deleteDate = dateFormat.format(dateWithOffset);
          if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Migrating from time_stamp to time_stamp_utc: "
                + oldStyle + " becomes " + deleteDate);
          }
        }
      }
    } catch (IllegalArgumentException e) {
      LOGGER.severe("Invalid Checkpoint: " + checkpoint);
      throw new RepositoryException("Invalid Checkpoint: " + checkpoint, e);
    } catch (JSONException e) {
      LOGGER.severe("Invalid Checkpoint: " + checkpoint);
      throw new RepositoryException("Invalid Checkpoint: " + checkpoint, e);
    } catch (ParseException e) {
      LOGGER.severe("Invalid Checkpoint: " + checkpoint);
      throw new RepositoryException("Invalid Checkpoint: " + checkpoint, e);
    }

    oldAclId = aclId;
    if (insertIndex != -1) {
      oldInsertId = getInsertId();
      oldInsertDate = getInsertDate();
    }
    oldDeleteId = deleteId;
    oldDeleteDate = deleteDate;
  }

  /**
   * Gets a trimmed string value from a JSON object. If the value is
   * missing, null, or empty, return null.
   */
  private static String getJsonString(JSONObject jo, String key)
      throws JSONException {
    if (!jo.isNull(key)) {
      String value = jo.getString(key).trim();
      if (value.length() > 0) {
        return value;
      }
    }
    return null;
  }

  /**
   * Gets a trimmed string value from a JSON array. If the value is
   * missing, null, or empty, return null.
   */
  private static String getJsonString(JSONArray ja, int index)
      throws JSONException {
    if (!ja.isNull(index)) {
      String value = ja.getString(index).trim();
      if (value.length() > 0) {
        return value;
      }
    }
    return null;
  }

  /** r_object_id of the last ACL inserted. */
  public String getAclId() {
    return aclId;
  }

  /** r_object_id of the last item inserted. */
  public String getInsertId() {
    if ( insertIndex == -1) { 
      return null;
    } else {
      return insertId.get(insertIndex);
    }
  }

  /** r_modify_date of the last item inserted. */
  public String getInsertDate() {
    if ( insertIndex == -1) { 
      return null;
    } else {
      return insertDate.get(insertIndex);
    }
  }

  public String getAclModifiedDate() {
    return aclModifyDate;
  }

  /** Gets the index into the insert arrays. */
  public int getInsertIndex() {
    return insertIndex;
  }

  /** r_object_id of the last item deleted. */
  public String getDeleteId() {
    return deleteId;
  }

  /**
   * dm_audittrail time_stamp_utc of the last item deleted. Note that
   * the stored date is UTC, but the value of this field may not be,
   * due to the way DQL handles time zones.
   */
  public String getDeleteDate() {
    return deleteDate;
  }

  public String getAclModifyId() {
    return aclModifyId;
  }

  /**
   * Set the Inserted Item's portion of the checkpoint.
   *
   * @param date the r_modify_date of the last item inserted.
   * @param objectId the r_object_id of the last item inserted.
   */
  public void setInsertCheckpoint(String date, String objectId) {
    // Remember previous insert checkpoint as a restore point.
    oldInsertDate = getInsertDate();
    oldInsertId = getInsertId();

    // Set the new insert checkpoint.
    if (insertIndex != -1) {
      insertDate.set(insertIndex, date);
      insertId.set(insertIndex, objectId);
    }
    lastAction = LastAction.ADD;
  }

  /**
   * Sets the Deleted Item's portion of the checkpoint.
   *
   * @param date the dm_audittrail time_stamp_utc of the last item deleted.
   * @param objectId the r_object_id of the last item deleted.
   */
  public void setDeleteCheckpoint(String date, String objectId) {
    // Remember the current delete checkpoint as a restore point.
    oldDeleteDate = deleteDate;
    oldDeleteId = deleteId;

    // Set the new insert checkpoint.
    deleteDate = date;
    deleteId = objectId;
    lastAction = LastAction.DELETE;
  }

  /**
   * Sets the ACL Item's portion of the checkpoint
   * 
   * @param objectId the r_object_id of the last ACL item deleted
   */
  public void setAclCheckpoint(String objectId) {
    // Remember the current ACL checkpoint as a restore point.
    oldAclId = aclId;

    // Set the new ACL checkpoint.
    aclId = objectId;
  }

  public void setAclModifyCheckpoint(String date, String objectId) {
    // Remember the current ACL checkpoint as a restore point.
    oldAclModifyId = aclModifyId;
    oldAclModifyDate = aclModifyDate;

    // Set the new ACL checkpoint.
    aclModifyId = objectId;
    aclModifyDate = date;
  }

  /**
   * Returns true if some component of the checkpoint has changed.
   * (In other words, restore() would result in a different checkpoint.)
   */
  public boolean hasChanged() {
    return nextInsertIndex != insertIndex
        || (insertIndex != -1 && ((getInsertDate() != oldInsertDate)
            || (getInsertId() != oldInsertId)
            || (deleteDate != oldDeleteDate)
            || (deleteId != oldDeleteId)))
        || (insertIndex == -1 && (aclId != oldAclId));
  }

  /**
   * Restores the checkpoint to its previous state.  This is used when
   * we wish to retry an item that seems to have failed.
   */
  public void restore() {
    if (lastAction == LastAction.ADD) {
      if (insertIndex == -1) {
        aclId = oldAclId;
      } else {
        insertDate.set(insertIndex, oldInsertDate);
        insertId.set(insertIndex, oldInsertId);
      }
    } else if (lastAction == LastAction.DELETE) {
      if (insertIndex == -1) {
        aclModifyId = oldAclModifyId;
        aclModifyDate = oldAclModifyDate;
      } else {
        deleteDate = oldDeleteDate;
        deleteId = oldDeleteId;
      }
    }
    lastAction = LastAction.NONE;
  }

  /**
   * Advance to the next insert index, if there is one. This should
   * have much the same effect as the following (if it were legal),
   * but keeping startInsertIndex fixed:
   * <pre>
   * this = new Checkpoint(whereClause, asString())
   * </pre>
   *
   * @return true if the checkpoint has advanced back to the starting
   * insert index, and false if it has not
   */
  public boolean advance() {
    insertIndex = nextInsertIndex;
    if (insertIndex == insertId.size()) {
      insertId.add(null);
      insertDate.add(null);
    }
    nextInsertIndex = incrementModulo(insertIndex, insertIndexModulus);

    lastAction = LastAction.NONE;
    if (insertIndex != -1) {
      oldInsertId = getInsertId();
      oldInsertDate = getInsertDate();
    }
    oldDeleteId = deleteId;
    oldDeleteDate = deleteDate;
    oldAclModifyId = aclModifyId;
    oldAclModifyDate = aclModifyDate;

    return insertIndex != startInsertIndex;
  }

  /**
   * @returns the Checkpoint as a String.
   * @throws RepositoryException if error.
   */
  public String asString() throws RepositoryException {
    try {
      JSONObject jo = new JSONObject();

      jo.put(INS_INDEX, nextInsertIndex);
      if (aclId != null) {
        jo.put(ACL_ID, aclId);
      }
      if (aclModifyId != null) {
        jo.put(ACL_MODIFY_ID, aclModifyId);
      }
      if (aclModifyDate != null) {
        jo.put(ACL_MODIFY_DATE, aclModifyDate);
      }
      jo.put(INS_ID, new JSONArray(insertId));
      jo.put(INS_DATE, new JSONArray(insertDate));

      if (deleteId != null) {
        jo.put(DEL_ID, deleteId);
      }
      if (deleteDate != null) {
        jo.put(DEL_DATE, deleteDate);
      }
      String result = jo.toString();
      LOGGER.fine("Created Checkpoint: " + result);
      return result;
    } catch (JSONException e) {
      LOGGER.severe("JSON problem creating Checkpoint: " + e.toString());
      throw new RepositoryException("JSON problem creating Checkpoint", e);
    }
  }

  /**
   * @returns the Checkpoint as a String.
   */
  public String toString() {
    try {
      return asString();
    } catch (RepositoryException re) {
      // Already logged in asString.
      return null;
    }
  }
}
