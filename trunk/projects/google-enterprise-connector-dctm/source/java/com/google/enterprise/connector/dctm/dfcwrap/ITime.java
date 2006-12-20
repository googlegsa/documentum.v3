package com.google.enterprise.connector.dctm.dfcwrap;

import java.util.Date;

public interface ITime {
	public String asString(String pattern);
	public Date getDate();
}
