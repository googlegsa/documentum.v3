package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmValue;

public interface ICollection {
	public IDctmValue getValue(String attrName);
}
