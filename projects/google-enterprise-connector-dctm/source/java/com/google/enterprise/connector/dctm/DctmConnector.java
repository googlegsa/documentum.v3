package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.Session;

public class DctmConnector implements Connector{
	public Session login() throws LoginException{
		 Session sess=new DctmSession();
		 return sess;
	 }
}
