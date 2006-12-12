package com.google.enterprise.connector.dctm.mock.dctmmockwrap;


import com.google.enterprise.connector.dctm.DctmResultSet;
import com.google.enterprise.connector.dctm.mock.mockwrap.*;
import com.google.enterprise.connector.mock.*;

import java.util.List;
import java.util.ListIterator;

public class DctmMockRepository implements IRepository {
	private MockRepository mr;
	
	public DctmMockRepository(IRepositoryEventList iEventList,
			IRepositoryDateTime iTime) {
		
		MockRepositoryEventList mrEL=new MockRepositoryEventList();
		MockRepositoryDateTime mrDT=new MockRepositoryDateTime(iTime.getTicks());
		
		mrEL.setRepositoryFileName(iEventList.getRepFileName());
		mrEL.setWorkDirName(iEventList.getWorkDirName());
//		Initializes private variable eventList with what's in the file defined by the two other private variables (set above).
		mrEL.getEventList();
		
		mr = new MockRepository(mrEL,mrDT);
	}

	public DctmMockRepository(IRepositoryEventList iEventList) {
		MockRepositoryEventList mrEL=new MockRepositoryEventList();
		mrEL.setRepositoryFileName(iEventList.getRepFileName());
		mrEL.setWorkDirName(iEventList.getWorkDirName());
//		Initializes private variable eventList with what's in the file defined by the two other private variables (set above).
		mrEL.getEventList();
		
		mr = new MockRepository(mrEL);
	}
	
	public void reinit() {
		mr.reinit();
	}
	
	public void setTime(IRepositoryDateTime newTime) {
		mr.setTime(new MockRepositoryDateTime(newTime.getTicks()));
	}
	
	public IRepositoryDateTime getCurrentTime() {
		return new DctmMockRepositoryDateTime(mr.getCurrentTime().getTicks());
	}
	
	/*public IRepositoryDocumentStore getStore() {
		return mr.getStore();
	}*/
}