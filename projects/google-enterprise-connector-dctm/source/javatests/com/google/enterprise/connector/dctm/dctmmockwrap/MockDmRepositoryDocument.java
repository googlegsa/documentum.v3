package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.mock.MockRepositoryDateTime;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryProperty;
import com.google.enterprise.connector.spi.RepositoryException;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Iterator;

import org.json.JSONObject;

public class MockDmRepositoryDocument implements ISysObject, IPersistentObject,
		ITypedObject {

	private MockRepositoryDocument mrDocument;

	protected MockDmRepositoryDocument(MockRepositoryDocument mrD) {
		mrDocument = mrD;
	}

	public MockDmRepositoryDocument(JSONObject jo) {
		mrDocument = new MockRepositoryDocument(jo);
	}

	public ByteArrayInputStream getContent() throws RepositoryException {
		ByteArrayInputStream is = null;
		try {
			is = (ByteArrayInputStream) mrDocument.getContentStream();
		} catch (FileNotFoundException e) {
			throw new RepositoryException(e);
		}
		return is;
	}

	public long getContentSize() {
		return mrDocument.getContent().length();
	}

	/**
	 * @author jpasquon Utilized to fill the SpiConstants.PROPNAME_SECURITYTOKEN
	 *         SimpleValue. Is utilized by the gsa to deal with authorizations.
	 *         Let's wait for the update of the way ResultSet is built to
	 *         implement it.
	 */
	public String getACLDomain() throws RepositoryException {
		// TODO Auto-generated method stub
		return "";
	}

	/**
	 * @author jpasquon Utilized to fill the SpiConstants.PROPNAME_SECURITYTOKEN
	 *         SimpleValue. Is utilized by the gsa to deal with authorizations.
	 *         Let's wait for the update of the way ResultSet is built to
	 *         implement it.
	 */
	public String getACLName() throws RepositoryException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getString(String name) throws RepositoryException {
		return mrDocument.getProplist().getProperty(name).getValue();
	}

	/**
	 * @see getString(String name)
	 */
	public int getInt(String name) throws RepositoryException {
		MockRepositoryProperty prop = mrDocument.getProplist()
				.getProperty(name);
		if (prop.getType() == MockRepositoryProperty.PropertyType.INTEGER) {
			return Integer.parseInt(prop.getValue());
		}
		throw new RepositoryException(
				"com.google.enterprise.connector.mock.MockRepositoryProperty.getType() != MockRepositoryProperty.PropertyType.INTEGER => MockDmRepositoryDocument.getInt("
						+ name
						+ ") is not able to return a well formated value.");
	}

	/**
	 * @see getString(String name)
	 */
	public ITime getTime(String name) throws RepositoryException {
		MockRepositoryProperty prop = mrDocument.getProplist()
				.getProperty(name);
		if (prop.getType() == MockRepositoryProperty.PropertyType.DATE) {
			int tmpINT = Integer.parseInt(prop.getValue());
			return new MockDmRepositoryDateTime(new MockRepositoryDateTime(tmpINT));
		}
		throw new RepositoryException(
				"com.google.enterprise.connector.mock.MockRepositoryProperty.getType() != MockRepositoryProperty.PropertyType.DATE => MockDmRepositoryDocument.getDate("
						+ name
						+ ") is not able to return a well formated value.");
	}

	/**
	 * @see getString(String name)
	 */
	public double getDouble(String name) throws RepositoryException {
	    Double d = Double.valueOf(mrDocument.getProplist().getProperty(name).getValue());
	    return d.doubleValue();
	}

	/**
	 * @see getString(String name)
	 */
	public boolean getBoolean(String name) throws RepositoryException {
		String val = mrDocument.getProplist().getProperty(name).getValue();
		if (val.equals("1") || val.equals("true")) {
			return true;
		}
		else if (val.equals("0") || val.equals("false")) {
			return false;
		} else {
			throw new RepositoryException(
					"MockDmRepositoryDocument.getBoolean("
							+ name
							+ ") : not able to identify any boolean value in it.");
		}
	}

	public IId getId(String name) throws RepositoryException {
		return new MockDmId(mrDocument.getDocID());
	}

	public Enumeration enumAttrs() throws RepositoryException {
		return new EnumeratedIterator(mrDocument.getProplist().iterator());
	}
	
	public class EnumeratedIterator implements Enumeration {
		
		Iterator linkedIterator;
		
		protected EnumeratedIterator(Iterator iterator) {
			this.linkedIterator = iterator;
		}

		public boolean hasMoreElements() {
			return linkedIterator.hasNext();
		}

		public Object nextElement() {
			return linkedIterator.next();
		}
		
	}

	/**
	 * CONSTANT format
	 */
	public IFormat getFormat() throws RepositoryException {
		return new MockDmFormat();
	}

}
