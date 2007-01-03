package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.ResultSet;

public interface ICollection{
	public IValue getValue(String attrName);
	public boolean next();
	public ITypedObject getTypedObject();
	public IId getObjectId();
	public String getString(String colName);
	public ResultSet buildResulSetFromCollection(ISession session);
}
