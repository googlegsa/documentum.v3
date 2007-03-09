package com.google.enterprise.connector.dctm;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.LoginException;

public class DctmAuthenticationManager implements AuthenticationManager {

	ISessionManager sessionManager;

	IClientX clientX;

	ILoginInfo loginInfo;

	private static Logger logger = null;

	static {
		logger = Logger.getLogger(DctmAuthenticationManager.class.getName());
		logger.setLevel(Level.ALL);
	}

	public DctmAuthenticationManager(IClientX clientX) {
		setClientX(clientX);
		sessionManager = clientX.getSessionManager();
	}

	public boolean authenticate(String username, String password) {
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 2) {
			logger.warning("authentication process for user  " + username);
		}
		setLoginInfo(username, password);
		sessionManager.clearIdentity(sessionManager.getDocbaseName());
		try {
			sessionManager.setIdentity(sessionManager.getDocbaseName(),
					loginInfo);
		} catch (LoginException e) {
			logger.warning("authentication failed for user  " + username
					+ "\ncause:" + e.getMessage());
			return false;
		}

		boolean authenticate = false;

		authenticate = sessionManager.authenticate(sessionManager
				.getDocbaseName());

		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 2) {
			logger.log(Level.INFO, "authentication status: " + authenticate);
		}

		return authenticate;
	}

	public void setLoginInfo(String username, String password) {
		loginInfo = clientX.getLoginInfo();
		loginInfo.setUser(username);
		loginInfo.setPassword(password);
	}

	public void setClientX(IClientX clientX) {
		this.clientX = clientX;
	}

}
