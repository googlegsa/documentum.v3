package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.util.Date;

import com.documentum.fc.common.IDfTime;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;

public class DmTime implements ITime {

	private IDfTime idfTime;
	
	private String time_pattern44 = IDfTime.DF_TIME_PATTERN44;

	public DmTime(IDfTime idfTime) {
		this.idfTime = idfTime;
	}

	public Date getDate() {
		Date date = null;
		date = idfTime.getDate();
		return date;
	}
	
	public String asString(String pattern) {
		String st=null;
		st = idfTime.asString(pattern);
		return st;
	}

	public String getTime_pattern44() {
		return time_pattern44;
	}

}
