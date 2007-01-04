package com.google.enterprise.connector.dctm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

public class DctmSimpleValueTest extends TestCase {
	  private static final SimpleDateFormat ISO8601_DATE_FORMAT =
	      new SimpleDateFormat("yyyy-MM-dd");
	  public static final SimpleDateFormat RFC822_DATE_FORMAT =
	      new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss z");
	

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.iso8601ToCalendar(String)'
	 */
	public void testIso8601ToCalendar() {
		Date d = null;
		String s ="2007-01-04";
		
		String waitedString = "Thu Jan 04 00:00:00 CET 2007";
		try {
   	    	assertNotNull(d);
	    	assertEquals(waitedString,ISO8601_DATE_FORMAT.parse(s).toString());
	    }catch (ParseException e) {
	    	System.out.println(e.getMessage());
	         
	    }
	}

}
