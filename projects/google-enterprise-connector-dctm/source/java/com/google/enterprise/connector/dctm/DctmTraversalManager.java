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

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Set;

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

public class DctmTraversalManager implements TraversalManager, TraversalContextAware {
  private static final Logger logger =
      Logger.getLogger(DctmTraversalManager.class.getName());

  private static final Set<String> EMPTY_SET = Collections.<String>emptySet();

  private static final String whereBoundedClause = " and ((r_modify_date = date(''{0}'',''yyyy-mm-dd hh:mi:ss'')  and r_object_id > ''{1}'') OR ( r_modify_date > date(''{0}'',''yyyy-mm-dd hh:mi:ss'')))";
  private static final String whereBoundedClauseRemove = " and ((time_stamp = date(''{0}'',''yyyy-mm-dd hh:mi:ss'') and (r_object_id > ''{1}'')) OR ( time_stamp > date(''{0}'',''yyyy-mm-dd hh:mi:ss'')))";
  private static final String whereBoundedClauseRemoveDateOnly = " and ( time_stamp > date(''{0}'',''yyyy-mm-dd hh:mi:ss''))";

  private final SimpleDateFormat dateFormat =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private final String serverUrl;
  private int batchHint = -1;
  private ISessionManager sessionManager;
  private IClientX clientX;
  private TraversalContext traversalContext = null;
  private final Map<String, IType> superTypeCache;

  private final String additionalWhereClause;
  private final boolean isPublic;
  private final Set<String> includedObjectType;
  private final Set<String> includedMeta;
  private final Set<String> excludedMeta;
  private final String rootObjectType;

  // Constructor used by tests.
  public DctmTraversalManager(IClientX clientX, String webtopServerUrl,
      Set<String> included_meta, ISessionManager sessionMgr)
      throws RepositoryException {
    this(clientX, webtopServerUrl, "", true, included_meta, EMPTY_SET,
        EMPTY_SET, "");
    setSessionManager(sessionMgr);
  }

  public DctmTraversalManager(IClientX clientX, String webtopServerUrl,
      String additionalWhereClause, boolean isPublic,
      Set<String> includedMeta, Set<String> excludedMeta,
      Set<String> includedObjectType, String rootObjectType)
      throws RepositoryException {
    this.additionalWhereClause = additionalWhereClause;
    setClientX(clientX);
    setSessionManager(clientX.getSessionManager());

    this.superTypeCache = new HashMap<String, IType>();

    this.serverUrl = webtopServerUrl;
    this.isPublic = isPublic;
    this.includedObjectType = includedObjectType;
    this.includedMeta = includedMeta;
    this.excludedMeta = excludedMeta;
    this.rootObjectType = rootObjectType;
  }

  protected void setClientX(IClientX clientX) {
    this.clientX = clientX;
  }

  public IClientX getClientX() {
    return clientX;
  }

  public ISessionManager getSessionManager() {
    return sessionManager;
  }

  public void setSessionManager(ISessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }

  protected String getServerUrl() {
    return serverUrl;
  }

  public void setTraversalContext(TraversalContext traversalContext) {
    this.traversalContext = traversalContext;
  }

  public TraversalContext getTraversalContext() {
    return traversalContext;
  }

  public Map<String, IType> getSuperTypeCache() {
    return superTypeCache;
  }

  public Set<String> getIncludedMeta() {
    return includedMeta;
  }

