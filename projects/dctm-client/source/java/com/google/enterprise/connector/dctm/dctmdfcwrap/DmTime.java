package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.util.Date;

import com.documentum.fc.common.IDfTime;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;

public class DmTime implements ITime {

	private IDfTime idfTime;

	public static String DF_TIME_PATTERN45 = IDfTime.DF_TIME_PATTERN45;

	public static String DF_TIME_PATTERN26 = IDfTime.DF_TIME_PATTERN26;

	public DmTime(IDfTime idfTime) {
		this.idfTime = idfTime;
	}

	public Date getDate() {
		Date date = null;
		date = idfTime.getDate();
		return date;
	}

}
