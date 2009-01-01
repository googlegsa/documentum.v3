package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import javax.jcr.ValueFormatException;

import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.mock.MockRepositoryDateTime;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryProperty;
import com.google.enterprise.connector.mock.MockRepositoryPropertyList;
import com.google.enterprise.connector.mock.MockRepositoryProperty.PropertyType;
import com.google.enterprise.connector.mock.jcr.MockJcrValue;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;

public class MockDmObject implements ISysObject {
	private MockRepositoryDocument mockDocument;

	public MockDmObject(MockRepositoryDocument mRD) {
		this.mockDocument = mRD;
	}

	public long getContentSize() throws RepositoryException {
		ByteArrayInputStream contentStream = null;
		int avail = 0;
		try {
			contentStream = (ByteArrayInputStream) mockDocument
					.getContentStream();
			avail = contentStream.available();
		} catch (FileNotFoundException e) {

		}
		return avail;
	}

	public ByteArrayInputStream getContent() throws RepositoryException {
		ByteArrayInputStream contentStream = null;
		try {
			contentStream = (ByteArrayInputStream) mockDocument
					.getContentStream();
		} catch (FileNotFoundException e) {

		}
		return contentStream;
	}

	public String getACLDomain() throws RepositoryException {
		return "ACLDomain";
	}

	public String getACLName() throws RepositoryException {
		return "ACLName";
	}

	public String getString(String name) throws RepositoryException {
		// /faire les remplacements requis entre attributs Mock et attributs
		// Dctm
		String propStrVal = null;
		if (name.equals("object_name")) {
			name = "name";
			MockRepositoryProperty pm = mockDocument.getProplist().getProperty(
					name);
			MockJcrValue propVal = new MockJcrValue(pm);
			try {
				propStrVal = propVal.getString();
			} catch (ValueFormatException e) {

			} catch (IllegalStateException e) {

			} catch (javax.jcr.RepositoryException e) {

			}
		} else if (name.equals(SpiConstants.PROPNAME_DOCID)) {
			name = "docid";
			propStrVal = mockDocument.getDocID();
		} else if (name.equals(SpiConstants.PROPNAME_SECURITYTOKEN)) {
			name = "acl";
			MockRepositoryProperty pm = mockDocument.getProplist().getProperty(
					name);
			MockJcrValue propVal = new MockJcrValue(pm);
			try {
				propStrVal = propVal.getString();
			} catch (ValueFormatException e) {

			} catch (IllegalStateException e) {

			} catch (javax.jcr.RepositoryException e) {

			}
		} else {
			MockRepositoryProperty pm = mockDocument.getProplist().getProperty(
					name);
			MockJcrValue propVal = new MockJcrValue(pm);
			try {
				propStrVal = propVal.getString();
			} catch (ValueFormatException e) {

			} catch (IllegalStateException e) {

			} catch (javax.jcr.RepositoryException e) {

			}
		}

		return propStrVal;
	}

	public int getInt(String name) throws RepositoryException {
		MockRepositoryProperty pm = mockDocument.getProplist()
				.getProperty(name);
		MockJcrValue propVal = new MockJcrValue(pm);
		int propIntVal = 0;
		try {
			propIntVal = (int) propVal.getLong();
		} catch (ValueFormatException e) {

		} catch (IllegalStateException e) {

		} catch (javax.jcr.RepositoryException e) {

		}
		return propIntVal;
	}

	public ITime getTime(String name) throws RepositoryException {
		Date propDateVal = null;
		if (name.equals("r_modify_date")) {
			name = "google:lastmodify";
		}
		MockRepositoryProperty pm = mockDocument.getProplist()
				.getProperty(name);
		long time = 0;
		if (pm == null) {
			MockRepositoryDateTime dateTime = mockDocument.getTimeStamp();
			time = dateTime.getTicks();
			propDateVal = new Date(time);
		} else {
			String propVal = pm.getValue();
			SimpleDateFormat simple = new SimpleDateFormat(
					"EEE, d MMM yyyy HH:mm:ss z", new Locale("EN"));
			ParsePosition parsePosition = new ParsePosition(0);
			propDateVal = simple.parse(propVal, parsePosition);
			time = propDateVal.getTime();
		}

		return new MockDmTime(propDateVal);
	}

	public double getDouble(String name) throws RepositoryException {
		MockRepositoryProperty pm = mockDocument.getProplist()
				.getProperty(name);
		MockJcrValue propVal = new MockJcrValue(pm);
		double propDblVal = 0;
		try {
			propDblVal = propVal.getDouble();
		} catch (ValueFormatException e) {

		} catch (IllegalStateException e) {

		} catch (javax.jcr.RepositoryException e) {

		}
		return propDblVal;
	}

	public boolean getBoolean(String name) throws RepositoryException {
		MockRepositoryProperty pm = mockDocument.getProplist()
				.getProperty(name);
		MockJcrValue propVal = new MockJcrValue(pm);
		boolean propBlVal = true;
		try {
			propBlVal = propVal.getBoolean();
		} catch (ValueFormatException e) {

		} catch (IllegalStateException e) {

		} catch (javax.jcr.RepositoryException e) {

		}
		return propBlVal;
	}

	public IId getId(String id) throws RepositoryException {

		return new MockDmId(this.mockDocument.getDocID());
	}

	public IFormat getFormat() throws RepositoryException {
		// /return new MockDmFormat("text/plain");
		return new MockDmFormat("application/octet-stream");
	}

	public int getAttrDataType(String name) throws RepositoryException {
		MockRepositoryProperty pm = mockDocument.getProplist()
				.getProperty(name);
		MockJcrValue propVal = new MockJcrValue(pm);
		return propVal.getType();
	}

	public int getAttrCount() throws RepositoryException {
		MockRepositoryPropertyList Mockpm = mockDocument.getProplist();
		int counter = 0;
		for (Iterator mockIt = Mockpm.iterator(); mockIt.hasNext();) {
			mockIt.next();
			counter++;
		}
		return counter;
	}

	public IAttr getAttr(int attrIndex) throws RepositoryException {
		MockRepositoryPropertyList Mockpm = mockDocument.getProplist();
		MockRepositoryProperty pm = null;
		int counter = 0;
		for (Iterator mockIt = Mockpm.iterator(); mockIt.hasNext();) {
			pm = (MockRepositoryProperty) mockIt.next();
			if (counter == attrIndex) {
				return new MockDmAttr(pm);
			}
			counter++;
		}
		return null;
	}

	public void setSessionManager(ISessionManager sessionManager)
			throws RepositoryException {

	}

	public IValue getRepeatingValue(String name, int index)
			throws RepositoryException {
		return new MockDmValue(new MockJcrValue(new MockRepositoryProperty(
				name, PropertyType.STRING, getString(name))));
	}

	public int findAttrIndex(String name) throws RepositoryException {

		return 0;
	}

	public int getValueCount(String name) throws RepositoryException {

		return 1;
	}

}
