package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.util.Hashtable;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.naming.AuthenticationException;

import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.mock.MockRepository;
import com.google.enterprise.connector.mock.MockRepositoryEventList;
import com.google.enterprise.connector.mock.jcr.MockJcrRepository;
import com.google.enterprise.connector.mock.jcr.MockJcrSession;
import com.google.enterprise.connector.spi.LoginException;

public class DctmMockSessionManager implements ISessionManager {
	DctmMockSession mSession;
	Hashtable mCreds;
	
	public DctmMockSessionManager(){
		mSession=null;
		mCreds=new Hashtable(1,1);
	}
	
	public ISession getSession(String docbase){
		//maybe check credentials?
		return mSession; 
	}
	
	/**
	 * Calling newSession assumes setIdentity has previously been called.
	 */
	public ISession newSession(String docbase){
		if (mCreds.containsKey(docbase)){
				try {
					return createAuthenticatedSession(docbase ,(ILoginInfo) mCreds.get(docbase));
				} catch (LoginException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
		}
		else return null;
	}

	/**
	 * This method only stores credentials.
	 * Authentication is performed later, through a newSession(docbase) call.
	 */
	public void setIdentity(String docbase,ILoginInfo identity){
		mCreds.put(docbase, identity);
	}
	
	/**
	 * Authenticates the same way the SpiRepositoryFromJcr connector does.
	 * @param db
	 * @param iLI
	 * @return
	 * @throws LoginException
	 */
	private ISession createAuthenticatedSession(String db, ILoginInfo iLI) throws LoginException{
		MockRepositoryEventList mrel =
	        new MockRepositoryEventList(db);
		
	    MockJcrRepository repo = 
	    	new MockJcrRepository(new MockRepository(mrel));
	    
	    Credentials creds = new SimpleCredentials(iLI.getUser(), iLI.getPassword().toCharArray());
	    
	    MockJcrSession sess = null;
	    try {
			sess = (MockJcrSession) repo.login(creds);
		} catch (javax.jcr.LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new DctmMockSession(repo,sess);
		
	}
}