  public Set<String> getExcludedMeta() {
    return excludedMeta;
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
  public DocumentList startTraversal() throws RepositoryException {
    logger.info("StartTraversal");
    return execQuery(forgeStartCheckpoint());
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
  public DocumentList resumeTraversal(String checkPoint)
      throws RepositoryException {
    logger.info("ResumeTraversal from checkpoint: " + checkPoint);
    return execQuery(new Checkpoint(checkPoint));
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
  public void setBatchHint(int batchHint) throws RepositoryException {
    logger.info("batchHint of " + batchHint);
    this.batchHint = batchHint;
  }

  /**
   * Execute queries to retrieve the documents to add to the GSA and the
   * document to remove from the GSA.
   *
   * @param checkpoint the Checkpoint from which to resume traversal.
   * @return DocumentList of traversal results.
   * @throws RepositoryException
   */
  protected DocumentList execQuery(Checkpoint checkpoint)
      throws RepositoryException {
    sessionManager.setServerUrl(serverUrl);
    ICollection collecToAdd = null;
    ICollection collecToDel = null;

    ISession sessAdd = null;
    ISession sessDel = null;

    IQuery query = buildAddQuery(checkpoint);
    IQuery queryDocToDel = buildDelQuery(checkpoint);

    DocumentList documentList = null;

    try {
      if (query != null) {
        sessAdd = sessionManager.getSession(sessionManager.getDocbaseName());
        sessionManager.setSessionAdd(sessAdd);
        collecToAdd = query.execute(sessAdd, IQuery.EXECUTE_READ_QUERY);
        logger.fine("execution of the query returns a collection of documents"
                    + " to add");
      }

      if (queryDocToDel != null) {
        sessDel = sessionManager.getSession(sessionManager.getDocbaseName());
        sessionManager.setSessionDel(sessDel);
        collecToDel = queryDocToDel.execute(sessDel, IQuery.EXECUTE_READ_QUERY);
        logger.fine("execution of the query returns a collection of documents"
                    + " to delete");
      }

      if ((collecToAdd != null && collecToAdd.hasNext()) ||
          (collecToDel != null && collecToDel.hasNext())) {
        documentList =
            new DctmDocumentList(this, collecToAdd, collecToDel, checkpoint);
      }
    } finally {
      // No documents to add or delete.   Return a null DocumentList,
      // but close the collections first!
      if (documentList == null) {
        if (collecToAdd != null) {
          try {
            collecToAdd.close();
            logger.fine("collection of documents to add closed");
            sessionManager.releaseSessionAdd();
            logger.fine("collection session released");
          } catch (RepositoryException e) {
            logger.severe("Error while closing the collection of documents"
                          + " to add: " + e);
          }
        }
        if (collecToDel != null) {
          try {
            collecToDel.close();
            logger.fine("collection of documents to delete closed");
            sessionManager.releaseSessionDel();
            logger.fine("collection session released");
          } catch (RepositoryException e) {
            logger.severe("Error while closing the collection of documents"
                          + " to delete: " + e);
          }
        }
      }
    }

    return documentList;
  }

  protected Checkpoint forgeStartCheckpoint() {
    Checkpoint checkpoint = new Checkpoint();
    // Only consider delete actions that occur from this moment onward.
    checkpoint.setDeleteCheckpoint(new java.util.Date(), null);
    return checkpoint;
  }

  protected IQuery makeQuery(String queryStr) {
    IQuery query = clientX.getQuery();
    query.setDQL(queryStr);
    return query;
  }

  protected IQuery buildAddQuery(Checkpoint checkpoint) {
    StringBuilder queryStr = new StringBuilder();
    baseQueryString(queryStr);
    if (checkpoint.insertId != null && checkpoint.insertDate != null) {
      Object[] arguments = { dateFormat.format(checkpoint.insertDate),
                             checkpoint.insertId };
      queryStr.append(MessageFormat.format(whereBoundedClause, arguments));
    }
    queryStr.append(" order by r_modify_date,r_object_id");
    if (batchHint > 0) {
      queryStr.append(" ENABLE (return_top ").append(batchHint).append(')');
    }
    logger.fine("queryToAdd completed : " + queryStr.toString());
    return makeQuery(queryStr.toString());
  }

  public String buildVersionsQueryString(String chronicleId) {
    StringBuilder queryStr = new StringBuilder();
    baseQueryString(queryStr);
    queryStr.append(" and i_chronicle_id='").append(chronicleId);
    queryStr.append("' order by r_modify_date,r_object_id desc");
    return queryStr.toString();
  }

  protected void baseQueryString(StringBuilder query) {
    query.append("select i_chronicle_id, r_object_id, r_modify_date from ");
    query.append(rootObjectType);
    query.append(" where ");
    if (!includedObjectType.isEmpty()) {
      DqlUtils.appendObjectTypes(query, includedObjectType);
    } else {
      // FIXME: Append the WHERE text only when needed.
      query.append("1=1 ");
    }
    if (additionalWhereClause != null && additionalWhereClause.length() > 0) {
      logger.fine("adding the additionalWhereClause to the query : "
                  + additionalWhereClause);
      query.append(" and (").append(additionalWhereClause).append(")");
    }
  }

  protected IQuery buildDelQuery(Checkpoint checkpoint) {
    StringBuilder queryStr = new StringBuilder(
        "select r_object_id, chronicle_id, audited_obj_id, time_stamp "
        + "from dm_audittrail "
        + "where (event_name='dm_destroy' or event_name='dm_prune') ");
    if (checkpoint.deleteDate != null) {
      Object[] arguments = { dateFormat.format(checkpoint.deleteDate),
                             checkpoint.deleteId };
      queryStr.append(MessageFormat.format(
          (arguments[1] == null) ? whereBoundedClauseRemoveDateOnly : whereBoundedClauseRemove,
          arguments));
    }
    queryStr.append(" order by time_stamp,r_object_id");
    if (batchHint > 0) {
      queryStr.append(" ENABLE (return_top ").append(batchHint).append(')');
    }
    logger.info("queryToDel.toString()" + queryStr.toString());
    return makeQuery(queryStr.toString());
  }

  public boolean isPublic() {
    return isPublic;
  }
}
