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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.enterprise.connector.dctm.dfcwrap.IAcl;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IGroup;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.IUser;
import com.google.enterprise.connector.spi.Document;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.Principal;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SecureDocument;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.SpiConstants.ActionType;
import com.google.enterprise.connector.spi.SpiConstants.CaseSensitivityType;
import com.google.enterprise.connector.spi.SpiConstants.PrincipalType;
import com.google.enterprise.connector.spi.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

/**
 * @since 3.2.0
 */
public class DctmAclList implements DocumentList {
  // TODO(Srinivas): Refactor similarities between DocumentList.
  private static final Logger logger =
      Logger.getLogger(DctmAclList.class.getName());

  private ISession session;

  private final ICollection collectionAcl;
  private final ICollection collectionAclToModify;

  private final HashSet<String> aclModifiedIds = new HashSet<String>();

  private final Checkpoint checkpoint;

  private final DctmTraversalManager traversalManager;
  private final String localNamespace;
  private final String globalNamespace;

  private final Stack<Document> aclStack = new Stack<Document>();

  public DctmAclList(DctmTraversalManager traversalManager, ISession session,
      ICollection collAcl, ICollection collAclToModify,
      Checkpoint checkpoint) {
    this.traversalManager = traversalManager;
    localNamespace = traversalManager.getLocalNamespace();
    globalNamespace = traversalManager.getGlobalNamespace();

    this.session = session;
    this.collectionAcl = collAcl;
    this.collectionAclToModify = collAclToModify;
    this.checkpoint = checkpoint;
  }

