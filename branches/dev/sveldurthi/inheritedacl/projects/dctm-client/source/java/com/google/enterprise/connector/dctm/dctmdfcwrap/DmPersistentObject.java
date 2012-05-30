package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfPersistentObject;
import com.google.enterprise.connector.dctm.dfcwrap.IPersistentObject;

public class DmPersistentObject implements IPersistentObject {

  IDfPersistentObject idfPersistentObject;
  
  public DmPersistentObject(IDfPersistentObject idfPersistentObject) {
    this.idfPersistentObject = idfPersistentObject;
  }

}
