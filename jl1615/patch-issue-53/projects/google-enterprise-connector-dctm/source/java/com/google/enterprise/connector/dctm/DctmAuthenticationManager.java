package com.google.enterprise.connector.dctm;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.AuthenticationIdentity;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.AuthenticationResponse;
import com.google.enterprise.connector.spi.RepositoryLoginException;
import com.google.enterprise.connector.spi.RepositoryException;

public class DctmAuthenticationManager implements AuthenticationManager {

	ISessionManager sessionManager;

	IClientX clientX;

	private static Logger logger = null;

	static {
		logger = Logger.getLogger(DctmAuthenticationManager.class.getName());
	}

	public DctmAuthenticationManager(IClientX clientX) {
		setClientX(clientX);
		sessionManager = clientX.getSessionManager();
	}

	public AuthenticationResponse authenticate(
			AuthenticationIdentity authenticationIdentity) throws RepositoryLoginException, RepositoryException{
		String username = authenticationIdentity.getUsername();
		String password = authenticationIdentity.getPassword();

		logger.info("authentication process for user " + username);

		ILoginInfo loginInfo = getLoginInfo(username, password);
		ISessionManager sessionManagerUser = clientX.getLocalClient().newSessionManager();
		try {
			sessionManagerUser.setIdentity(sessionManager.getDocbaseName(),
					loginInfo);
		} catch (RepositoryLoginException e) {
			logger.warning("authentication failed for user  " + username
					+ "\ncause:" + e.getMessage());

			return new AuthenticationResponse(false, "");
		}
		boolean authenticate = false;

		authenticate = sessionManagerUser.authenticate(sessionManager
				.getDocbaseName());

		logger.log(Level.INFO, "authentication status: " + authenticate);

		return new AuthenticationResponse(authenticate, "");
	}

	private ILoginInfo getLoginInfo(String username, String password) {
		ILoginInfo loginInfo = clientX.getLoginInfo();
		loginInfo.setUser(username);
		loginInfo.setPassword(password);
		return loginInfo;
	}

	public void setClientX(IClientX clientX) {
		this.clientX = clientX;
	}

}
