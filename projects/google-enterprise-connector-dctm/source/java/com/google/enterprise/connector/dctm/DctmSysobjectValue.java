package com.google.enterprise.connector.dctm;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Value;
import com.google.enterprise.connector.spi.ValueType;

public class DctmSysobjectValue implements Value {

	ISysObject sysObject = null;
	String name = null;

	public DctmSysobjectValue(ISysObject sysObject, String name) {
		super();
		this.sysObject = sysObject;
		this.name = name;
	}

	public boolean getBoolean() throws IllegalArgumentException,
			RepositoryException {
		return sysObject.getBoolean(name);
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
		// todo: fix the content case
	    throw new UnsupportedOperationException();
	}

	public String getString() throws IllegalArgumentException,
			RepositoryException {
		return sysObject.getString(name);
	}

	public ValueType getType() throws RepositoryException {
		// do we need this?
	    throw new UnsupportedOperationException();
	}

}
