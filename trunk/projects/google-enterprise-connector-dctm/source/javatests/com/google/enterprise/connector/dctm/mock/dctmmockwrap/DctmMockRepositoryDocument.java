package com.google.enterprise.connector.dctm.mock.dctmmockwrap;

import com.google.enterprise.connector.dctm.mock.mockwrap.*;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryDateTime;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONObject;

public class DctmMockRepositoryDocument implements IRepositoryDocument{
/**MockAccess**/
	private MockRepositoryDocument mrDocument;
/**MockAccess**/

/**Constructors**/
	public DctmMockRepositoryDocument(IRepositoryDateTime timeStamp, 
			IId docid,
			String content,
			IRepositoryPropertyList proplist) {
		mrDocument = new MockRepositoryDocument(new MockRepositoryDateTime(timeStamp.getTicks()),
				docid.getValue(),
				content,
				proplist.getmrPropertyList());
	}
	
	public DctmMockRepositoryDocument(JSONObject jo) {
		mrDocument = new MockRepositoryDocument(jo);
	}
/**Constructors**/
	
	public String getContent() {
		return mrDocument.getContent();
	}
	
	public InputStream getContentStream() throws FileNotFoundException {
		return mrDocument.getContentStream();
	}
	
	public String getDocID() {
		return mrDocument.getDocID();
	}
	
	public IRepositoryPropertyList getProplist() {
		return new DctmMockRepositoryPropertyList(mrDocument.getProplist());
	}
	
	public IRepositoryDateTime getTimeStamp() {
		return new DctmMockRepositoryDateTime(mrDocument.getTimeStamp().getTicks());
	}
}
