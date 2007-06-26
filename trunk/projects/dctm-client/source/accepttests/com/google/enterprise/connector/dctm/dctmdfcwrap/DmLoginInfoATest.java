package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.DmInitialize;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.spi.RepositoryLoginException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DmLoginInfoATest extends TestCase {

	IClientX dctmClientX;

	ILoginInfo loginInfo;

	public void setUp() throws Exception {
		super.setUp();
		dctmClientX = new DmClientX();
		loginInfo = dctmClientX.getLoginInfo();

	}

	public void testGetSetUser() throws RepositoryLoginException {
		loginInfo.setUser(DmInitialize.DM_LOGIN_OK1);
		Assert.assertEquals(DmInitialize.DM_LOGIN_OK1, loginInfo.getUser());
	}

	public void testGetSetPassword() throws RepositoryLoginException {
		String password = DmInitialize.DM_PWD_OK1;
		loginInfo.setPassword(password);

		Assert.assertEquals(DmInitialize.DM_PWD_OK1, loginInfo.getPassword());
	}

}
