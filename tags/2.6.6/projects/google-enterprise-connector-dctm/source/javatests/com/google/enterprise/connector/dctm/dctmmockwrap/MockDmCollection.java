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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.query.QueryResult;

import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryException;

public class MockDmCollection implements ICollection {
  private NodeIterator collection;

  private Node currentNode;

  protected MockDmCollection(QueryResult mjQueryResult)
      throws RepositoryException {
    try {
      collection = mjQueryResult.getNodes();
    } catch (javax.jcr.RepositoryException e) {
      throw new RepositoryException(e);
    }
  }

  public boolean next() {
    if (collection.hasNext()) {
      currentNode = collection.nextNode();
      return true;
    } else {
      currentNode = null;
      return false;
    }
  }

  public String getString(String colName) throws RepositoryException {
    try {
      if (colName.equals("r_object_id")
          || colName.equals("i_chronicle_id")) {
        colName = "jcr:uuid";
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

  public void close() throws RepositoryException {
  }

  public int getState() {
    return ICollection.DF_READY_STATE;
  }

  public ISession getSession(){
      MockDmClient mockDm = new MockDmClient();
      ISession session = null;
      try {
        session = mockDm.getSession(mockDm.getDocbaseName());
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
      return session;
  }

  public boolean hasNext() throws RepositoryException {
    // TODO Auto-generated method stub
    return collection.hasNext();
  }

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
}
