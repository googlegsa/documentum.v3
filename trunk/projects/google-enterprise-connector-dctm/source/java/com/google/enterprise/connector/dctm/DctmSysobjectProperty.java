package com.google.enterprise.connector.dctm;

import java.util.HashSet;
import java.util.Iterator;

import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Value;

public class DctmSysobjectProperty implements Property {

	private String name;

	private Iterator iter;
	
	public DctmSysobjectProperty(String name, HashSet hashSet) {
		this.name = name;
		this.iter = hashSet.iterator();
	}

	public String getName() throws RepositoryException {
		return name;
	}

	public Value nextValue() throws RepositoryException {
		if(iter.hasNext()){
			return (Value)iter.next();
		}
		return null;
	
	}

}
