package com.google.enterprise.connector.dctm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

public class DctmSimpleValueTest extends TestCase {

	  private static final TimeZone TIME_ZONE_GMT = TimeZone.getTimeZone("GMT+0");
	  private static final Calendar GMT_CALENDAR =
	      Calendar.getInstance(TIME_ZONE_GMT);
	  private static final SimpleDateFormat ISO8601_DATE_FORMAT_MILLIS =
	      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	  private static final SimpleDateFormat ISO8601_DATE_FORMAT_SECS =
	      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	  private static final SimpleDateFormat ISO8601_DATE_FORMAT =
	      new SimpleDateFormat("yyyy-MM-dd");
	  public static final SimpleDateFormat RFC822_DATE_FORMAT =
	      new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss z");
	public static void main(String[] args) {
	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.DctmSimpleValue(ValueType, String)'
	 */
	public void testDctmSimpleValueValueTypeString() {

	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.DctmSimpleValue(ValueType, byte[])'
	 */
	public void testDctmSimpleValueValueTypeByteArray() {

	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.getBoolean()'
	 */
	public void testGetBoolean() {

	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.getDate()'
	 */
	public void testGetDate() {
	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.getDouble()'
	 */
	public void testGetDouble() {

	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.getLong()'
	 */
	public void testGetLong() {

	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.getStream()'
	 */
	public void testGetStream() {

	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.getString()'
	 */
	public void testGetString() {

	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.getType()'
	 */
	public void testGetType() {

	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.calendarToRfc822(Calendar)'
	 */
	public void testCalendarToRfc822() {

	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.calendarToIso8601(Calendar)'
	 */
	public void testCalendarToIso8601() {

	}

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmSimpleValue.iso8601ToCalendar(String)'
	 */
	public void testIso8601ToCalendar() {
		Date d = null;
		String s ="2006-10-03";
		/*
	    try {
	      System.out.println("ISO8601_DATE_FORMAT_MILLIS");
	      d = ISO8601_DATE_FORMAT_MILLIS.parse(s);
	      assertNotNull(d);
	    }catch (ParseException e) {
	      // this is just here so we can try another format
	    }
	    try {
	    	System.out.println("ISO8601_DATE_FORMAT_SECS");
	    	d = ISO8601_DATE_FORMAT_SECS.parse(s);
	    	assertNotNull(d);
	    }catch (ParseException e) {
	      // this is just here so we can try another format
	    }
	    */
	    try {
	    	System.out.println("ISO8601_DATE_FORMAT");
	    	d = ISO8601_DATE_FORMAT.parse(s);
	    	System.out.println("d vaut "+d);
	    	assertNotNull(d);
	    }catch (ParseException e) {
	    	System.out.println(e.getMessage());
	          // this is just here so we can try another format
	    }
	    System.out.println("d vaut "+d);

	}

}
