package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.spi.SpiConstants;

public class DctmMockFormat implements IFormat{
	private String mimeType;
	
	public DctmMockFormat() {
		mimeType = SpiConstants.DEFAULT_MIMETYPE;
	}
	
	public boolean canIndex(){
		return true;
	}
	
	public String getMIMEType(){
		return mimeType;
	}
}
