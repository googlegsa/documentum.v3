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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.DocumentList;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.TraversalContext;
import com.google.enterprise.connector.spi.TraversalContextAware;
import com.google.enterprise.connector.spi.TraversalManager;

public class DctmTraversalManager implements TraversalManager, TraversalContextAware {
  private static final Logger logger =
      Logger.getLogger(DctmTraversalManager.class.getName());

  private String order_by = " order by r_modify_date,r_object_id";
  private String whereBoundedClause = " and ((r_modify_date = date(''{0}'',''yyyy-mm-dd hh:mi:ss'')  and r_object_id > ''{1}'') OR ( r_modify_date > date(''{0}'',''yyyy-mm-dd hh:mi:ss'')))";
  private String whereBoundedClauseRemove = " and ((time_stamp = date(''{0}'',''yyyy-mm-dd hh:mi:ss'') and (r_object_id > ''{1}'')) OR ( time_stamp > date(''{0}'',''yyyy-mm-dd hh:mi:ss'')))";
  private String whereBoundedClauseRemoveDateOnly = " and ( time_stamp > date(''{0}'',''yyyy-mm-dd hh:mi:ss''))";

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private String serverUrl;
  private int batchHint = -1;
  private ISessionManager sessionManager;
  private IClientX clientX;

  protected String additionalWhereClause;
  private boolean isPublic;
  private Set<String> hash_included_object_type;
  private Set<String> hash_included_meta;
  private String root_object_type;

  private TraversalContext traversalContext = null;

  public DctmTraversalManager(IClientX clientX, String webtopServerUrl,
      String additionalWhereClause, boolean isPublic, String included_meta,
      String included_object_type, String root_object_type)
      throws RepositoryException {

    this.additionalWhereClause = additionalWhereClause;
    setClientX(clientX);
    setSessionManager(clientX.getSessionManager());

    this.serverUrl = webtopServerUrl;
    this.isPublic = isPublic;
    setHash_included_object_type(included_object_type);
    setHash_included_meta(included_meta);
    this.root_object_type = root_object_type;
  }

  protected void setClientX(IClientX clientX) {
    this.clientX = clientX;
  }

  protected IClientX getClientX() {
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

    IQuery query = makeCheckpointQuery(buildQueryString(checkpoint));
    IQuery queryDocToDel = makeCheckpointQuery(buildQueryStringToDel(checkpoint));

    DocumentList documentList = null;

    try {
      if (query != null) {
        sessAdd = sessionManager.getSession(sessionManager.getDocbaseName());
        sessionManager.setSessionAdd(sessAdd);
        collecToAdd = query.execute(sessAdd, IQuery.EXECUTE_READ_QUERY);
        logger.fine("execution of the query returns a collection of document to add");
      }

      if (queryDocToDel != null) {
        sessDel = sessionManager.getSession(sessionManager.getDocbaseName());
        sessionManager.setSessionDel(sessDel);
        collecToDel = queryDocToDel.execute(sessDel, IQuery.EXECUTE_READ_QUERY);
        logger.fine("execution of the query returns a collection of document to delete");
      }

      if ((collecToAdd != null && collecToAdd.hasNext()) ||
          (collecToDel != null && collecToDel.hasNext())) {
        documentList = new DctmDocumentList(collecToAdd, collecToDel, sessionManager,
            clientX, isPublic, hash_included_meta, checkpoint, traversalContext);
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
            logger.severe("Error while closing the collection of documents to add: " + e);
          }
        }
        if (collecToDel != null) {
          try {
            collecToDel.close();
            logger.fine("collection of documents to delete closed");
            sessionManager.releaseSessionDel();
            logger.fine("collection session released");
          } catch (RepositoryException e) {
            logger.severe("Error while closing the collection of documents to delete: " + e);
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

  protected IQuery makeCheckpointQuery(String queryString) {
    IQuery query = null;
    query = clientX.getQuery();
    query.setDQL(queryString);
    return query;
  }

  protected String buildQueryString(Checkpoint checkpoint) {
    StringBuilder query = new StringBuilder(
        "select i_chronicle_id, r_object_id, r_modify_date from ");
    query.append(root_object_type);
    if (!hash_included_object_type.isEmpty()) {
      query.append(" where (");
      Iterator<String> iter = hash_included_object_type.iterator();
      String name = iter.next();
      query.append(" r_object_type='" + name + "'");
      while (iter.hasNext()) {
        name = iter.next();
        query.append(" OR r_object_type='" + name + "'");
      }
      query.append(")");
    } else {
      query.append(" where r_object_type='");
      query.append("dm_document");
      query.append("' ");
    }
    if (additionalWhereClause != null && additionalWhereClause.trim().length() > 0) {
      logger.fine("adding the additionalWhereClause to the query : " + additionalWhereClause);
      ///adding the "and" operator if not present in the additional where clause (mandatory)
      if (!additionalWhereClause.toLowerCase().startsWith("and ")) {
        logger.log(Level.INFO, "clause does not start with AND : ");
        additionalWhereClause = "and ".concat(additionalWhereClause);
        logger.log(Level.INFO, "after adding AND : " + additionalWhereClause);
      }

      query.append(additionalWhereClause);
    }
    if (checkpoint.insertId != null && checkpoint.insertDate != null) {
      logger.fine("adding the checkpoint to the query : " + checkpoint);
      Object[] arguments = { dateFormat.format(checkpoint.insertDate), checkpoint.insertId };
      query.append(MessageFormat.format(whereBoundedClause, arguments));
    }

    query.append(order_by);

    if (batchHint > 0) {
      if (query.indexOf("ENABLE (return_top") == -1) {
        query.append(" ENABLE (return_top "
            + Integer.toString(batchHint) + ")");
      } else {
        int a = query.indexOf(" ENABLE (return_top");
        query.replace(a, query.length(), " ENABLE (return_top "
            + Integer.toString(batchHint) + ")");
      }
    }
    logger.fine("query completed : " + query.toString());
    return query.toString();
  }

  protected String buildQueryStringToDel(Checkpoint checkpoint) {
    StringBuilder query = new StringBuilder(
        "select r_object_id, chronicle_id, time_stamp from dm_audittrail ");
    query.append("where ");
    query.append("event_name='dm_destroy' ");

    if (checkpoint.deleteDate != null) {
      logger.fine("adding the checkpoint to the query : " + checkpoint);
      Object[] arguments = { dateFormat.format(checkpoint.deleteDate), checkpoint.deleteId };
      String whereClause = MessageFormat.format(
           (arguments[1] == null) ? whereBoundedClauseRemoveDateOnly : whereBoundedClauseRemove,
           arguments);
    }
    logger.info("query.toString()" + query.toString());
    return  query.toString();
  }

  public boolean isPublic() {
    return isPublic;
  }

  public void setPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }

  protected void setHash_included_object_type(String included_object_type) {
    hash_included_object_type = new HashSet<String>();
    String[] hashTab = included_object_type.split(",");
    for (int i = 0; i < hashTab.length; i++) {
      hash_included_object_type.add(hashTab[i]);
    }
  }

  protected void setHash_included_meta(String included_metadata) {
    hash_included_meta = new HashSet<String>();
    String[] hashTab = included_metadata.split(",");
    for (int i = 0; i < hashTab.length; i++) {
      hash_included_meta.add(hashTab[i]);
    }
  }
}
