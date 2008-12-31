package com.google.enterprise.connector.dctm.dfcwrap;

import java.util.Date;

public interface ITime {

	public Date getDate();
	
	public String asString(String pattern);

	public String getTime_pattern44();
}
