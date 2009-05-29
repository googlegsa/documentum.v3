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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spiimpl.StringValue;

public class DctmDocumentList implements DocumentList {
  private static final Logger logger =
      Logger.getLogger(DctmDocumentList.class.getName());

  ICollection collectionToAdd;
  ICollection collectionToDel;

  IClientX clientX;
  ISessionManager sessMag;
  private boolean isPublic;
  private final Set<String> included_meta;
  private Checkpoint checkpoint;

  public DctmDocumentList() {
    this.included_meta = null;
    this.checkpoint = new Checkpoint();
  }

  public DctmDocumentList(ICollection collToAdd, ICollection collToDel,
      ISessionManager sessMag, IClientX clientX, boolean isPublic,
      Set<String> included_meta, Checkpoint checkpoint) {
    this.collectionToAdd = collToAdd;
    this.collectionToDel = collToDel;
    this.clientX = clientX;
    this.sessMag = sessMag;
    this.isPublic = isPublic;
    this.included_meta = included_meta;
    this.checkpoint = checkpoint;
  }

  public Document nextDocument() throws RepositoryException {
    DctmSysobjectDocument dctmSysobjectDocument;
    Document retDoc = null;
    boolean skippingDoc = false;
    try {
      if (isOpen(collectionToAdd) && collectionToAdd.next()) {
        logger.fine("Looking through the collection of documents to add");

        String crID = "";
        ITime modifyDate = null;
        try {
          crID = collectionToAdd.getString("r_object_id");
          modifyDate = collectionToAdd.getTime("r_modify_date");
          logger.fine("modifyDate is " + modifyDate);
          logger.fine("r_object_id is " + crID);
        } catch (RepositoryException e) {
          logger.severe("impossible to get the r_object_id of the document");
          return null;
        }
        checkpoint.setInsertCheckpoint(modifyDate.getDate(), crID);

        dctmSysobjectDocument = new DctmSysobjectDocument(crID, modifyDate,
            sessMag, clientX, isPublic, included_meta,
            SpiConstants.ActionType.ADD, checkpoint);

        logger.fine("Creation of a new dctmSysobjectDocument to add");
        retDoc = dctmSysobjectDocument;

      } else if (isOpen(collectionToDel) && collectionToDel.next()) {
        logger.fine("Looking through the collection of documents to remove");

        String crID = "";
        String commonVersionID = "";
        ITime deleteDate = null;
        try {
          crID = collectionToDel.getString("r_object_id");

          commonVersionID = collectionToDel.getString("chronicle_id");
          deleteDate = collectionToDel.getTime("time_stamp");

          logger.fine("r_object_id is " + crID);
        } catch (RepositoryException e) {
          logger.warning("impossible to get the r_object_id of the document");
          return null;
        }
        checkpoint.setDeleteCheckpoint(deleteDate.getDate(), crID);

        dctmSysobjectDocument = new DctmSysobjectDocument(crID,
            commonVersionID, deleteDate, sessMag, clientX, isPublic,
            included_meta, SpiConstants.ActionType.DELETE, checkpoint);

        logger.fine("Creation of a new dctmSysobjectDocument to delete");
        retDoc = dctmSysobjectDocument;
      } else {
        logger.fine("End of document list");
      }
    } catch (RepositoryDocumentException rde) {
      logger.warning("Error while trying to get next document : " + rde);
      skippingDoc = true;
      throw rde;
    } catch (RepositoryException re) {
      logger.warning("Error while trying to get next document : " + re);
      if (lostConnection()) {
        // If we have lost connectivity to the server, rollback the
        // checkpoint to the previous state, retrying this document later.
        checkpoint.restore();
      }
      throw re;
    } finally {
      if (retDoc == null && !skippingDoc) {
        logger.fine("retDoc is null before finalize");
        finalize();
      }
    }
    return retDoc;
  }

  public String checkpoint() throws RepositoryException {
    try {
      return checkpoint.asString();
    } finally {
      finalize();
    }
  }

  /**
   * Test connectivity to server.  If we have a session and
   * can verify that session isConnected(), return false.
   * If we cannot verfy the session is connected return true.
   * If we don't have a session to test, return false.
   */
  private boolean lostConnection() {
    try {
      if (isOpen(collectionToAdd) &&
          !collectionToAdd.getSession().isConnected()) {
        return true;
      }
      if (isOpen(collectionToDel) &&
          !collectionToDel.getSession().isConnected()) {
        return true;
      }
    } catch (Exception e) {
      logger.warning("Lost connectivity to server : " + e);
      return true;
    }
    return false;
  }

  /**
   * Return true if the collection is open (non-null and not in CLOSED_STATE).
   */
  private boolean isOpen(ICollection collection) {
    return ((collectionToAdd != null) &&
            (collectionToAdd.getState() != ICollection.DF_CLOSED_STATE));
  }

  // Last chance to make sure the collections are closed and their sessions
  // are released.
  public void finalize() {
    if (isOpen(collectionToAdd)) {
      try {
        collectionToAdd.close();
        logger.fine("collection of documents to add closed");
        sessMag.releaseSessionAdd();
        logger.fine("collection session released");
      } catch (RepositoryException e) {
        logger.severe(
            "Error while closing the collection of documents to add: " + e);
      }
    }

    if (isOpen(collectionToDel)) {
      try {
        collectionToDel.close();
        logger.fine("collection of documents to delete closed");
        sessMag.releaseSessionDel();
        logger.fine("collection session released");
      } catch (RepositoryException e) {
        logger.severe(
            "Error while closing the collection of documents to delete: " + e);
      }
    }
  }
}
