package com.google.enterprise.connector.dctm.dfcwrap;

import java.io.ByteArrayInputStream;

public interface ISysObject{
	public IFormat getFormat();
	public long getContentSize();
	public ByteArrayInputStream getContent();
	public int getPermitEx(String name);
	public IId getObjectId();
}
