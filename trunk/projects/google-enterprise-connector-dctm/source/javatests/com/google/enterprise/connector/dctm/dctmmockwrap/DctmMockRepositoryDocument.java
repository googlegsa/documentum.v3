package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryDateTime;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONObject;

public class DctmMockRepositoryDocument implements ISysObject, IPersistentObject, ITypedObject{

	private MockRepositoryDocument mrDocument;

	public DctmMockRepositoryDocument(MockRepositoryDocument mrD) {
		mrDocument = mrD;
	}
	
	public DctmMockRepositoryDocument(JSONObject jo) {
		mrDocument = new MockRepositoryDocument(jo);
	}
	
	public ByteArrayInputStream getContent() {
		ByteArrayInputStream is = null;
		try {
			is = (ByteArrayInputStream) mrDocument.getContentStream();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return is;
	}
	
	public IFormat getFormat(){
		return new DctmMockFormat();
	}
	public long getContentSize(){
		return mrDocument.getContent().length();
	}
	public int getPermitEx(String name){
		//6 = read/write access
		return 6;
	}

	public IId getObjectId(){
		return new DctmMockId(mrDocument.getDocID());
	}
}
