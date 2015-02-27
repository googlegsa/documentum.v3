package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IGroup;
import com.google.enterprise.connector.spi.RepositoryDocumentException;

public class MockDmGroup implements IGroup {

  private String name;

  MockDmGroup(String name) {
    this.name = name;
  }

  @Override
  public String getUserSource() throws RepositoryDocumentException {
    return null;
  }
}