  @Override
  public Document nextDocument() throws RepositoryException {
    Document retDoc = null;
    boolean skippingDoc = false;
    try {
      while (retDoc == null) {
        if (!aclStack.isEmpty()) {
          retDoc = aclStack.pop();
        } else if (isOpen(collectionAcl) && collectionAcl.next()) {
          logger.fine("Looking through the collection of ACLs to add");

          try {
            String objectId = collectionAcl.getString("r_object_id");
            logger.fine("ACL r_object_id is " + objectId);
            checkpoint.setAclCheckpoint(objectId);
            pushAclChainToStack(objectId);
            retDoc = aclStack.pop();
          } catch (RepositoryException e) {
            logger.severe("impossible to get the r_object_id of the document");
            return null;
          }
        } else if (isOpen(collectionAclToModify)
            && collectionAclToModify.next()) {
          logger.fine("Looking through the collection of ACLs to modify");
          String eventId = collectionAclToModify.getString("r_object_id");
          String modifyDateToStr =
              collectionAclToModify.getString("time_stamp_utc_str");
          logger.fine("audit event r_object_id is " + eventId
              + ", modifyDate is " + modifyDateToStr);
          checkpoint.setAclModifyCheckpoint(modifyDateToStr, eventId);

          String chronicleId = collectionAclToModify.getString("chronicle_id");
          if (aclModifiedIds.contains(chronicleId)) {
            logger.fine("Skipping redundant modify of: " + chronicleId);
            continue;
          }

          String objectId = collectionAclToModify.getString("audited_obj_id");
          if (collectionAclToModify.getString("event_name").equalsIgnoreCase(
              "dm_destroy")) {
            logger.log(Level.FINE, "ACL to delete: {0}" + objectId);
            retDoc = getDeletedAcl(objectId);
          } else {
            pushAclChainToStack(objectId);
            retDoc = aclStack.pop();
          }
          aclModifiedIds.add(chronicleId);
        } else {
          logger.fine("End of ACL list");
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
   * Processes users and groups from the ACL object, populates the user and
   * group list with users and groups with READ permission. Populates
   * denyusers and denygroup list with restricted users and groups with no 
   * READ permission
   */
  @VisibleForTesting
  void processAcl(IAcl dmAcl, Map<String, List<Value>> aclValues)
      throws RepositoryDocumentException, RepositoryException {
    List<Value> userPrincipals = new ArrayList<Value>();
    List<Value> groupPrincipals = new ArrayList<Value>();
    List<Value> userDenyPrincipals = new ArrayList<Value>();
    List<Value> groupDenyPrincipals = new ArrayList<Value>();

    for (int i = 0; i < dmAcl.getAccessorCount(); i++) {
      String accessorName = dmAcl.getAccessorName(i);
      int permitType = dmAcl.getAccessorPermitType(i);

      IUser userObj = (IUser) session.getObjectByQualification(
          "dm_user where user_name = '" + accessorName + "'");

      String principalName = getPrincipalName(accessorName, userObj);
      if (principalName == null) {
        logger.log(Level.FINE,
            "Skipping invalid username {0} found in ACL.", accessorName);
        continue;
      }
      if (permitType == IAcl.DF_PERMIT_TYPE_ACCESS_RESTRICTION) {
        if (dmAcl.getAccessorPermit(i) <= IAcl.DF_PERMIT_READ) {
          if (dmAcl.isGroup(i)) {
            groupDenyPrincipals.add(asPrincipalValue(principalName,
                getGroupNamespace(accessorName)));
          } else {
            userDenyPrincipals.add(asPrincipalValue(principalName,
                globalNamespace));
          }
        }
      } else if (permitType == IAcl.DF_PERMIT_TYPE_ACCESS_PERMIT) {
        if (dmAcl.getAccessorPermit(i) >= IAcl.DF_PERMIT_READ
            && isAccessorActive(accessorName, userObj)) {
          if (accessorName.equalsIgnoreCase("dm_world")
              || dmAcl.isGroup(i)) {
            groupPrincipals.add(asPrincipalValue(principalName,
                getGroupNamespace(accessorName)));
          } else if (accessorName.equalsIgnoreCase("dm_owner")
              || accessorName.equalsIgnoreCase("dm_group")) {
            // skip dm_owner and dm_group for now.
            // TODO (Srinivas): Need to resolve these acls
            continue;
          } else {
            userPrincipals.add(asPrincipalValue(principalName,
                globalNamespace));
          }
        }
      }
    }
    // add users and groups principals to the map
    aclValues.put(SpiConstants.PROPNAME_ACLUSERS, userPrincipals);
    aclValues.put(SpiConstants.PROPNAME_ACLGROUPS, groupPrincipals);
    aclValues.put(SpiConstants.PROPNAME_ACLDENYUSERS, userDenyPrincipals);
    aclValues.put(SpiConstants.PROPNAME_ACLDENYGROUPS, groupDenyPrincipals);
  }

  /*
   * Creates an empty secure document for a deleted ACL. Named
   * resources cannot be deleted, so we are just returning an empty
   * ACL with no principals, no inherit-from, and the implicit default
   * inheritance-type of LEAF (so it is invalid to inherit from this
   * ACL).
   */
  private Document getDeletedAcl(String objectId) throws RepositoryException {
    Map<String, List<Value>> aclValues = new HashMap<String, List<Value>>();

    aclValues.put(SpiConstants.PROPNAME_DOCID,
        Collections.singletonList(Value.getStringValue(objectId)));
    return SecureDocument.createAcl(aclValues);
  }

  /* Creates a secure document using ACL basic permissions. */
  private Document getBasicAcl(IAcl dmAcl, String aclId,
      String parentAclId) throws RepositoryException {
    Map<String, List<Value>> aclValues = new HashMap<String, List<Value>>();

    aclValues.put(SpiConstants.PROPNAME_DOCID,
        Collections.singletonList(Value.getStringValue(aclId)));

    if (parentAclId != null) {
      logger.log(Level.FINE,
          "ACL {0} has required groups or required group set", aclId);
      aclValues.put(SpiConstants.PROPNAME_ACLINHERITFROM_DOCID,
          Collections.singletonList(Value.getStringValue(parentAclId)));
    }

    aclValues.put(SpiConstants.PROPNAME_ACLINHERITANCETYPE,
        Collections.singletonList(Value.getStringValue(
                SpiConstants.AclInheritanceType.PARENT_OVERRIDES.toString())));
    processAcl(dmAcl, aclValues);

    return SecureDocument.createAcl(aclValues);
  }

  /* Creates a secure document using ACL required group or group set. */
  private Document getRequiredAcl(String aclId,
      String parentAclId, List<String> groups) throws RepositoryException {
    Map<String, List<Value>> aclValues = new HashMap<String, List<Value>>();

    aclValues.put(SpiConstants.PROPNAME_DOCID,
        Collections.singletonList(Value.getStringValue(aclId)));

    if (parentAclId != null) {
      aclValues.put(SpiConstants.PROPNAME_ACLINHERITFROM_DOCID,
          Collections.singletonList(Value.getStringValue(parentAclId)));
    }

    aclValues.put(SpiConstants.PROPNAME_ACLINHERITANCETYPE,
        Collections.singletonList(Value.getStringValue(
                SpiConstants.AclInheritanceType.AND_BOTH_PERMIT.toString())));
    List<Value> groupPrincipals = new ArrayList<Value>();
    for (String name : groups) {
      groupPrincipals.add(asPrincipalValue(name, getGroupNamespace(name)));
    }
    aclValues.put(SpiConstants.PROPNAME_ACLGROUPS, groupPrincipals);

    return SecureDocument.createAcl(aclValues);
  }

  /**
   * Creates the full ACL chain for the Documentum ACL and pushes it
   * onto {@link #aclStack}.
   */
  private void pushAclChainToStack(String objectId) throws RepositoryException {
    IAcl dmAcl = fetchAcl(objectId);
    List<String> requiredGroupSet = new ArrayList<String>();
    String parentAclId = null;

    for (int i = 0; i < dmAcl.getAccessorCount(); i++) {
      String accessorName = dmAcl.getAccessorName(i);
      int permitType = dmAcl.getAccessorPermitType(i);

      if (permitType == IAcl.DF_PERMIT_TYPE_REQUIRED_GROUP) {
        String aclId = objectId + "_" + accessorName;
        Document acl = getRequiredAcl(aclId, parentAclId,
            Collections.singletonList(accessorName));
        aclStack.push(acl);
        parentAclId = aclId;
      } else if (permitType == IAcl.DF_PERMIT_TYPE_REQUIRED_GROUP_SET) {
        requiredGroupSet.add(accessorName);
      }
    }

    if (!requiredGroupSet.isEmpty()) {
      String aclId = objectId + "_reqGroupSet";
      Document acl = getRequiredAcl(aclId, parentAclId, requiredGroupSet);
      aclStack.push(acl);
      parentAclId = aclId;
    }

    Document acl = getBasicAcl(dmAcl, objectId, parentAclId);
    aclStack.push(acl);
  }

  private Value asPrincipalValue(String item, String namespace)
      throws RepositoryDocumentException {
    return Value.getPrincipalValue(new Principal(PrincipalType.UNKNOWN,
        namespace, item, CaseSensitivityType.EVERYTHING_CASE_SENSITIVE));
  }

  private String getPrincipalName(String accessorName, IUser userObj)
      throws RepositoryException {
    if (accessorName.equalsIgnoreCase("dm_world")
        || accessorName.equalsIgnoreCase("dm_owner")
        || accessorName.equalsIgnoreCase("dm_group")) {
      return accessorName;
    }

    if (userObj == null) {
      return null;
    }

    if (!Strings.isNullOrEmpty(userObj.getUserSourceAsString())
        && userObj.getUserSourceAsString().equalsIgnoreCase("ldap")) {
      String dnName = userObj.getUserDistinguishedLDAPName();
      if (Strings.isNullOrEmpty(dnName)) {
        // TODO(jlacey): This is inconsistent with authN, which
        // matches such users against windows_domain. This case
        // probably can't happen, so I don't think it's important.
        logger.log(Level.FINE, "Missing DN for user: {0}", accessorName);
        return null;
      }

      try {
        LdapName dnDomain = IdentityUtil.getDomainComponents(dnName);
        if (!dnDomain.isEmpty()) {
          return IdentityUtil.getFirstDomainFromDN(dnDomain) + "\\"
              + userObj.getUserLoginName();
        }
        // Else fall-through to use windows_domain.
      } catch (InvalidNameException e) {
        logger.log(Level.FINE,
            "Invalid DN " + dnName + " for user: " + accessorName, e);
        return null;
      }
    }

    String principalName;
    String windowsDomain = traversalManager.getWindowsDomain();
    if (!Strings.isNullOrEmpty(windowsDomain) && !userObj.isGroup()) {
      logger.log(Level.FINEST,
          "using configured domain: {0} for unsynchronized user {1}",
          new String[] {windowsDomain, accessorName});
      principalName = windowsDomain + "\\" + userObj.getUserLoginName();
    } else {
      principalName = userObj.getUserLoginName();
    }
    return principalName;
  }

  private boolean isAccessorActive(String accessorName, IUser userObj)
      throws RepositoryException {
    // TODO(Srinivas): Need to disable dm_owner and dm_group as well.
    if (accessorName.equalsIgnoreCase("dm_world")
        || accessorName.equalsIgnoreCase("dm_owner")
        || accessorName.equalsIgnoreCase("dm_group")) {
      return true;
    }

    return userObj != null && userObj.getUserState() == 0;
  }

  private String getGroupNamespace(String usergroup)
      throws RepositoryDocumentException {
    // special group local to repository
    if (usergroup.equalsIgnoreCase("dm_world")) {
      return localNamespace;
    }

    IGroup groupObj = (IGroup) session.getObjectByQualification(
        "dm_group where group_name = '" + usergroup + "'");
    if (groupObj == null) {
      // TODO(jlacey): Return localNamespace instead.
      return null;
    } else if (Strings.isNullOrEmpty(groupObj.getUserSource())) {
      logger.finer("local namespace for group " + usergroup);
      return localNamespace;
    } else {
      logger.finer("global namespace for group " + usergroup);
      return globalNamespace;
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
  @Override
  protected void finalize() {
    try {
      if (isOpen(collectionAcl)) {
        try {
          collectionAcl.close();
          logger.fine("collection of ACLs to add closed");
        } catch (RepositoryException e) {
          logger.warning(
              "Error while closing the collection of ACLs to add: " + e);
        }
      }

      if (isOpen(collectionAclToModify)) {
        try {
          collectionAclToModify.close();
          logger.fine("collection of ACLs to modify closed");
        } catch (RepositoryException e) {
          logger.warning(
              "Error while closing the collection of ACLs to modify: " + e);
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
