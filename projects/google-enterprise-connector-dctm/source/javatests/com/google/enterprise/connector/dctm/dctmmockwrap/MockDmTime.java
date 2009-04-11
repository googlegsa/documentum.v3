package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.enterprise.connector.dctm.dfcwrap.ITime;

public class MockDmTime implements ITime {

	private Date time;

	public MockDmTime(Date time) {
		this.time = time;
	}

	public Date getDate() {
		return time;
	}

	public String getFormattedDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'", new Locale("EN"));

		return simpleDateFormat.format(time);
	}
	
	public String asString(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTime_pattern44() {
		// TODO Auto-generated method stub
		return null;
	}

}
