package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.documentum.fc.common.IDfTime;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;

public class MockDmTime implements ITime {
	
	private Date time;
	///private long time;
	
	public MockDmTime(Date time) {
		this.time = time;
	}
	
	public String asString(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate() {
		return time;
		///return new Date(time);
	}
	
	public String getFormattedDate() {
		///return time;
		/*
		SimpleDateFormat myDate=new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z",new Locale("EN"));
		String formattedDate=myDate.format(time);
		System.out.println("formattedDate vaut "+formattedDate);
		return formattedDate;
		*/
		SimpleDateFormat myDate=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",new Locale("EN"));
		String formattedDate=myDate.format(time);
		System.out.println("formattedDate vaut "+formattedDate);
		return formattedDate;
	}

}
