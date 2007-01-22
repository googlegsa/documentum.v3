package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.spi.RepositoryException;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;


import org.json.JSONObject;

public class DctmMockRepositoryDocument implements ISysObject, IPersistentObject, ITypedObject{

	private MockRepositoryDocument mrDocument;

	public DctmMockRepositoryDocument(MockRepositoryDocument mrD) {
		mrDocument = mrD;
	}
	
	public DctmMockRepositoryDocument(JSONObject jo) {
		mrDocument = new MockRepositoryDocument(jo);
	}
	
	public ByteArrayInputStream getContent() throws RepositoryException {
		ByteArrayInputStream is = null;
		try {
			is = (ByteArrayInputStream) mrDocument.getContentStream();
		} catch (FileNotFoundException e) {
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
		return is;
	}
	
	public long getContentSize(){
		return mrDocument.getContent().length();
	}
	public int getPermitEx(String name){
		//6 = read/write access
		return 6;
	}

	public boolean findString(String value) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getACLDomain() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getACLName() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(String name) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getInt(String name) throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public ITime getTime(String name) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public double getDouble(String name) throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getBoolean(String name) throws RepositoryException {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getTitle() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
