package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;

import junit.framework.TestCase;

public class DctmConnectorTest extends TestCase {

	public void testLogin() throws RepositoryException {

		Connector connector = new DctmConnector();
		((DctmConnector) connector).setLogin(DmInitialize.DM_LOGIN_OK1);
		((DctmConnector) connector).setPassword(DmInitialize.DM_PWD_OK1);
		((DctmConnector) connector).setDocbase(DmInitialize.DM_DOCBASE);
		((DctmConnector) connector).setClientX(DmInitialize.DM_CLIENTX);
		((DctmConnector) connector)
				.setWebtop_display_url(DmInitialize.DM_WEBTOP_SERVER_URL);
		((DctmConnector) connector).setIs_public("false");
		DctmSession dctmSession = (DctmSession) connector.login();
		assertNotNull(dctmSession);
		assertEquals(DmInitialize.DM_WEBTOP_SERVER_URL,
				dctmSession.webtopServerUrl);

	}

}
