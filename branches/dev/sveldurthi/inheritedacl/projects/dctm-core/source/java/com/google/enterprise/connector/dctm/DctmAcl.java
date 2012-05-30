package com.google.enterprise.connector.dctm;

import java.util.logging.Logger;

import com.google.enterprise.connector.dctm.dfcwrap.IACL;
import com.google.enterprise.connector.spi.RepositoryDocumentException;

public class DctmAcl implements IACL {
  private static final Logger logger = Logger
      .getLogger(DctmSysobjectDocument.class.getName());

  private IACL iAcl;

  public DctmAcl(IACL iAcl) {
    this.iAcl = iAcl;
  }

  public String getObjectName() throws RepositoryDocumentException {
    try {
      return iAcl.getObjectName();
    } catch (RepositoryDocumentException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public int getAccessorCount() throws RepositoryDocumentException {
    try {
      return iAcl.getAccessorCount();
    } catch (RepositoryDocumentException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public String getAccessorName(int index) throws RepositoryDocumentException {
    try {
      return iAcl.getAccessorName(index);
    } catch (RepositoryDocumentException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public int getAccessorPermit(int index) throws RepositoryDocumentException {
    try {
      return iAcl.getAccessorPermit(index);
    } catch (RepositoryDocumentException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public boolean hasPermission(String permissionName, String accessorName)
      throws RepositoryDocumentException {
    try {
      return iAcl.hasPermission(permissionName, accessorName);
    } catch (RepositoryDocumentException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public boolean isGroup(int index) throws RepositoryDocumentException {
    try {
      return iAcl.isGroup(index);
    } catch (RepositoryDocumentException e) {
      throw new RepositoryDocumentException(e);
    }
  }

  public String getDomain() throws RepositoryDocumentException {
    try {
      return iAcl.getDomain();
    } catch (RepositoryDocumentException e) {
      throw new RepositoryDocumentException(e);
    }
  }
}
