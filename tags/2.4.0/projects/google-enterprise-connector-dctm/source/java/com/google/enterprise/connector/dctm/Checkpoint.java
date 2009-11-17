// Copyright (C) 2007-2009 Google Inc.
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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.enterprise.connector.spi.RepositoryException;

/**
 * Create and parse the checkpoint strings passed back and forth between
 * the connector and the Google appliance traverser.
 */
class Checkpoint {
  /** The logger for this class. */
  private static final Logger LOGGER =
      Logger.getLogger(Checkpoint.class.getName());

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

  /** r_object_id of the last item inserted. */
  public String insertId;

  /** r_modify_date of the last item inserted. */
  public Date insertDate;

  /** r_object_id of the last item deleted. */
  public String deleteId;

  /**
   * dm_audittrail time_stamp_utc of the last item deleted. Note that
   * the stored date is UTC, but the value of this field may not be,
   * due to the way DQL handles time zones.
   */
  public Date deleteDate;

  /**
   * Backup versions of the insert and delete checkpoints,
   * so that we may restore a checkpoint when attempting to
   * retry failed items.
   */
  private String oldInsertId;
  private Date oldInsertDate;
  private String oldDeleteId;
  private Date oldDeleteDate;

  private enum LastAction { NONE, ADD, DELETE }
  private LastAction lastAction = LastAction.NONE;

  /** Generic Constructor */
  Checkpoint() {
  }

  /**
   * Constructor given a checkpoint string.  Checkpoints are passed
   * between the Connector Manager and the Connector in the form of
   * a string.  This parses the checkpoint string.
   *
   * @param checkpoint a checkpoint String.
   * @throws RepositoryException if checkpoint is invalid.
   */
  Checkpoint(String checkpoint) throws RepositoryException {
    LOGGER.fine("Parse Checkpoint: " + checkpoint);
    try {
      JSONObject jo = new JSONObject(checkpoint);
      insertId = getJsonValue(jo, INS_ID);
      deleteId = getJsonValue(jo, DEL_ID);
      String value;
      if ((value = getJsonValue(jo, INS_DATE)) != null) {
        insertDate = dateFormat.parse(value);
      }
      if ((value = getJsonValue(jo, DEL_DATE)) != null) {
        deleteDate = dateFormat.parse(value);
      } else if ((value = getJsonValue(jo, DEL_DATE_OLD)) != null) {
        // TODO: This migration will be repeated until the checkpoint
        // is actually returned to the connector manager and
        // persisted. We could force that to happen on the next call
        // to resumeTraversal by returning an empty DocumentList
        // instead of null.
        Date oldStyle = dateFormat.parse(value);
        deleteDate = new Date(oldStyle.getTime() + TIME_STAMP_OFFSET);
        if (LOGGER.isLoggable(Level.INFO)) {
          LOGGER.info("Migrating from time_stamp to time_stamp_utc: "
              + oldStyle + " becomes " + deleteDate);
        }
      }
      oldInsertId = insertId;
      oldInsertDate = insertDate;
      oldDeleteId = deleteId;
      oldDeleteDate = deleteDate;
    } catch (JSONException e) {
      LOGGER.severe("Invalid Checkpoint: " + checkpoint);
      throw new RepositoryException("Invalid Checkpoint: " + checkpoint);
    } catch (ParseException e) {
      LOGGER.severe("Invalid Checkpoint: " + checkpoint);
      throw new RepositoryException("Invalid Checkpoint: " + checkpoint);
    }
  }

  /**
   * Fetch the value from a JSON object.  If the value is missing,
   * null, or empty, return null.
   */
  private static String getJsonValue(JSONObject jo, String key)
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
   * Set the Inserted Item's portion of the checkpoint.
   *
   * @param date the r_modify_date of the last item inserted.
   * @param objectId the r_object_id of the last item inserted.
   */
  public void setInsertCheckpoint(Date date, String objectId) {
    // Remember previous insert checkpoint as a restore point.
    oldInsertDate = insertDate;
    oldInsertId = insertId;

    // Set the new insert checkpoint.
    insertDate = date;
    insertId = objectId;
    lastAction = LastAction.ADD;
  }

  /**
   * Sets the Deleted Item's portion of the checkpoint.
   *
   * @param date the dm_audittrail time_stamp_utc of the last item deleted.
   * @param objectId the r_object_id of the last item deleted.
   */
  public void setDeleteCheckpoint(Date date, String objectId) {
    // Remember the current delete checkpoint as a restore point.
    oldDeleteDate = deleteDate;
    oldDeleteId = deleteId;

    // Set the new insert checkpoint.
    deleteDate = date;
    deleteId = objectId;
    lastAction = LastAction.DELETE;
  }

  /**
   * Returns true if some component of the checkpoint has changed.
   * (In other words, restore() would result in a different checkpoint.)
   */
  public boolean hasChanged() {
    return (insertDate != oldInsertDate || insertId != oldInsertId ||
            deleteDate != oldDeleteDate || deleteId != oldDeleteId);
  }

  /**
   * Restores the checkpoint to its previous state.  This is used when
   * we wish to retry an item that seems to have failed.
   */
  public void restore() {
    if (lastAction == LastAction.ADD) {
      insertDate = oldInsertDate;
      insertId = oldInsertId;
    }
    if (lastAction == LastAction.DELETE) {
      deleteDate = oldDeleteDate;
      deleteId = oldDeleteId;
    }
    lastAction = LastAction.NONE;
  }

  /**
   * @returns the Checkpoint as a String.
   * @throws RepositoryException if error.
   */
  public String asString() throws RepositoryException {
    // A null checkpoint is OK.
    if ((insertDate == null) && (deleteDate == null))
      return null;

    try {
      JSONObject jo = new JSONObject();
      if (insertId != null) {
        jo.put(INS_ID, insertId);
      }
      if (insertDate != null) {
        jo.put(INS_DATE, dateFormat.format(insertDate));
      }
      if (deleteId != null) {
        jo.put(DEL_ID, deleteId);
      }
      if (deleteDate != null) {
        jo.put(DEL_DATE, dateFormat.format(deleteDate));
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
