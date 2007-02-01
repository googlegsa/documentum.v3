package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.util.Enumeration;
import java.util.Hashtable;

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
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryEventList;
import com.google.enterprise.connector.mock.MockRepositoryProperty;

import com.google.enterprise.connector.mock.jcr.MockJcrRepository;
import com.google.enterprise.connector.mock.jcr.MockJcrSession;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;

//Implements four interfaces to simulate the session pool.
//Does not manage multiple sessions for the same docbase (for the moment) 
public class MockDmClient implements IClientX, IClient,
		ISessionManager {

	private MockDmSession currentSession;

	private Hashtable sessMgerCreds = new Hashtable(1, 1);

	private Hashtable sessMgerSessions = new Hashtable(1, 1);

	private Hashtable sessMgerIDs = new Hashtable(1, 1);

	public MockDmClient() {
	}


	public ISessionManager newSessionManager() {
		return this;
	}

	/**
	 * Factory method for an IDfQuery object. Constructs an new query object to
	 * use for sending DQL queries to Documentum servers.
	 */
	public IQuery getQuery() {
		return new MockDmQuery();
	}

	public boolean authenticate(String docbaseName, ILoginInfo loginInfo)
			throws LoginException {
		MockRepositoryEventList mrel = new MockRepositoryEventList(docbaseName);
		MockRepository repo = new MockRepository(mrel);

		String userID = loginInfo.getUser();
		String password = loginInfo.getPassword();

		if (userID == null || userID.length() < 1)
			throw new LoginException("No user Defined");
		MockRepositoryDocument doc = repo.getStore().getDocByID("users");
		if (doc == null)
			throw new LoginException("No user Defined");
		MockRepositoryProperty property = doc.getProplist().getProperty("acl");
		if (property == null)
			throw new LoginException("No user Defined");
		String values[] = property.getValues();
		for (int i = 0; i < values.length; i++)
			if (values[i].equals(userID))
				if (userID.equals(password)) {
					return true;// succes
				}
		return false;
	}

	/**
	 * Not advised
	 * 
	 * @throws com.google.enterprise.connector.spi.RepositoryException
	 */
	public ISession newSession(String docbase, ILoginInfo logInfo)
			throws com.google.enterprise.connector.spi.RepositoryException {
		setIdentity(docbase, logInfo);
		return newSession(docbase);
	}

	/**
	 * Factory method for an IDfLoginInfo object. Constructs a new empty object
	 * to set with login details prior to connecting to Documentum servers.
	 */
	public ILoginInfo getLoginInfo() {
		return new MockDmLoginInfo();
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

	/**
	 * 
	 */
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
			return null;
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
			throws com.google.enterprise.connector.spi.RepositoryException {
		// db is actually the suffix of the filename that is used to create the
		// eventlist.
		// As there is no way we can retrieve it, we will store it in the
		// MockDmSession we create.
		MockRepositoryEventList mrel = new MockRepositoryEventList(db);

		MockJcrRepository repo = new MockJcrRepository(new MockRepository(mrel));

		Credentials creds = new SimpleCredentials(iLI.getUser(), iLI
				.getPassword().toCharArray());

		MockJcrSession sess = null;
		try {
			sess = (MockJcrSession) repo.login(creds);
		} catch (javax.jcr.LoginException e) {
			throw new LoginException(e);
		} catch (javax.jcr.RepositoryException e) {
			throw new com.google.enterprise.connector.spi.RepositoryException(e);
		}
		// If not caught any error, authentication successful
		String sessID = createNewId();
		sessMgerIDs.put(sessID, db);
		return new MockDmSession(repo, sess, db);

	}

	private String createNewId() {
		String uniqueID = "";
		int i = 0;
		while (true) {
			if (!sessMgerIDs.containsKey(Integer.toBinaryString(i)))
				break;
			i++;
		}
		uniqueID = Integer.toBinaryString(i);
		return uniqueID;
	}

	public void setSession(ISession session) {
		currentSession = (MockDmSession) session;
	}

	public void setSessionManager(ISessionManager session) {
		// IClient and ISessionManager are both implemented by the MockDmClient
		// then nothing to do.
	}

	public void release(ISession session) {
		// No need to do anything regarding Mock sessions.
	}

	public void setServerUrl(String serverUrl) {
		// Wait for Google to ask me to do it
	}

	public String getDocbaseName() {
		Enumeration e = sessMgerSessions.keys();
		while (e.hasMoreElements()) {
			Object n = e.nextElement();
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
		currentSession = (MockDmSession) sessMgerSessions.get((Object) docbaseName);
	}

	public String getServerUrl() {
		return "http://localhost:8080/connector-manager/ok.jsp";
	}

	public ISessionManager getSessionManager() {
		return this;
	}

	public IId getId(String id) {
		return new MockDmId(id);
	}

	public IClient getLocalClient() throws RepositoryException {
		return this;
	}

	public IClient getClient() {
		
		return this;
	}
	
	public void setClient(IClient client){
	
	}

	public ILoginInfo getIdentity(String docbase){
		return null;
	}
	
	public boolean authenticate(String docbaseName){
		return true;
	}

	public void clearIdentity(String docbase){

	}

}
