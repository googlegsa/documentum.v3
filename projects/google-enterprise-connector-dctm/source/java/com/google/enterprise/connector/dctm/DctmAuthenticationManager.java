package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.spi.AuthenticationManager;
import com.google.enterprise.connector.spi.LoginException;

public class DctmAuthenticationManager implements AuthenticationManager {

	ISessionManager sessionManager;

	IClientX clientX;

	ILoginInfo loginInfo;

	public DctmAuthenticationManager(IClientX clientX) {
		setClientX(clientX);
		sessionManager = clientX.getSessionManager();
	}

	public boolean authenticate(String username, String password) {
		setLoginInfo(username, password);
		sessionManager.clearIdentity(sessionManager.getDocbaseName());
		try {
			sessionManager.setIdentity(sessionManager.getDocbaseName(),
					loginInfo);
		} catch (LoginException e) {
			return false;
		}

		boolean authenticate = false;

		authenticate = sessionManager.authenticate(sessionManager
				.getDocbaseName());
		if (DebugFinalData.debugInEclipse) {
			System.out.println("DCTMAuthenticate method authenticate "
					+ authenticate);
		}

		return authenticate;
	}

	public void setLoginInfo(String username, String password) {
		loginInfo = clientX.getLoginInfo();
		loginInfo.setUser(username);
		loginInfo.setPassword(password);
	}

	public ILoginInfo getLoginInfo() {
		return loginInfo;
	}

	public IClientX getClientX() {
		return clientX;
	}

	public void setClientX(IClientX clientX) {
		this.clientX = clientX;
	}

}
