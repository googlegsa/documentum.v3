// Copyright 2013 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.dctm;

import com.google.common.base.Strings;
import com.google.enterprise.connector.dctm.dfcwrap.IAcl;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IGroup;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IUser;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.Principal;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SecureDocument;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.Value;
import com.google.enterprise.connector.spi.SpiConstants.CaseSensitivityType;
import com.google.enterprise.connector.spi.SpiConstants.PrincipalType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * @since TODO(Srinivas)
 */
public class DctmAclList implements DocumentList {
  //TODO(Srinivas): Refactor similarities between DocumentList 
  private static final Logger logger =
      Logger.getLogger(DctmAclList.class.getName());

  private ISession session;

  private final ICollection collectionAcl;

  private final Checkpoint checkpoint;

  private final DctmTraversalManager traversalManager;

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
          logger.fine("Looking through the collection of ACLs to add");

          String objId = "";
          ITime modifyDate = null;
          try {
            objId = collectionAcl.getString("r_object_id");
            logger.fine("r_object_id is " + objId + ", modifyDate is null");
            checkpoint.setAclCheckpoint(objId);
            retAclDocument = getSecureAclDocument(objId);
            logger.fine("Creation of a new Acl to add");
            retDoc = retAclDocument;
          } catch (RepositoryException e) {
            logger.severe("impossible to get the r_object_id of the document");
            return null;
          }
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
   * Fetches the ACL object
   */
  private IAcl fetchAcl(String docId) throws RepositoryDocumentException {
    IId id = traversalManager.getClientX().getId(docId);
    return (IAcl) session.getObject(id);
  }

  /*
   * Processes users and groups from the ACL object for the given object id.
   * Gets the users and groups with READ permission and populates the user 
   * and group list. Populates denyusers and denygroup list users and groups
   * with no READ permission
   */
  private void processAcl(String docId, Map<String, List<Value>> aclValues)
      throws RepositoryDocumentException {
    List<Value> userPrincipals = new ArrayList<Value>();
    List<Value> groupPrincipals = new ArrayList<Value>();

    try {
      // fetch the Acl object
      IAcl iAcl = fetchAcl(docId);

      for (int i = 0; i < iAcl.getAccessorCount(); i++) {
        String name = iAcl.getAccessorName(i);
        if (iAcl.getAccessorPermit(i) >= IAcl.DF_PERMIT_READ) {
          if (iAcl.isGroup(i)) {
            groupPrincipals.add(asPrincipalValue(name, getGroupNamespace(name)));
          } else {
            userPrincipals.add(asPrincipalValue(name, getUserNamespace(name)));
          }
        }
      }
      // add users and groups principals to the map
      aclValues.put(SpiConstants.PROPNAME_ACLUSERS, userPrincipals);
      aclValues.put(SpiConstants.PROPNAME_ACLGROUPS, groupPrincipals);
    } catch (RepositoryDocumentException e) {
      logger.log(Level.WARNING, "Error fetching Acl user and group names");
      throw new RepositoryDocumentException(e);
    }
  }

  /*
   * Creates a secure document using Acl users and groups§
   */
  private Document getSecureAclDocument(String objId)
      throws RepositoryDocumentException {
    Map<String, List<Value>> aclValues = new HashMap<String, List<Value>>();
    processAcl(objId, aclValues);

    aclValues.put(SpiConstants.PROPNAME_DOCID,
        Collections.singletonList(Value.getStringValue(objId)));
    aclValues.put(SpiConstants.PROPNAME_ACLINHERITANCETYPE,
        Collections.singletonList(Value.getStringValue(
            SpiConstants.AclInheritanceType.PARENT_OVERRIDES.toString())));

    return SecureDocument.createAcl(aclValues);
  }

  private Value asPrincipalValue(String item, String namespace)
      throws RepositoryDocumentException {
    return Value.getPrincipalValue(new Principal(PrincipalType.UNKNOWN,
        namespace, item, CaseSensitivityType.EVERYTHING_CASE_SENSITIVE));
  }

  private String getUserNamespace(String usergroup)
      throws RepositoryDocumentException {
    String localNamespace = traversalManager.getLocalNamespace();
    String globalNamespace = traversalManager.getGlobalNamespace();

    // special users local to repository
    if (usergroup.toLowerCase().equalsIgnoreCase("dm_world")
        || usergroup.toLowerCase().equalsIgnoreCase("dm_owner")
        || usergroup.toLowerCase().equalsIgnoreCase("dm_group")) {
      // TODO(srinivas) to check with Meghna about name space for users
      // if it is always global namespace. setting to global for now.
      return globalNamespace;
      // return localNamespace;
    }

    try {
      IUser userObj = (IUser) session.getObjectByQualification(
          "dm_user where user_name = '" + usergroup + "'");
      if (userObj != null) {
        // TODO(srinivas) to check with Meghna about name space for users
        // if it is always global namespace. setting to global for now.
        return globalNamespace;
        // following code commented until verification from Meghna
        // if (Strings.isNullOrEmpty(userObj.getUserSourceAsString())) {
        // logger.fine("local namespace for user " + usergroup);
        // return localNamespace;
        // } else {
        // logger.fine("global namespace for user " + usergroup);
        // return globalNamespace;
        // }
      } else {
        return null;
      }
    } catch (RepositoryDocumentException e) {
      logger.fine("Exception in getNamespace " + e.getMessage());
      throw e;
    }
  }

  private String getGroupNamespace(String usergroup)
      throws RepositoryDocumentException {
    String localNamespace = traversalManager.getLocalNamespace();
    String globalNamespace = traversalManager.getGlobalNamespace();

    // return localNamespace;
    try {
      IGroup groupObj = (IGroup) session.getObjectByQualification(
          "dm_group where group_name = '" + usergroup + "'");
      if (groupObj != null) {
        if (Strings.isNullOrEmpty(groupObj.getUserSource())) {
          logger.finer("local namespace for group " + usergroup);
          return localNamespace;
        } else {
          logger.finer("global namespace for group " + usergroup);
          return globalNamespace;
        }
      } else {
        return null;
      }
    } catch (RepositoryDocumentException e) {
      logger.fine("Exception in getNamespace " + e.getMessage());
      throw e;
    }
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
    return ((collection != null) && (collection.getState() 
        != ICollection.DF_CLOSED_STATE));
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