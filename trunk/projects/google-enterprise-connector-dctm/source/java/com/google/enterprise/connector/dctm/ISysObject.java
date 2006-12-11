package com.google.enterprise.connector.dctm.dfcwrap;

import java.io.ByteArrayInputStream;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmFormat;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmId;

public interface ISysObject{
	public IDctmFormat getFormat();
	public long getContentSize();
	public ByteArrayInputStream getContent();
	public int getPermitEx(String name);
	public IDctmId getObjectId();
}
