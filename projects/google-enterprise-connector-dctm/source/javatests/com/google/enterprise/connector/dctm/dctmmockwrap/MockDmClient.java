package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.util.HashMap;
import java.util.Iterator;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.mock.MockRepository;
import com.google.enterprise.connector.mock.MockRepositoryEventList;

import com.google.enterprise.connector.mock.jcr.MockJcrRepository;
import com.google.enterprise.connector.mock.jcr.MockJcrSession;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;

//Implements four interfaces to simulate the session pool.
//Does not manage multiple sessions for the same docbase (for the moment) 
public class MockDmClient implements IClientX, IClient, ISessionManager {

	private MockDmSession currentSession;

	private HashMap sessMgerCreds = new HashMap(1, 1);

	private HashMap sessMgerSessions = new HashMap(1, 1);

	// //////////////////////////////////////////////////
	/** ******************Public use******************* */
	// //////////////////////////////////////////////////
	public MockDmClient() {
	}

	public ISessionManager newSessionManager() {
		return this;
	}

	/**
	 * SessionManager's method - do not set the identified session as current
	 * This method only stores credentials. Authentication is performed later,
	 * through a newSession(docbase) call.
	 */
	public void setIdentity(String docbase, ILoginInfo identity) {
		if (!sessMgerCreds.containsKey(docbase))
			sessMgerCreds.put(docbase, identity);
		else {
			sessMgerCreds.remove(docbase);
			sessMgerCreds.put(docbase, identity);
		}
	}

	public IClient getLocalClient() {
		return this;
	}

	/**
	 * Factory method for an IDfLoginInfo object. Constructs a new empty object
	 * to set with login details prior to connecting to Documentum servers.
	 */
	public ILoginInfo getLoginInfo() {
		return new MockDmLoginInfo();
	}

	public void setSessionManager(ISessionManager session) {
		// IClientX and ISessionManager are both
		// implemented by the MockDmClient then nothing to do.
	}

	public ISession newSession(String docbase)
			throws com.google.enterprise.connector.spi.RepositoryException {
		if (sessMgerCreds.containsKey(docbase)) {
			currentSession = createAuthenticatedSession(docbase,
					(ILoginInfo) sessMgerCreds.get(docbase));
			if (!sessMgerSessions.containsKey(docbase))
				sessMgerSessions.put(docbase, currentSession);
			else {
				sessMgerSessions.remove(docbase);
				sessMgerSessions.put(docbase, currentSession);
			}
			return currentSession;
		} else
			throw new LoginException("newSession(" + docbase
					+ ") called for docbase " + docbase
					+ " without setting any credentials prior to this call");
	}

	public void release(ISession session) {
		// No need to do anything regarding Mock sessions.
	}

	public ISessionManager getSessionManager() {
		return this;
	}

	public void clearIdentity(String docbase) {
		sessMgerCreds.remove(docbase);
	}

