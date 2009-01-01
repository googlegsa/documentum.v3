package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.AuthenticationIdentity;

public class DctmAuthenticationIdentity implements AuthenticationIdentity {

	private String username;

	private String password;

	public DctmAuthenticationIdentity(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {

		return username;
	}

	public String getPassword() {

		return password;
	}

}
