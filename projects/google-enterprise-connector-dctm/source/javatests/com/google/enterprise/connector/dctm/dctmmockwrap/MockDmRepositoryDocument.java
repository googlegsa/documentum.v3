package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.spi.RepositoryException;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;


import org.json.JSONObject;

public class MockDmRepositoryDocument implements ISysObject, IPersistentObject, ITypedObject{

	private MockRepositoryDocument mrDocument;

	public MockDmRepositoryDocument(MockRepositoryDocument mrD) {
		mrDocument = mrD;
	}
	
	public MockDmRepositoryDocument(JSONObject jo) {
		mrDocument = new MockRepositoryDocument(jo);
	}
	
	public MockDmRepositoryDocument() {
		mrDocument = null;
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
	
	public long getContentSize(){
		return mrDocument.getContent().length();
	}

	/**
	 * @author jpasquon
	 * Utilized to fill the SpiConstants.PROPNAME_SECURITYTOKEN SimpleValue.
	 * Is utilized by the gsa to deal with authorizations. Let's wait for the update of
	 * the way ResultSet is built to implement it.
	 */
	public String getACLDomain() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @author jpasquon
	 * Utilized to fill the SpiConstants.PROPNAME_SECURITYTOKEN SimpleValue.
	 * Is utilized by the gsa to deal with authorizations. Let's wait for the update of
	 * the way ResultSet is built to implement it.
	 */
	public String getACLName() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @author jpasquon
	 * Retrieves an attribute by its name. For DFC, this is directly applied to the SysObj.
	 * For Mock though, we first need to retrieve its property list either by attaching it (through an initilization)
	 * to the MockDmRepositoryDocument or by retrieving the list each time it's needed.
	 * That decision will be taken according to how many times an attribute is to be retrieved (memory load/speed rate balancing).
	 */
	public String getString(String name) throws RepositoryException {
		
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see getString(String name)
	 */
	public int getInt(String name) throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see getString(String name)
	 */
	public ITime getTime(String name) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see getString(String name)
	 */
	public double getDouble(String name) throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see getString(String name)
	 */
	public boolean getBoolean(String name) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}

	public IId getId(String name) throws RepositoryException {
		return new MockDmId(mrDocument.getDocID());
	}

	/**
	 * @author jpasquon
	 * Let's wait to see if we implement an exhaustive description of attributes 
	 * to get (with custom attr = enumeratedAttrs - regularSysObjAttrs).
	 * or "attributes' rank" to discriminate custom attributes from regular ones.
	 */
	public Enumeration enumAttrs() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @author jpasquon
	 * For Mock, maybe we should return a constant.
	 */
	public IFormat getFormat() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