	public boolean authenticate(String docbaseName) {
		MockDmSession tmp;
		try {
			tmp = createAuthenticatedSession(docbaseName,
					(ILoginInfo) sessMgerCreds.get(docbaseName));
		} catch (RepositoryException e) {
			return false;
		}
		if (tmp == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Factory method for an IDfQuery object. Constructs an new query object to
	 * use for sending DQL queries to Documentum servers.
	 */
	public IQuery getQuery() {
		return new MockDmQuery();
	}

	/**
	 * Session Manager's method. Sets current session as well
	 * 
	 * @throws com.google.enterprise.connector.spi.RepositoryException
	 */
	public ISession getSession(String docbase)
			throws com.google.enterprise.connector.spi.RepositoryException {
		if (!sessMgerSessions.containsKey(docbase))
			return this.newSession(docbase);// DFC javadoc. If session not
		// existing, created.
		else {
			return (ISession) sessMgerSessions.get(docbase);
		}
	}

	public String getDocbaseName() {
		Iterator e = sessMgerSessions.keySet().iterator();
		while (e.hasNext()) {
			Object n = e.next();
			if (sessMgerSessions.get(n).equals(currentSession)) {
				return (String) n;
			}
		}
		return null;
	}

	/**
	 * This method sets current session's docbase name. Substitute IDs based
	 * session management
	 */
	public void setDocbaseName(String docbaseName) {
		currentSession = (MockDmSession) sessMgerSessions
				.get((Object) docbaseName);
	}

	public void setServerUrl(String serverUrl) {
		// Wait for Google to ask me to do it
	}
	
	public ILoginInfo getIdentity(String docbase) {
		return (ILoginInfo) sessMgerCreds.get(docbase);
	}

	// //////////////////////////////////////////////////
	/** *****************Internal use****************** */
	// //////////////////////////////////////////////////
	/**
	 * Authenticates the same way the SpiRepositoryFromJcr connector does.
	 * private then we manage sessions synchronisation within the class that
	 * called this method, not here.
	 * 
	 * @param db
	 * @param iLI
	 * @return
	 * @throws com.google.enterprise.connector.spi.RepositoryException
	 */
	private MockDmSession createAuthenticatedSession(String db, ILoginInfo iLI)
			throws RepositoryException, LoginException {
		// db is actually the suffix of the filename that is used to create the
		// eventlist.
		// As there is no way we can retrieve it, we will store it in the
		// MockDmSession we create.
		MockRepositoryEventList mrel = new MockRepositoryEventList(db);

		MockJcrRepository repo = new MockJcrRepository(new MockRepository(mrel));
		Credentials creds = null;
		if (iLI != null) {
			creds = new SimpleCredentials(iLI.getUser(), iLI.getPassword()
					.toCharArray());
		} else {
			throw new LoginException("No credentials defined for " + db);
		}

		MockJcrSession sess = null;
		try {
			sess = (MockJcrSession) repo.login(creds);
		} catch (javax.jcr.LoginException e) {
			throw new LoginException(e);
		} catch (javax.jcr.RepositoryException e) {
			throw new com.google.enterprise.connector.spi.RepositoryException(e);
		}
		return new MockDmSession(repo, sess, db);

	}

	/**
	 * Never called for mock
	 */
	public String getServerUrl() {
		// return "http://localhost:8080/connector-manager/ok.jsp";
		return null;
	}

	/**
	 * Never called for mock
	 */
	public IId getId(String id) {
		// return new MockDmId(id);
		return null;
	}

	/**
	 * Never called for mock
	 */
	public IClient getClient() {
		// return this;
		return null;
	}

	/**
	 * Never called for mock
	 */
	public void setClient(IClient client) {

	}

	/**
	 * Never called for mock
	 */
	public boolean authenticate(String docbaseName, ILoginInfo loginInfo)
			throws LoginException {
		/*
		 * MockRepositoryEventList mrel = new
		 * MockRepositoryEventList(docbaseName); MockRepository repo = new
		 * MockRepository(mrel);
		 * 
		 * String userID = loginInfo.getUser(); String password =
		 * loginInfo.getPassword();
		 * 
		 * if (userID == null || userID.length() < 1) throw new
		 * LoginException("No user Defined"); MockRepositoryDocument doc =
		 * repo.getStore().getDocByID("users"); if (doc == null) throw new
		 * LoginException("No user Defined"); MockRepositoryProperty property =
		 * doc.getProplist().getProperty("acl"); if (property == null) throw new
		 * LoginException("No user Defined"); String values[] =
		 * property.getValues(); for (int i = 0; i < values.length; i++) if
		 * (values[i].equals(userID)) if (userID.equals(password)) { return
		 * true;// succes } return false;
		 */
		return false;
	}

	/**
	 * Never called for mock
	 */
	public ISession newSession(String docbase, ILoginInfo logInfo)
			throws com.google.enterprise.connector.spi.RepositoryException {
		// setIdentity(docbase, logInfo);
		// return newSession(docbase);
		return null;
	}

}
