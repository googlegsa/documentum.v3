package com.google.enterprise.connector.dctm;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Value;
import com.google.enterprise.connector.spi.ValueType;

public class DctmSysobjectValue implements Value {

	private static final SimpleDateFormat ISO8601_DATE_FORMAT_MILLIS = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");

	private static final SimpleDateFormat ISO8601_DATE_FORMAT_SECS = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	ISysObject sysObject = null;

	String name = null;

	ValueType type = null;

	private String stringValue;

	public DctmSysobjectValue(ISysObject sysObject, String name) {
		super();
		this.sysObject = sysObject;
		this.name = name;
		this.type = getType();
		this.stringValue = null;
	}

	public DctmSysobjectValue(ISysObject sysObject, String name, ValueType type) {
		super();
		this.sysObject = sysObject;
		this.name = name;
		this.type = type;
		this.stringValue = null;
	}

	public DctmSysobjectValue(ValueType type, String value) {
		super();
		this.sysObject = null;
		this.name = null;
		this.type = type;
		this.stringValue = value;
	}

	public boolean getBoolean() throws IllegalArgumentException,
			RepositoryException {
		if (stringValue != null) {
			return stringValue.equals("true");
		} else {
			return sysObject.getBoolean(name);
		}
	}

	public Calendar getDate() throws IllegalArgumentException,
			RepositoryException {
		ITime time = sysObject.getTime(name);
		Date date = time.getDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public double getDouble() throws IllegalArgumentException,
			RepositoryException {
		return sysObject.getDouble(name);
	}

	public long getLong() throws IllegalArgumentException, RepositoryException {
		return sysObject.getInt(name);
	}

	public InputStream getStream() throws IllegalArgumentException,
			IllegalStateException, RepositoryException {

		InputStream str = null;
		if (sysObject.getContentSize() != 0) {

			str = sysObject.getContent();
		} else {
			str = new ByteArrayInputStream(new byte[1]);
		}
		return str;

	}

	public String getString() throws IllegalArgumentException,
			RepositoryException {
		if (stringValue != null) {
			return stringValue;
		} else {
			String str = sysObject.getString(name);
			if (str != null) {
				return str;
			} else {
				return "";
			}
		}
	}

	public ValueType getType() {

		int dataType;
		try {
			dataType = sysObject.getAttrDataType(name);
		} catch (RepositoryException e) {
			return null;
		}

		if (dataType == IType.STRING) {
			return ValueType.STRING;
		} else if (dataType == IType.BOOLEAN) {
			return ValueType.BOOLEAN;
		} else if (dataType == IType.INT) {
			return ValueType.LONG;
		} else if (dataType == IType.TIME) {
			return ValueType.DATE;
		}
		return null;

	}

	public static String calendarToIso8601(Calendar c) {

		Date d = c.getTime();
		String isoString = ISO8601_DATE_FORMAT_MILLIS.format(d);
		return isoString;

	}

	private static Date iso8601ToDate(String s) throws ParseException {
		Date d = null;
		try {
			d = ISO8601_DATE_FORMAT_MILLIS.parse(s);
			return d;
		} catch (ParseException e) {
			// this is just here so we can try another format
		}
		d = ISO8601_DATE_FORMAT_SECS.parse(s);
		return d;
	}

	public static Calendar iso8601ToCalendar(String s) throws ParseException {
		Date d = iso8601ToDate(s);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c;
	}

}
