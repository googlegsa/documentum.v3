package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.util.Hashtable;

import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;

public class DctmMockSessionManager implements ISessionManager {
	DctmMockSession mSession;
	Hashtable mCreds;
	
	public DctmMockSessionManager(){
		mSession=null;
		mCreds=null;
	}
	
	public ISession getSession(String docbase){
		//maybe check credentials?
		return mSession; 
	}
	
	public ISession newSession(String docbase){
		if (mCreds.containsKey(docbase)){
			mSession=new DctmMockSession();
			return mSession;
		}
		else return null;
	}
	
	public void setIdentity(String docbase,ILoginInfo identity){
		mCreds.put(docbase, identity);
	}
}
