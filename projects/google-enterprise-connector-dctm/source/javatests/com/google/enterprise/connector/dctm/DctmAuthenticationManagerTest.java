package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

import junit.framework.TestCase;

public class DctmAuthenticationManagerTest extends TestCase {

	/*
	 * Test method for 'com.google.enterprise.connector.dctm.DctmAuthenticationManager.authenticate(String, String)'
	 */
	public void testAuthenticate() throws LoginException, RepositoryException {
		Connector connector = new DctmConnector();
		
		((DctmConnector) connector).setLogin("emilie");
		((DctmConnector) connector).setPassword("p@ssw0rd");
		((DctmConnector) connector).setDocbase("gsadctm");
		((DctmConnector) connector).setClient("com.google.enterprise.connector.dctm.dctmdfcwrap.DmClient");
		((DctmConnector) connector).setQueryStringUnboundedDefault("select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document' " +"order by r_modify_date, i_chronicle_id ");
		((DctmConnector) connector).setWebtopServerUrl("http://swp-vm-wt:8080/webtop/drl/objectId/");
		((DctmConnector) connector).setQueryStringBoundedDefault("select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type=''dm_document'' and r_modify_date >= "+ "''{0}'' "+"order by r_modify_date, i_chronicle_id");
		((DctmConnector) connector).setAttributeName("r_object_id");
		((DctmConnector) connector).setQueryStringAuthoriseDefault("select r_object_id from dm_sysobject where r_object_id in (");
		Session sess = (DctmSession) connector.login();
		DctmAuthenticationManager authentManager = (DctmAuthenticationManager) sess.getAuthenticationManager();
		
		assertFalse(authentManager.authenticate("user1","falsePassword"));
		assertFalse(authentManager.authenticate("user1",null));
		assertFalse(authentManager.authenticate(null,"p@ssw0rd"));
		assertFalse(authentManager.authenticate(null,null));
		
		assertTrue(authentManager.authenticate("emilie","p@ssw0rd"));
		assertTrue(authentManager.authenticate("user1","p@ssw0rd"));
		assertTrue(authentManager.authenticate("user2","p@ssw0rd"));
		assertTrue(authentManager.authenticate("queryUser","p@ssw0rd"));
		assertTrue(authentManager.authenticate("Fred","UnDeux34"));

	}

}
