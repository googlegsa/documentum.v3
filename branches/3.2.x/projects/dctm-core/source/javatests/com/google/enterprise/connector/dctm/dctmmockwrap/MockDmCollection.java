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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.query.QueryResult;

/** An ICollection implementation on top of jcr. */
public class MockDmCollection implements ICollection {
  private final NodeIterator collection;

  private Node currentNode;

  protected MockDmCollection(QueryResult mjQueryResult)
      throws RepositoryException {
    try {
      collection = mjQueryResult.getNodes();
    } catch (javax.jcr.RepositoryException e) {
      throw new RepositoryException(e);
    }
  }

  @Override
  public boolean next() {
    if (collection.hasNext()) {
      currentNode = collection.nextNode();
      return true;
    } else {
      currentNode = null;
      return false;
    }
  }

  @Override
  public String getString(String colName) throws RepositoryException {
    try {
      if (colName.equals("r_object_id")
          || colName.equals("i_chronicle_id")) {
        colName = "jcr:uuid";
      } else if (colName.equals("r_modify_date_str")) {
        colName = "jcr:lastModified";
        Date modifiedDate =
            currentNode.getProperty(colName).getDate().getTime();
        SimpleDateFormat formatter =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(modifiedDate);
      }

      Property tmp = currentNode.getProperty(colName);
      return tmp.getString();
    } catch (PathNotFoundException e) {
      throw new RepositoryException(e);
    } catch (javax.jcr.RepositoryException e) {
      throw new RepositoryException(e);
    }
  }

  /*
   * protected Value[] getAuthorizedUsers() throws RepositoryException { try {
   * Property tmp = currentNode.getProperty("acl"); return tmp.getValues(); }
   * catch (PathNotFoundException e) { throw new RepositoryException(e); }
   * catch (javax.jcr.RepositoryException e) { throw new
   * RepositoryException(e); } }
   */
  @Override
  public IValue getValue(String attrName) throws RepositoryException {
    Value val = null;
    if (attrName.equals("r_object_id")) {
      attrName = "jcr:uuid";
    } else if (attrName.equals("object_name")) {
      attrName = "name";
    } else if (attrName.equals("r_modify_date")) {
      attrName = "google:lastmodify";
    }

    try {
      val = currentNode.getProperty(attrName).getValue();
    } catch (ValueFormatException e) {
      throw new RepositoryException(e);
    } catch (PathNotFoundException e) {
      throw new RepositoryException(e);
    } catch (javax.jcr.RepositoryException e) {
      throw new RepositoryException(e);
    }
    return new MockDmValue(val);
  }

  @Override
  public void close() throws RepositoryException {
  }

  @Override
  public int getState() {
    return ICollection.DF_READY_STATE;
  }

  @Override
  public ISession getSession(){
    MockDmSessionManager mockDm = new MockDmSessionManager();
    ISession session = null;
    try {
      session = mockDm.getSession(mockDm.getDocbaseName());
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    return session;
  }

  @Override
  public boolean hasNext() throws RepositoryException {
    // TODO Auto-generated method stub
    return collection.hasNext();
  }

  @Override
  public ITime getTime(String colName) throws RepositoryException {
    Calendar val = null;
    if (colName.equals("r_modify_date")) {
      colName = "jcr:lastModified";
    }

    try {
      val = currentNode.getProperty(colName).getDate();
    } catch (ValueFormatException e) {
      throw new RepositoryException(e);
    } catch (PathNotFoundException e) {
      throw new RepositoryException(e);
    } catch (javax.jcr.RepositoryException e) {
      throw new RepositoryException(e);
    }
    return new MockDmTime(val.getTime());
  }

  @Override
  public String getAllRepeatingStrings(String colName, String separator)
      throws RepositoryException {
    throw new UnsupportedOperationException();
  }
}
