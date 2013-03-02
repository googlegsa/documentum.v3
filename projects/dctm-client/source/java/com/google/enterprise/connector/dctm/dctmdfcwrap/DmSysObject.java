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

package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IAcl;
import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryDocumentException;

public class DmSysObject implements ISysObject {
  IDfSysObject idfSysObject;

  private static Logger logger = Logger.getLogger(DmSysObject.class
      .getName());

  public DmSysObject(IDfSysObject idfSysObject) {
    this.idfSysObject = idfSysObject;
  }

  public String getObjectName() throws RepositoryDocumentException {
    String name = null;
    try {
      name = idfSysObject.getObjectName();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
    return name;
  }

  public IFormat getFormat() throws RepositoryDocumentException {
    IDfFormat idfFormat = null;
    try {
      idfFormat = idfSysObject.getFormat();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
    return new DmFormat(idfFormat);
  }

  public long getContentSize() throws RepositoryDocumentException {
    long contentSize = 0;
    try {
      contentSize = idfSysObject.getContentSize();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
    return contentSize;
  }

  public ByteArrayInputStream getContent() throws RepositoryDocumentException {
    ByteArrayInputStream content = null;
    try {
      content = idfSysObject.getContent();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
    return content;
  }

  public String getACLDomain() throws RepositoryDocumentException {
    try {
      return idfSysObject.getACLDomain();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public String getACLName() throws RepositoryDocumentException {
    try {
      return idfSysObject.getACLName();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public String getString(String name) throws RepositoryDocumentException {
    try {
      if (name.equals("r_object_id") || name.equals("i_chronicle_id")) {
        return idfSysObject.getString(name);
      }
      if (idfSysObject.getAttrDataType(name) == IDfAttr.DM_TIME) {
        return this.getTime(name).getDate().toString();
      } else if (idfSysObject.getAttrDataType(name) == IDfAttr.DM_ID) {
        return this.getId(name).toString();
      }
      return idfSysObject.getString(name);
    } catch (DfException e) {
      // if the attribute name does not exist for the type
      if (e.getMessage().indexOf("DM_API_E_BADATTRNAME") != -1) {
        logger.finest("in the case of DM_API_E_BADATTRNAME");
        return "";
      }
      throw new RepositoryDocumentException(e);
    }
  }

  public boolean getBoolean(String name) throws RepositoryDocumentException {
    try {
      return idfSysObject.getBoolean(name);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public double getDouble(String name) throws RepositoryDocumentException {
    try {
      return idfSysObject.getDouble(name);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public IId getId(String name) throws RepositoryDocumentException {
    try {
      return new DmId(idfSysObject.getId(name));
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public int getInt(String name) throws RepositoryDocumentException {
    try {
      return idfSysObject.getInt(name);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public ITime getTime(String name) throws RepositoryDocumentException {
    try {
      return new DmTime(idfSysObject.getTime(name));
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public IType getType() throws RepositoryDocumentException {
    try {
      IDfType type = idfSysObject.getType();
      return (type == null) ? null : new DmType(type);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public int getAttrDataType(String name) throws RepositoryDocumentException {
    try {
      return idfSysObject.getAttrDataType(name);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public int getAttrCount() throws RepositoryDocumentException {
    try {
      return idfSysObject.getAttrCount();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public IAttr getAttr(int attrIndex) throws RepositoryDocumentException {
    try {
      return new DmAttr(idfSysObject.getAttr(attrIndex));
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public void setSessionManager(ISessionManager sessionManager)
      throws RepositoryDocumentException {
    DmSessionManager dmSessionManager = (DmSessionManager) sessionManager;
    try {
      this.idfSysObject.setSessionManager(dmSessionManager
          .getDfSessionManager());
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public IValue getRepeatingValue(String name, int index)
      throws RepositoryDocumentException {
    try {
      return new DmValue(idfSysObject.getRepeatingValue(name, index));
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public int findAttrIndex(String name) throws RepositoryDocumentException {
    try {
      return idfSysObject.findAttrIndex(name);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public int getValueCount(String name) throws RepositoryDocumentException {
    try {
      return idfSysObject.getValueCount(name);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public IAcl getAcl() throws RepositoryDocumentException {
    try {
      return new DmAcl(idfSysObject.getACL());
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  // TODO(Srinivas): Move this logic to get AclId by using getObjectId to
  // higher level class. getObjectId() belongs to IDfTypedObject, which we 
  // don't have an interface/implementation. Maybe add to IPersistentObject
  public IId getAclId() throws RepositoryDocumentException {
    try {
      return new DmId(idfSysObject.getACL().getObjectId());
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }
}
