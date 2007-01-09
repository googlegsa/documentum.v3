package com.google.enterprise.connector.dctm;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;

public class DctmSimpleValueTest extends TestCase {
	  private static final SimpleDateFormat ISO8601_DATE_FORMAT_SECS =
	      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	  public static final SimpleDateFormat RFC822_DATE_FORMAT =
	      new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss z");
	

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.iso8601ToCalendar(String)'
	 */
	public void testIso8601ToCalendar() {
		String s ="2007-01-04T00:00:00Z";
		
		String waitedString = "Thu Jan 04 00:00:00 CET 2007";
		try {
   	    	
	    	assertEquals(waitedString,ISO8601_DATE_FORMAT_SECS.parse(s).toString());
	    }catch (ParseException e) {
	    	System.out.println(e.getMessage());
	         
	    }
	}

}
