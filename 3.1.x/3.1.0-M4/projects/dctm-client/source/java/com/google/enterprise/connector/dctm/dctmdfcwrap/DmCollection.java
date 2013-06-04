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

package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryException;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DmCollection implements ICollection {
  private final IDfCollection idfCollection;

  private int numberOfRows = 0;

  private boolean didPeek = false;
  private boolean peekResult;
  private int peekState;

  private static Logger logger = Logger.getLogger(DmCollection.class
      .getName());

  public DmCollection(IDfCollection idfCollection) {
    this.idfCollection = idfCollection;
    numberOfRows = 0;
    didPeek = false;
  }

  public IValue getValue(String attrName) throws RepositoryException {
    if (didPeek) {
      throw new IllegalStateException("Cannot access current row after hasNext()");
    }
    IDfValue dfValue = null;
    try {
      dfValue = idfCollection.getValue(attrName);
      if (logger.isLoggable(Level.FINEST))
        logger.finest("getting the value of attribute " + attrName);
    } catch (DfException e) {
      RepositoryException re = new RepositoryException(e);
      throw re;
    }
    return new DmValue(dfValue);
  }

  /* IDfCollection.DF_NO_MORE_ROWS_STATE is fundamentally broken
   * as it is not testable from DF_INITIAL_STATE.   Therefore, we
   * cannot tell if a IDfCollection as more than zero rows without
   * calling next() first.  This hasNext() peeks ahead, actually
   * calling next() under the covers, then caching the result.
   * This has a serious side effect, in that once hasNext() has
   * been called, you cannot access data from the "current" row
   * (as it is no longer "current"), until you actually call next().
   * At this time, hasNext() is only called at the time the collection
   * is in DF_INITIAL_STATE, so there is no "current" row.
   */
  public boolean hasNext() throws RepositoryException {
    if (!didPeek) {
      peekState = getState();
      peekResult = next();
      didPeek = true;
    }
    return peekResult;
  }

  public boolean next() throws RepositoryException {
    if (didPeek) {
      didPeek = false;
      return peekResult;
    }

    boolean isNext;
    try {
      isNext = idfCollection.next();
    } catch (DfException e) {
      throw new RepositoryException(e);
    }
    if (isNext) {
      numberOfRows++;
      if (logger.isLoggable(Level.FINEST)) {
        logger.finest("Collection positioned on row " + numberOfRows);
      }
    } else if (logger.isLoggable(Level.FINEST)) {
      logger.finest("End of collection, number Of Rows is " + numberOfRows);
    }
    return isNext;
  }

  public String getAllRepeatingStrings(String colName, String separator)
      throws RepositoryException {
    try {
      String colValues = idfCollection.getAllRepeatingStrings(colName,
          separator);
      if (logger.isLoggable(Level.FINEST))
        logger.finest("column values are " + colValues);
      return colValues;
    } catch (DfException e) {
      throw new RepositoryException(e);
    }
  }

  public String getString(String colName) throws RepositoryException {
    if (didPeek) {
      throw new IllegalStateException("Cannot access current row after hasNext()");
    }
    try {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("column value is " + idfCollection.getString(colName));
      return idfCollection.getString(colName);
    } catch (DfException e) {
      throw new RepositoryException(e);
    }
  }

  public ITime getTime(String colName) throws RepositoryException {
    IDfTime dfTime = null;
    try {
      if (logger.isLoggable(Level.FINEST))
        logger.finest("column value is " + idfCollection.getTime(colName));
      dfTime = idfCollection.getTime(colName);
    } catch (DfException e) {
      throw new RepositoryException(e);
    }
    return new DmTime(dfTime);
  }

  public void close() throws RepositoryException {
    if (logger.isLoggable(Level.FINEST))
      logger.finest(numberOfRows + " rows have been processed");
    try {
      didPeek = false;
      idfCollection.close();
    } catch (DfException e) {
      throw new RepositoryException(e);
    }
  }

  public int getState() {
    int state = (didPeek) ? peekState : idfCollection.getState();
    logger.fine("state of the collection : " + state);
    return state;
  }

  public ISession getSession() {
    logger.finest("getting the session from the collection");
    return new DmSession(idfCollection.getSession());
  }

  protected void finalize() throws RepositoryException {
    if (getState() != ICollection.DF_CLOSED_STATE) {
      logger.warning("Open DmCollection getting reaped by GC: " + this);
      close();
    }
  }
}
