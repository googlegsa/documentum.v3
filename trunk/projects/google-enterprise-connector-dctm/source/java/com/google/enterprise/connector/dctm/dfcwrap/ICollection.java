package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSysObject;

public interface ICollection{
	public IValue getValue(String attrName);
	public boolean next();
	public ITypedObject getTypedObject();
}
