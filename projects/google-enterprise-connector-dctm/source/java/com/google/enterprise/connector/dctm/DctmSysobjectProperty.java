package com.google.enterprise.connector.dctm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Value;

public class DctmSysobjectProperty implements Property {

	private String name;

	private DctmSysobjectValue dctmSysobjectValue;

	public DctmSysobjectProperty(String name,
			DctmSysobjectValue dctmSysobjectValue) {
		this.name = name;
		this.dctmSysobjectValue = dctmSysobjectValue;
	}

	public DctmSysobjectProperty(String name) {
		this.name = name;
	}

	public String getName() throws RepositoryException {
		return name;
	}

	public Value getValue() throws RepositoryException {
		return dctmSysobjectValue;
	}

	public Iterator getValues() throws RepositoryException {
		List l = new ArrayList(1);
		l.add(dctmSysobjectValue);
		return l.iterator();
	}

}
