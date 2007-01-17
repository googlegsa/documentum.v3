package com.google.enterprise.connector.dctm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SimpleProperty;
import com.google.enterprise.connector.spi.Value;


public class DctmSimpleProperty extends SimpleProperty{

	  public DctmSimpleProperty(String name, Value value) {
		    super(name,value);
		  }

		  public DctmSimpleProperty(String name, List valueList) {
			  super(name,valueList);
		  }

		  public DctmSimpleProperty(String name, String value) {
			  super(name,value);
		  }

		  public DctmSimpleProperty(String name, boolean value) {
			  super(name,value);
		  }

		  public DctmSimpleProperty(String name, long value) {
			  super(name,value);
		  }

		  public DctmSimpleProperty(String name, double value) {
			  super(name,value);
		  }
	
		 /* (non-Javadoc)
		   * 
		   * @see com.google.enterprise.connector.spi.Property#getValues()
		   */
		  public Iterator getValues() throws RepositoryException {
		   
		    List l = new ArrayList(1);
		    l.add(super.getValue());
		    return l.iterator();
		  }
}
