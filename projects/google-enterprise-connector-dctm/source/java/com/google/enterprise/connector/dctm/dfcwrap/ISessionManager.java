package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLoginInfo;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSession;


public interface ISessionManager{
	 IDctmSession getSession(String docbase); 
	 
	 public void setIdentity(String docbase,IDctmLoginInfo identity);
}
