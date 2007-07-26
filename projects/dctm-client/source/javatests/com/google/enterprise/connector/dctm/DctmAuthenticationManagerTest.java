package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

import junit.framework.TestCase;

public class DctmAuthenticationManagerTest extends TestCase {

	/*
	 * Test method for
	 * 'com.google.enterprise.connector.dctm.DctmAuthenticationManager.authenticate(String,
	 * String)'
	 */

	public void testAuthenticate() throws RepositoryException {
		Connector connector = new DctmConnector();
		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector)
				.setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
		((DctmConnector) connector).setIs_public("false");

		Session sess = (DctmSession) connector.login();

		DctmAuthenticationManager authentManager = (DctmAuthenticationManager) sess
				.getAuthenticationManager();

		assertTrue(authentManager.authenticate(
				new DctmAuthenticationIdentity(DmInitialize.DM_LOGIN_OK1,
						DmInitialize.DM_PWD_OK1)).isValid());
		assertFalse(authentManager.authenticate(
				new DctmAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2,
						DmInitialize.DM_PWD_KO)).isValid());
		assertTrue(authentManager.authenticate(
				new DctmAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2,
						DmInitialize.DM_PWD_OK2)).isValid());
		assertFalse(authentManager.authenticate(
				new DctmAuthenticationIdentity(DmInitialize.DM_LOGIN_OK2,
						DmInitialize.DM_PWD_KO)).isValid());
		assertFalse(authentManager
				.authenticate(
						new DctmAuthenticationIdentity(
								DmInitialize.DM_LOGIN_OK2, null)).isValid());
		assertFalse(authentManager.authenticate(
				new DctmAuthenticationIdentity(null, DmInitialize.DM_PWD_OK1))
				.isValid());
		assertFalse(authentManager.authenticate(
				new DctmAuthenticationIdentity(null, null)).isValid());

		assertTrue(authentManager.authenticate(
				new DctmAuthenticationIdentity(DmInitialize.DM_LOGIN_OK3,
						DmInitialize.DM_PWD_OK3)).isValid());
		assertTrue(authentManager.authenticate(
				new DctmAuthenticationIdentity(DmInitialize.DM_LOGIN_OK1,
						DmInitialize.DM_PWD_OK1)).isValid());
		assertTrue(authentManager.authenticate(
				new DctmAuthenticationIdentity(DmInitialize.DM_LOGIN_OK5,
						DmInitialize.DM_PWD_OK5)).isValid());
	}

}
