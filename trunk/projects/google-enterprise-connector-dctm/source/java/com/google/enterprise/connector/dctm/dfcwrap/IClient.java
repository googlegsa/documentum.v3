package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.LoginException;

public interface IClient{
	
	
	public ILocalClient getLocalClientEx();

	
	public IQuery getQuery();
	
	public ILoginInfo getLoginInfo();

	public ISession newSession(String string, ILoginInfo logInfo);

	public void authenticate(String docbaseName, ILoginInfo loginInfo) throws LoginException;


	public IId getId(String value);

	public ISession getSession();
	
	public void setSession(ISession session);

		
	
}
