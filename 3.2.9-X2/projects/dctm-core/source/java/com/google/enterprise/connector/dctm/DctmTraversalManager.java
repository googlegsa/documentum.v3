// Copyright 2006 Google Inc.
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

import com.google.common.base.Strings;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.TraversalContext;
import com.google.enterprise.connector.spi.TraversalContextAware;
import com.google.enterprise.connector.spi.TraversalManager;
import com.google.enterprise.connector.util.EmptyDocumentList;
import com.google.enterprise.connector.util.TraversalTimer;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class DctmTraversalManager
    implements TraversalManager, TraversalContextAware {
  private static final Logger logger =
      Logger.getLogger(DctmTraversalManager.class.getName());

  private static final List<String> EMPTY_LIST =
      Collections.<String>emptyList();

  private static final Set<String> EMPTY_SET = Collections.<String>emptySet();

  private static final String whereBoundedClause = " and ((r_modify_date = date(''{0}'',''yyyy-mm-dd hh:mi:ss'') and r_object_id > ''{1}'') OR (r_modify_date > date(''{0}'',''yyyy-mm-dd hh:mi:ss'')))";
  private static final String whereBoundedClauseRemove = " and ((time_stamp_utc = date(''{0}'',''yyyy-mm-dd hh:mi:ss'') and (r_object_id > ''{1}'')) OR (time_stamp_utc > date(''{0}'',''yyyy-mm-dd hh:mi:ss'')))";
  private static final String whereBoundedClauseRemoveDateOnly = " and (time_stamp_utc > date(''{0}'',''yyyy-mm-dd hh:mi:ss''))";
  private static final String whereClauseAcl = " where r_object_id > ''{0}''";

  private final SimpleDateFormat dateFormat =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private final String docbase;
  private final String serverUrl;
  private int batchHint = -1;
  private final ISessionManager sessionManager;
  private final IClientX clientX;
  private TraversalContext traversalContext = null;

  private final Map<String, IType> superTypeCache =
      new HashMap<String, IType>();
  private final Map<String, List<String>> typeAttributesCache =
      new HashMap<String, List<String>>();

  private final List<String> additionalWhereClause;
  private final boolean isPublic;
  private final Set<String> includedObjectType;
  private final Set<String> includedMeta;
  private final Set<String> excludedMeta;
  private final String rootObjectType;
  private final String globalNamespace;
  private final String localNamespace;
  private final String windowsDomain;

  public DctmTraversalManager(DctmConnector connector,
      ISessionManager sessionManager) throws RepositoryException {
    this(connector.getClientX(), connector.getDocbase(),
        connector.getWebtopDisplayUrl(), connector.getWhereClause(),
        connector.isPublic(), connector.getRootObjectType(),
        connector.getIncludedObjectType(), connector.getIncludedMeta(),
        connector.getExcludedMeta(),
        connector.getGoogleGlobalNamespace(),
        connector.getGoogleLocalNamespace(),
        connector.getWindowsDomain(), sessionManager);
  }

  /** Constructor used by tests. */
  DctmTraversalManager(IClientX clientX, String docbase,
      String webtopServerUrl, Set<String> included_meta,
      ISessionManager sessionManager) throws RepositoryException {
    this(clientX, docbase, webtopServerUrl, EMPTY_LIST, true, "", EMPTY_SET,
        included_meta, EMPTY_SET, null, null, null, sessionManager); 
    //Srinivas TODO: add global, local name space for tests
  }

  private DctmTraversalManager(IClientX clientX, String docbase,
      String webtopServerUrl, List<String> additionalWhereClause,
      boolean isPublic, String rootObjectType, Set<String> includedObjectType,
      Set<String> includedMeta, Set<String> excludedMeta,
      String globalnamespace, String localnamespace, String windowsDomain,
      ISessionManager sessionManager) throws RepositoryException {
    this.additionalWhereClause = additionalWhereClause;
    this.clientX = clientX;
    this.sessionManager = sessionManager;

    this.docbase = docbase;
    this.serverUrl = webtopServerUrl;
    this.isPublic = isPublic;
    this.includedObjectType = includedObjectType;
    this.includedMeta = includedMeta;
    this.excludedMeta = excludedMeta;
    this.rootObjectType = rootObjectType;
    this.globalNamespace = globalnamespace;
    this.localNamespace = localnamespace;
    this.windowsDomain = windowsDomain;
  }

  IClientX getClientX() {
    return clientX;
  }

  ISessionManager getSessionManager() {
    return sessionManager;
  }

  String getDocbase() {
    return docbase;
  }

  String getServerUrl() {
    return serverUrl;
  }

  @Override
  public void setTraversalContext(TraversalContext traversalContext) {
    this.traversalContext = traversalContext;
  }

  TraversalContext getTraversalContext() {
    return traversalContext;
  }

  Map<String, IType> getSuperTypeCache() {
    return superTypeCache;
  }

  Map<String, List<String>> getTypeAttributesCache() {
    return typeAttributesCache;
  }

  boolean isPublic() {
    return isPublic;
  }

  Set<String> getIncludedMeta() {
    return includedMeta;
  }

  Set<String> getExcludedMeta() {
    return excludedMeta;
  }

  public String getGlobalNamespace() {
    return globalNamespace;
  }

  public String getLocalNamespace() {
    return localNamespace;
  }

  public String getWindowsDomain() {
    return windowsDomain;
  }
  /**
   * Starts (or restarts) traversal from the beginning. This action will
   * return objects starting from the very oldest, or with the smallest IDs,
   * or whatever natural order the implementation prefers. The caller may
   * consume as many or as few of the results as it wants, but it will
   * call {@link #checkpoint()} when it has finished with the results.
   *
   * @return A DocumentList of Documents from the repository in natural order
   * @throws RepositoryException
   *             if the Repository is unreachable or similar exceptional
   *             condition.
   */
  @Override
  public DocumentList startTraversal() throws RepositoryException {
    logger.info("StartTraversal");
    return getDocumentList(forgeStartCheckpoint());
  }

  /**
   * Continues traversal from a supplied checkpoint. The checkPoint parameter
   * will have been created by a call to the {@link #checkpoint(PropertyMap)}
   * method. The ResultSet object returns objects from the repository in
   * natural order starting just after the document that was used to create
   * the checkpoint string.
   *
   * @param checkPoint
   *            String that indicates from where to resume traversal.
   * @return DocumentList object that returns Documents starting just after
   *         the checkpoint.
   * @throws RepositoryException
   */
  @Override
  public DocumentList resumeTraversal(String checkPoint)
      throws RepositoryException {
    logger.info("ResumeTraversal from checkpoint: " + checkPoint);
    return getDocumentList(new Checkpoint(additionalWhereClause, checkPoint));
  }

  /**
   * Sets the preferred batch size. The caller advises the implementation that
   * the result sets returned by startTraversal or resumeTraversal need not be
   * larger than this number. The implementation may ignore this call or do
   * its best to return approximately this number.
   *
   * @param batchHint
   * @throws RepositoryException
   */
  @Override
  public void setBatchHint(int batchHint) throws RepositoryException {
    logger.info("batchHint of " + batchHint);
    this.batchHint = batchHint;
  }

  /**
   * Gets a document list. This method exists to handle multiple
   * additional where clauses.
   *
   * @param checkpoint the Checkpoint from which to resume traversal.
   * @return DocumentList of traversal results.
   * @throws RepositoryException
   */
  /* @VisibleForTesting */
  final DocumentList getDocumentList(Checkpoint checkpoint)
      throws RepositoryException {
    // In the case of multiple where clauses, execute them in turn
    // until one returns results, all of them have been tried,
    // or time expires.
    TraversalTimer timer = new TraversalTimer(traversalContext);
    boolean isMore;
    do {
      DocumentList documentList = execQuery(checkpoint);
      if (documentList != null)
        return documentList;
      isMore = checkpoint.advance();
    } while (isMore && timer.isTicking());
    return isMore ? new EmptyDocumentList(checkpoint.asString()) : null;
  }

  /**
   * Execute queries to retrieve the documents to add to the GSA and the
   * document to remove from the GSA.
   *
   * @param checkpoint the Checkpoint from which to resume traversal.
   * @return DocumentList of traversal results.
   * @throws RepositoryException
   */
  /* @VisibleForTesting */
  DocumentList execQuery(Checkpoint checkpoint) throws RepositoryException {
    ICollection collecAclToAdd = null;
    ICollection collecToAdd = null;
    ICollection collecToDel = null;
    ICollection collecAclToModify = null;
    ISession session = null;

    DocumentList documentList = null;

    try {
      session = sessionManager.getSession(docbase);

      if (checkpoint.getInsertIndex() == -1) {
        logger.fine("Processing Acls");
        IQuery queryAclToAdd = buildACLQuery(checkpoint);
        collecAclToAdd = queryAclToAdd.execute(session,
            IQuery.EXECUTE_READ_QUERY);
        logger.fine("execution of the query returns a collection of ACLs"
            + " to add");

        IQuery queryAclToModify = buildAclModifyQuery(checkpoint);
        collecAclToModify = queryAclToModify.execute(session,
            IQuery.EXECUTE_READ_QUERY);
        logger.fine("execution of the query returns a collection of ACLs"
            + " to modify");

        if ((collecAclToAdd != null && collecAclToAdd.hasNext())
            || (collecAclToModify != null && collecAclToModify.hasNext())) {
          documentList = new DctmAclList(this, session, collecAclToAdd,
              collecAclToModify, checkpoint);
        }
      } else {
        logger.fine("Processing Documents");
        IQuery query = buildAddQuery(checkpoint);
        collecToAdd = query.execute(session, IQuery.EXECUTE_READ_QUERY);
        logger.fine("execution of the query returns a collection of documents"
            + " to add");

        // Only execute the delete query with one of the add queries.
        // TODO: We could treat the delete query as a peer of the others,
        // and include it in the sequence.
        if (checkpoint.getInsertIndex() == 0) {
          IQuery queryDocToDel = buildDelQuery(checkpoint);
          collecToDel = queryDocToDel.execute(session,
              IQuery.EXECUTE_READ_QUERY);
          logger.fine("execution of the query returns a collection of " +
              "documents to delete");
        }

        if ((collecToAdd != null && collecToAdd.hasNext())
            || (collecToDel != null && collecToDel.hasNext())) {
          documentList = new DctmDocumentList(this, session, collecToAdd,
              collecToDel, checkpoint);
        }
      }
    } finally {
      if (documentList == null) {
        // No documents to add or delete. Return a null DocumentList,
        // but close the collections and release the session first.
        try {
          closeCollection(collecAclToAdd,
              "collection of ACLs to add closed",
              "Error while closing the collection of ACLs to add");
          closeCollection(collecAclToModify,
              "collection of ACLs to modify closed",
              "Error while closing the collection of ACLs to modify");
          closeCollection(collecToAdd,
              "collection of documents to add closed",
              "Error while closing the collection of documents to add");
          closeCollection(collecToDel,
              "collection of documents to delete closed",
              "Error while closing the collection of documents to delete");
        } finally {
          if (session != null) {
            sessionManager.release(session);
            logger.fine("collection session released");
          }
        }
      }
    }
    return documentList;
  }

  private void closeCollection(ICollection collection, String message,
      String errorMessage) {
    if (collection != null) {
      try {
        collection.close();
        logger.fine(message);
      } catch (RepositoryException e) {
        logger.severe(errorMessage + ": " + e);
      }
    }
  }

  protected Checkpoint forgeStartCheckpoint() {
    Checkpoint checkpoint = new Checkpoint(additionalWhereClause);
    // Only consider delete actions that occur from this moment onward.
    checkpoint.setDeleteCheckpoint(dateFormat.format(new Date()), null);
    // Only consider ACL changes that occur from this moment onward.
    checkpoint.setAclModifyCheckpoint(dateFormat.format(new Date()), null);
    return checkpoint;
  }

  protected IQuery makeQuery(String queryStr) {
    IQuery query = clientX.getQuery();
    query.setDQL(queryStr);
    return query;
  }

  protected IQuery buildAddQuery(Checkpoint checkpoint) {
    StringBuilder queryStr = new StringBuilder();
    baseQueryString(queryStr, checkpoint);
    if (checkpoint.getInsertId() != null
        && checkpoint.getInsertDate() != null) {
      Object[] arguments =
          {checkpoint.getInsertDate(), checkpoint.getInsertId()};
      queryStr.append(MessageFormat.format(whereBoundedClause, arguments));
    }
    queryStr.append(" order by r_modify_date,r_object_id");
    if (batchHint > 0) {
      queryStr.append(" ENABLE (return_top ").append(batchHint).append(')');
    }
    logger.fine("queryToAdd completed: " + queryStr.toString());
    return makeQuery(queryStr.toString());
  }

  public String buildVersionsQueryString(Checkpoint checkpoint,
      String chronicleId) {
    StringBuilder queryStr = new StringBuilder();
    baseQueryString(queryStr, checkpoint);
    queryStr.append(" and i_chronicle_id='").append(chronicleId);
    queryStr.append("' order by r_modify_date,r_object_id desc");
    return queryStr.toString();
  }

  protected void baseQueryString(StringBuilder query, Checkpoint checkpoint) {
    query.append("select i_chronicle_id, r_object_id, r_modify_date, ");
    query.append("DATETOSTRING(r_modify_date, 'yyyy-mm-dd hh:mi:ss') ");
    query.append("as r_modify_date_str from ");
    query.append(rootObjectType);
    query.append(" where ");
    if (!includedObjectType.isEmpty()) {
      DqlUtils.appendObjectTypes(query, includedObjectType);
    } else {
      // FIXME: Append the WHERE text only when needed.
      query.append("1=1 ");
    }
    int index = checkpoint.getInsertIndex();
    if (additionalWhereClause.size() > index) {
      String whereClause = additionalWhereClause.get(index);
      logger.fine("adding the additionalWhereClause to the query: "
          + whereClause);
      query.append(" and (").append(whereClause).append(")");
    }
  }

  protected IQuery buildDelQuery(Checkpoint checkpoint) {
    StringBuilder queryStr = new StringBuilder(
        "select r_object_id, chronicle_id, audited_obj_id, time_stamp_utc, "
        + "DATETOSTRING(time_stamp_utc, 'yyyy-mm-dd hh:mi:ss') "
        + "as time_stamp_utc_str "
        + "from dm_audittrail "
        + "where (event_name='dm_destroy' or event_name='dm_prune')");
    if (checkpoint.getDeleteDate() != null) {
      Object[] arguments =
          {checkpoint.getDeleteDate(), checkpoint.getDeleteId()};
      queryStr.append(MessageFormat.format(
          (arguments[1] == null) ? whereBoundedClauseRemoveDateOnly : whereBoundedClauseRemove,
          arguments));
    }
    queryStr.append(" order by time_stamp_utc,r_object_id");
    if (batchHint > 0) {
      queryStr.append(" ENABLE (return_top ").append(batchHint).append(')');
    }
    logger.fine("queryToDel completed: " + queryStr.toString());
    return makeQuery(queryStr.toString());
  }

  protected IQuery buildACLQuery(Checkpoint checkpoint) {
    StringBuilder queryStr = new StringBuilder();
    queryStr.append("select r_object_id from dm_acl");
    if (!Strings.isNullOrEmpty(checkpoint.getAclId())) {
      queryStr.append(MessageFormat.format(whereClauseAcl,
          checkpoint.getAclId()));
    }
    queryStr.append(" order by r_object_id");
    if (batchHint > 0) {
      queryStr.append(" ENABLE (return_top ").append(batchHint).append(')');
    }
    logger.fine("ACL queryToAdd completed: " + queryStr.toString());
    return makeQuery(queryStr.toString());
  }

  protected IQuery buildAclModifyQuery(Checkpoint checkpoint) {
    StringBuilder queryStr = new StringBuilder(
        "select r_object_id, chronicle_id, audited_obj_id, event_name, "
        + "time_stamp_utc, "
        + "DATETOSTRING(time_stamp_utc, 'yyyy-mm-dd hh:mi:ss') "
        + "as time_stamp_utc_str "
        + "from dm_audittrail_acl "
        + "where (event_name='dm_save' or event_name='dm_saveasnew' "
        + "or event_name='dm_destroy')");

    if (checkpoint.getAclModifiedDate() != null) {
      Object[] arguments =
          {checkpoint.getAclModifiedDate(), checkpoint.getAclModifyId()};
      queryStr.append(MessageFormat.format(
          (arguments[1] == null) ? whereBoundedClauseRemoveDateOnly
              : whereBoundedClauseRemove, arguments));
    }
    queryStr.append(" order by time_stamp_utc, r_object_id, event_name");
    if (batchHint > 0) {
      queryStr.append(" ENABLE (return_top ").append(batchHint).append(')');
    }
    logger.fine("queryAclModify completed: " + queryStr.toString());
    return makeQuery(queryStr.toString());
  }
}
