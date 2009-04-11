package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.SimpleAuthenticationIdentity;

public class DctmAuthenticationIdentity extends SimpleAuthenticationIdentity {
	public DctmAuthenticationIdentity(String username, String password) {
		super(username, password);
	}
}
