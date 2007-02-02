package com.google.enterprise.connector.dctm;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import com.google.enterprise.connector.spi.ResultSet;

public class DctmResultSet extends LinkedList implements ResultSet {

	private static final long serialVersionUID = 9081981L;

	public DctmResultSet() {
		super();
	}

	public DctmResultSet(Collection co) {
		super(co);
	}

	public Iterator iterator() {
		ListIterator iterator = listIterator(0);
		return iterator;
	}


}
