package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.util.Date;

import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;

public class IDctmTime implements ITime{
IDfTime dfTime;
public static String DF_TIME_PATTERN45 = IDfTime.DF_TIME_PATTERN45;
	public IDctmTime(IDfTime dfTime){
		this.dfTime=dfTime;
	}
	
	public String asString(String pattern){
		String time=null;
		time=dfTime.asString(pattern);
		return time;
	}
	
	public Date getDate(){
		Date date=null;
		date=dfTime.getDate();
		return date;
	}
	
}
