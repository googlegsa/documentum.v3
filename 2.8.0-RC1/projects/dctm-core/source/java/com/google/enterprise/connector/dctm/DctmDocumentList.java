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

import java.util.logging.Logger;
import java.util.HashSet;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;

public class DctmDocumentList implements DocumentList {
  private static final Logger logger =
      Logger.getLogger(DctmDocumentList.class.getName());

  private ISession session;

  private final ICollection collectionToAdd;

  private final ICollection collectionToDel;

  /**
   * Used for merging multiple chronicleIds in the same batch of deletes,
   * so we only send a single add or delete request.
   */
  private final HashSet<String> deletedIds = new HashSet<String>();

  private final Checkpoint checkpoint;

  private final DctmTraversalManager traversalManager;

  public DctmDocumentList(DctmTraversalManager traversalManager,
      ISession session, ICollection collToAdd, ICollection collToDel,
      Checkpoint checkpoint) {
    this.traversalManager = traversalManager;
    this.session = session;
    this.collectionToAdd = collToAdd;
    this.collectionToDel = collToDel;
    this.checkpoint = checkpoint;
  }

  public Document nextDocument() throws RepositoryException {
    DctmSysobjectDocument dctmSysobjectDocument;
    Document retDoc = null;
    boolean skippingDoc = false;
    try {
      while (retDoc == null) {
        if (isOpen(collectionToAdd) && collectionToAdd.next()) {
          logger.fine("Looking through the collection of documents to add");

          String objId = "";
          ITime modifyDate = null;
          try {
            objId = collectionToAdd.getString("r_object_id");
            modifyDate = collectionToAdd.getTime("r_modify_date");
            logger.fine("r_object_id is " + objId + "  modifyDate is "
                        + modifyDate.getDate());
          } catch (RepositoryException e) {
            logger.severe("impossible to get the r_object_id of the document");
            return null;
          }
          checkpoint.setInsertCheckpoint(modifyDate.getDate(), objId);

          dctmSysobjectDocument = new DctmSysobjectDocument(traversalManager,
              session, objId, collectionToAdd.getString("i_chronicle_id"),
              modifyDate, SpiConstants.ActionType.ADD, checkpoint);

          logger.fine("Creation of a new dctmSysobjectDocument to add");
          retDoc = dctmSysobjectDocument;
        } else if (isOpen(collectionToDel) && collectionToDel.next()) {
          logger.fine("Looking through the collection of documents to remove");

          String eventId = "";
          ITime deleteDate = null;
          try {
            eventId = collectionToDel.getString("r_object_id");
            deleteDate = collectionToDel.getTime("time_stamp_utc");
            logger.fine("delete event r_object_id is " + eventId
                        + "  deleteDate is " + deleteDate.getDate());
          } catch (RepositoryException e) {
            logger.warning("impossible to get the r_object_id of the delete event");
            return null;
          }
          checkpoint.setDeleteCheckpoint(deleteDate.getDate(), eventId);

          String chronicleId = collectionToDel.getString("chronicle_id");

          // Deleting multiple versions can post multiple dm_destroy and
          // dm_prune events with the same chronicle_id.  We only want to
          // send a single delete request to the GSA.
          if (deletedIds.contains(chronicleId)) {
            logger.fine("Skipping redundant delete of version: " + chronicleId);
            continue; // Already deleted this chronicle_id in this batch.
          }

          // If we are deleting the last version of a document, remove it
          // from the index.  If we may be deleting the latest version of
          // the document, force the new latest version to be re-indexed.
          ICollection versions = null;
          try {
            versions = getCurrentVersion(chronicleId);
            if (versions != null && versions.next()) {
              ITime lastModify = versions.getTime("r_modify_date");
              if (lastModify.getDate().before(deleteDate.getDate())) {
                // We may have deleted the latest version, so refeed the
                // current latest version.
                dctmSysobjectDocument = new DctmSysobjectDocument(
                    traversalManager, session,
                    versions.getString("r_object_id"), chronicleId,
                    lastModify, SpiConstants.ActionType.ADD, checkpoint);
                logger.fine("Creation of a new dctmSysobjectDocument to "
                            + "resubmit newest version of deleted item: "
                            + chronicleId);
              } else {
                // Skip this doc.
                logger.fine("Skipping delete of old version: " + chronicleId);
                continue;
              }
            } else {
              // No more versions of the document remain.
              // Delete the document from the index.
              dctmSysobjectDocument = new DctmSysobjectDocument(
                  traversalManager, session,
                  collectionToDel.getString("audited_obj_id"), chronicleId,
                  deleteDate, SpiConstants.ActionType.DELETE, checkpoint);
              logger.fine("Creation of a new dctmSysobjectDocument to delete: "
                          + chronicleId);
            }
          } finally {
            if (versions != null) {
              versions.close();
            }
          }
          // Handled this version in this batch.
          deletedIds.add(chronicleId);
          retDoc = dctmSysobjectDocument;
        } else {
          logger.fine("End of document list");
          break;
        }
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
   * Return a ICollection containing the latest version of document based
   * upon the supplied chronicleId.
   *
   * @param chronicleId root document version
   * @return ICollection of versions.
   */
  public ICollection getCurrentVersion(String chronicleId)
      throws RepositoryException {
    IQuery query = traversalManager.getClientX().getQuery();
    query.setDQL(traversalManager.buildVersionsQueryString(checkpoint,
        chronicleId));
    return query.execute(session, IQuery.EXECUTE_READ_QUERY);
  }

  /**
   * Test connectivity to server.  If we have a session and
   * can verify that session isConnected(), return false.
   * If we cannot verify the session is connected return true.
   * If we don't have a session to test, return false.
   */
  private boolean lostConnection() {
    try {
      return (session != null && !session.isConnected());
    } catch (Exception e) {
      logger.warning("Lost connectivity to server: " + e);
      return true;
    }
  }

  /**
   * Return true if the collection is open (non-null and not in CLOSED_STATE).
   */
  private static boolean isOpen(ICollection collection) {
    return ((collection != null) &&
            (collection.getState() != ICollection.DF_CLOSED_STATE));
  }

  // Last chance to make sure the collections are closed and their sessions
  // are released.
  public void finalize() {
    try {
      if (isOpen(collectionToAdd)) {
        try {
          collectionToAdd.close();
          logger.fine("collection of documents to add closed");
        } catch (RepositoryException e) {
          logger.severe(
              "Error while closing the collection of documents to add: " + e);
        }
      }

      if (isOpen(collectionToDel)) {
        try {
          collectionToDel.close();
          logger.fine("collection of documents to delete closed");
        } catch (RepositoryException e) {
          logger.severe(
              "Error while closing the collection of documents to delete: "
              + e);
        }
      }
    } finally {
      if (session != null) {
        traversalManager.getSessionManager().release(session);
        session = null;
        logger.fine("collection session released");
      }
    }
  }
}
