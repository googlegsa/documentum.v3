package com.google.enterprise.connector.dctm.dfcwrap;


public interface ISession{
	
	public ISysObject getObject(IId objectId);
	
	public String getSessionId();

	public String getLoginTicketForUser(String username);

	public String getDocbaseName();



}
