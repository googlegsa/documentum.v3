package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.IACL;
import com.google.enterprise.connector.spi.RepositoryDocumentException;

public class DmACL implements IACL {
  IDfACL idfAcl;

  public DmACL(IDfACL idfAcl) {
    this.idfAcl = idfAcl;
  }

  public String getObjectName() throws RepositoryDocumentException {
    try {
      return idfAcl.getObjectName();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public int getAccessorCount() throws RepositoryDocumentException {
    try {
      return idfAcl.getAccessorCount();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public String getAccessorName(int index) throws RepositoryDocumentException {
    try {
      return idfAcl.getAccessorName(index);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public int getAccessorPermit(int index) throws RepositoryDocumentException {
    try {
      return idfAcl.getAccessorPermit(index);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public boolean hasPermission(String permissionName, String accessorName)
      throws RepositoryDocumentException {
    try {
      return idfAcl.hasPermission(permissionName, accessorName);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public boolean isGroup(int index) throws RepositoryDocumentException {
    try {
      return idfAcl.isGroup(index);
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public String getDomain() throws RepositoryDocumentException {
    try {
      return idfAcl.getDomain();
    } catch (DfException e) {
      throw new RepositoryDocumentException(e);
    }
  }
}
