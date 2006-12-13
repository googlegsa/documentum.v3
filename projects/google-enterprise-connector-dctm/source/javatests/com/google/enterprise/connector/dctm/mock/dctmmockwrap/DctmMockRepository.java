package com.google.enterprise.connector.dctm.mock.dctmmockwrap;


import com.google.enterprise.connector.dctm.mock.mockwrap.*;
import com.google.enterprise.connector.mock.MockRepository;
import com.google.enterprise.connector.mock.MockRepositoryDateTime;

public class DctmMockRepository implements IRepository {
/**MockAccess**/
	private MockRepository mr;
/**MockAccess**/
	
	
/**Constructors**/
	public DctmMockRepository(IRepositoryEventList iEventList,
			IRepositoryDateTime iTime) {		
		mr = new MockRepository(iEventList.getmrEventList(),new MockRepositoryDateTime(iTime.getTicks()));
	}	
	public DctmMockRepository(IRepositoryEventList iEventList) {
		mr = new MockRepository(iEventList.getmrEventList());
	}
/**Constructors**/	
	
	public void reinit() {
		mr.reinit();
	}
	
	public void setTime(IRepositoryDateTime newTime) {
		mr.setTime(new MockRepositoryDateTime(newTime.getTicks()));
	}
	
	public IRepositoryDateTime getCurrentTime() {
		return new DctmMockRepositoryDateTime(mr.getCurrentTime().getTicks());
	}
	
	public IRepositoryDocumentStore getStore() {
		return new DctmMockRepositoryDocumentStore(mr.getStore());
	}
}