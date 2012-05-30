package com.google.enterprise.connector.dctm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.enterprise.connector.dctm.dfcwrap.IACL;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.IPersistentObject;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.Principal;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SecureDocument;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.Value;
import com.google.enterprise.connector.spiimpl.PrincipalValue;

public class DctmAclList implements DocumentList {
  private static final Logger logger =
      Logger.getLogger(DctmDocumentList.class.getName());

  private ISession session;

  private final ICollection collectionAcl;

  private final Checkpoint checkpoint;

  private final DctmTraversalManager traversalManager;

  private List<String> users;
  private List<String> groups;
  private List<String> denyUsers;
  private List<String> denyGroups;

  public DctmAclList(DctmTraversalManager traversalManager,
      ISession session, ICollection collAcl, Checkpoint checkpoint) {
    this.traversalManager = traversalManager;
    this.session = session;
    this.collectionAcl = collAcl;
    this.checkpoint = checkpoint;
  }

  @Override
  public Document nextDocument() throws RepositoryException {
    Document retAclDocument;
    Document retDoc = null;
    boolean skippingDoc = false;
    try {
      while (retDoc == null) {
        if (isOpen(collectionAcl) && collectionAcl.next()) {
          logger.fine("Looking through the collection of Acls to add");

          String objId = "";
          ITime modifyDate = null;
          try {
            objId = collectionAcl.getString("r_object_id");
            // modifyDate = collectionToAdd.getTime("r_modify_date");
            modifyDate = null; // Srinivas:TBD
            logger.fine("r_object_id is " + objId + "  modifyDate is null ");
          } catch (RepositoryException e) {
            logger.severe("impossible to get the r_object_id of the document");
            return null;
          }
          checkpoint.setAclCheckpoint(null, objId);

          processAcl(objId);
          retAclDocument = getSecureAclDocument(objId);
          logger.fine("Creation of a new Acl to add");
          retDoc = retAclDocument;
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

  @Override
  public String checkpoint() throws RepositoryException {
    try {
      return checkpoint.asString();
    } finally {
      finalize();
    }
  }

  /*
   * Return list of users
   */
  private List<String> getUsers() {
    return users;
  }

  /*
   * Return list deny users
   */
  private List<String> getDenyUsers() {
    return denyUsers;
  }

  /*
   * Returns list of groups
   */
  private List<String> getGroups() {
    return groups;
  }

  /*
   * Returns list of deny groups
   */
  private List<String> getDenyGroups() {
    return denyGroups;
  }

  /*
   * Fetches the ACL object
   */
  private IACL fetchAcl(String docId) throws RepositoryDocumentException {
    try {
      IId id = traversalManager.getClientX().getId(docId);
      return (IACL) session.getObject(id);
    } catch (RepositoryDocumentException rde) {
      logger.fine("Error fetching Acl object");
      throw rde;
    }
  }

  /*
   * Processes users and groups from the ACL object for the given object id.
   * Gets the users and groups with READ permission and populates the user 
   * and group list. Populates denyusers and denygroup list users and groups
   * with no READ permission
   */
  private void processAcl(String docId) throws RepositoryDocumentException {
    users = new ArrayList<String>();
    groups = new ArrayList<String>();
    denyUsers = new ArrayList<String>();
    denyGroups = new ArrayList<String>();

    try {
      // fetch the Acl object
      IACL iAcl = fetchAcl(docId);

      for (int i = 0; i < iAcl.getAccessorCount(); i++) {
        String name = iAcl.getAccessorName(i);
        if (iAcl.getAccessorPermit(i) >= IACL.DF_PERMIT_READ) {
          if (iAcl.isGroup(i))
            groups.add(name);
          else
            users.add(name);
        } else if (iAcl.getAccessorPermit(i) == IACL.DF_PERMIT_NONE
            || iAcl.getAccessorPermit(i) == IACL.DF_PERMIT_BROWSE) {
          if (iAcl.isGroup(i))
            denyGroups.add(name);
          else
            denyUsers.add(name);
        }
      }
    } catch (RepositoryDocumentException e) {
      logger.log(Level.WARNING, "Error fetching Acl user and group names");
      throw new RepositoryDocumentException(e);
    }
  }

  /*
   * Creates a secure document using Acl users and groups§
   */
  private Document getSecureAclDocument(String objId) {

    Map<String, List<Value>> aclValues = new HashMap<String, List<Value>>();
    aclValues.put(SpiConstants.PROPNAME_ACLUSERS, 
        asPrincipalValues(getUsers()));
    aclValues.put(SpiConstants.PROPNAME_ACLGROUPS,
        asPrincipalValues(getGroups()));
    aclValues.put(SpiConstants.PROPNAME_ACLDENYUSERS,
        asPrincipalValues(getDenyUsers()));
    aclValues.put(SpiConstants.PROPNAME_ACLDENYGROUPS,
        asPrincipalValues(getDenyGroups()));
    aclValues.put(SpiConstants.PROPNAME_DOCID,
        Collections.singletonList(Value.getStringValue(objId)));

    aclValues.put(SpiConstants.PROPNAME_ACLINHERITANCETYPE,
        Collections.singletonList(Value.getStringValue(
            SpiConstants.AclInheritanceType.CHILD_OVERRIDES.toString())));
    
    return SecureDocument.createAcl(aclValues);
  }

  /*
   * Converts list of Strings into list of principalValues
   */
  private List<Value> asPrincipalValues(List<String> list) {
    if (list == null) {
      return null;
    }
    List<Value> principalvalues = new ArrayList<Value>(list.size());
    for (String item : list) {
      principalvalues.add((PrincipalValue) Value
          .getPrincipalValue(new Principal(item)));
    }
    return principalvalues;
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
      if (isOpen(collectionAcl)) {
        try {
          collectionAcl.close();
          logger.fine("collection of documents to add closed");
        } catch (RepositoryException e) {
          logger.severe(
              "Error while closing the collection of documents to add: " + e);
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
