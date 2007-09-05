package com.google.enterprise.connector.dctm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.enterprise.connector.spiimpl.DateValue;

public class DctmDateValue extends DateValue {

	Calendar calendarValue;

	private static Logger logger = null;

	static {
		logger = Logger.getLogger(DctmDateValue.class.getName());
	}

	private static final SimpleDateFormat DCTM_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public DctmDateValue(Calendar calendarValue) {
		super(calendarValue);
		this.calendarValue = calendarValue;
	}

	/**
	 * Formats a calendar object as Dctm format.
	 * 
	 * @param c
	 * @return a String in ISO-8601 format - always in GMT zone
	 */
	public String toDctmFormat() {

		Date d = calendarValue.getTime();
		String dctmFormat = DCTM_DATE_FORMAT.format(d);
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
			logger.log(Level.INFO, "check additional where clause : "
					+ dctmFormat);
		}
		return dctmFormat;
	}
}
