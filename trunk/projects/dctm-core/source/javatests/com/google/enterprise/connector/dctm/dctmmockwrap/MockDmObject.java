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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.google.enterprise.connector.dctm.dfcwrap.IAcl;
import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.mock.MockRepositoryDateTime;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryProperty;
import com.google.enterprise.connector.mock.MockRepositoryPropertyList;
import com.google.enterprise.connector.mock.MockRepositoryProperty.PropertyType;
import com.google.enterprise.connector.mock.jcr.MockJcrValue;
import com.google.enterprise.connector.spi.RepositoryDocumentException;
import com.google.enterprise.connector.spi.SpiConstants;

public class MockDmObject implements ISysObject {
  private MockRepositoryDocument mockDocument;

  public MockDmObject(MockRepositoryDocument mRD) {
    this.mockDocument = mRD;
  }

  public String getObjectName() throws RepositoryDocumentException {
    return this.mockDocument.getDocID();
  }

  public long getContentSize() throws RepositoryDocumentException {
    ByteArrayInputStream contentStream = null;
    int avail = 0;
    try {
      contentStream = (ByteArrayInputStream) mockDocument.getContentStream();
      avail = contentStream.available();
    } catch (FileNotFoundException e) {
      // TODO: Why is this exception ignored?
    }
    return avail;
  }

  public ByteArrayInputStream getContent() throws RepositoryDocumentException {
    ByteArrayInputStream contentStream = null;
    try {
      contentStream = (ByteArrayInputStream) mockDocument.getContentStream();
    } catch (FileNotFoundException e) {
      // TODO: Why is this exception ignored?
    }
    return contentStream;
  }

  public String getACLDomain() throws RepositoryDocumentException {
    return "ACLDomain";
  }

  public String getACLName() throws RepositoryDocumentException {
    return "ACLName";
  }

  public String getString(String name) throws RepositoryDocumentException {
    // /faire les remplacements requis entre attributs Mock et attributs
    // Dctm
    String propStrVal = null;
    if (name.equals("object_name")) {
      name = "name";
      MockRepositoryProperty pm = mockDocument.getProplist().getProperty(
          name);
      MockJcrValue propVal = new MockJcrValue(pm);
      try {
        propStrVal = propVal.getString();
      } catch (IllegalStateException e) {
        // TODO: Why is this exception ignored?
      }
    } else if (name.equals(SpiConstants.PROPNAME_DOCID)) {
      name = "docid";
      propStrVal = mockDocument.getDocID();
    } else {
      MockRepositoryProperty pm = mockDocument.getProplist().getProperty(
          name);
      MockJcrValue propVal = new MockJcrValue(pm);
      try {
        propStrVal = propVal.getString();
      } catch (IllegalStateException e) {
        // TODO: Why is this exception ignored?
      }
    }

    return propStrVal;
  }

  public int getInt(String name) throws RepositoryDocumentException {
    MockRepositoryProperty pm = mockDocument.getProplist().getProperty(name);
    MockJcrValue propVal = new MockJcrValue(pm);
    int propIntVal = 0;
    try {
      propIntVal = (int) propVal.getLong();
    } catch (IllegalStateException e) {
      // TODO: Why is this exception ignored?
    }
    return propIntVal;
  }

  public ITime getTime(String name) throws RepositoryDocumentException {
    Date propDateVal = null;
    if (name.equals("r_modify_date")) {
      name = "google:lastmodify";
    }
    MockRepositoryProperty pm = mockDocument.getProplist().getProperty(name);
    long time = 0;
    if (pm == null) {
      MockRepositoryDateTime dateTime = mockDocument.getTimeStamp();
      time = dateTime.getTicks();
      propDateVal = new Date(time);
    } else {
      String propVal = pm.getValue();
      SimpleDateFormat simple = new SimpleDateFormat(
          "EEE, d MMM yyyy HH:mm:ss z", new Locale("EN"));
      ParsePosition parsePosition = new ParsePosition(0);
      propDateVal = simple.parse(propVal, parsePosition);
      time = propDateVal.getTime();
    }

    return new MockDmTime(propDateVal);
  }

  public IType getType() throws RepositoryDocumentException {
    String propType = "MockType";
    MockRepositoryProperty pm = mockDocument.getProplist()
        .getProperty("r_object_type");
    if (pm != null) {
      MockJcrValue propVal = new MockJcrValue(pm);
      try {
        propType = propVal.getString();
      } catch (IllegalStateException e) {
        // TODO: Why is this exception ignored?
      }
    }
    return new MockDmType(propType, this);
  }

  public double getDouble(String name) throws RepositoryDocumentException {
    MockRepositoryProperty pm = mockDocument.getProplist().getProperty(name);
    MockJcrValue propVal = new MockJcrValue(pm);
    double propDblVal = 0;
    try {
      propDblVal = propVal.getDouble();
    } catch (IllegalStateException e) {
      // TODO: Why is this exception ignored?
    }
    return propDblVal;
  }

  public boolean getBoolean(String name) throws RepositoryDocumentException {
    MockRepositoryProperty pm = mockDocument.getProplist().getProperty(name);
    MockJcrValue propVal = new MockJcrValue(pm);
    boolean propBlVal = true;
    try {
      propBlVal = propVal.getBoolean();
    } catch (IllegalStateException e) {
      // TODO: Why is this exception ignored?
    }
    return propBlVal;
  }

  public IId getId(String id) throws RepositoryDocumentException {
    return new MockDmId(this.mockDocument.getDocID());
  }

  public IFormat getFormat() throws RepositoryDocumentException {
    // /return new MockDmFormat("text/plain");
    return new MockDmFormat("application/octet-stream");
  }

  public int getAttrDataType(String name) throws RepositoryDocumentException {
    MockRepositoryProperty pm = mockDocument.getProplist().getProperty(name);
    MockJcrValue propVal = new MockJcrValue(pm);
    return propVal.getType();
  }

  public int getAttrCount() throws RepositoryDocumentException {
    int counter = 0;
    for (MockRepositoryProperty pm : mockDocument.getProplist()) {
      counter++;
    }
    return counter;
  }

  public IAttr getAttr(int attrIndex) throws RepositoryDocumentException {
    int counter = 0;
    for (MockRepositoryProperty pm : mockDocument.getProplist()) {
      if (counter == attrIndex) {
        return new MockDmAttr(pm);
      }
      counter++;
    }
    return null;
  }

  public void setSessionManager(ISessionManager sessionManager)
      throws RepositoryDocumentException {
  }

  public IValue getRepeatingValue(String name, int index)
      throws RepositoryDocumentException {
    return new MockDmValue(new MockJcrValue(new MockRepositoryProperty(
        name, PropertyType.STRING, getString(name))));
  }

  public int findAttrIndex(String name) throws RepositoryDocumentException {
    return 0;
  }

  public int getValueCount(String name) throws RepositoryDocumentException {
    return 1;
  }

  @Override
  public IAcl getAcl() throws RepositoryDocumentException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IId getAclId() throws RepositoryDocumentException {
    // TODO Auto-generated method stub
    return null;
  }
}
