package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.QueryTraversalManager;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class DocRetrievalTest extends TestCase {
	/**
	 * After instantiation, start/resume Traversal processes a query against target docbase.
	 * This class aims to provide time and memory usage according to the amount and variety
	 * of docs to process.
	 */
	public void testInstantiate(){
		String user, password, client, docbase;
		user="user1";
		password="p@ssw0rd";
		client="com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmClient";
		docbase="gsadctm";
		String QUERY_STRING_UNBOUNDED_DEFAULT = "select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document' " +
		"order by r_modify_date, i_chronicle_id ";
		String QUERY_STRING_BOUNDED_DEFAULT = 
			"select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type=''dm_document'' and r_modify_date >= "+ 
			"''{0}'' "+
			"order by r_modify_date, i_chronicle_id";
		Session session = null;
		Connector connector = null;
		QueryTraversalManager qtm = null;		
		connector = new DctmConnector();
		
		((DctmConnector) connector).setLogin(user);
		((DctmConnector) connector).setPassword(password);
		((DctmConnector) connector).setDocbase(docbase);
		((DctmConnector) connector).setClient(client);
		try {
			session = (DctmSession) connector.login();
			qtm = (DctmQueryTraversalManager) session.getQueryTraversalManager();
		} catch (LoginException e) {
			throw new AssertionFailedError("Login exception on post login tests. " +
					"Check initial values. (" + e.getMessage() + " ; " + e.getCause() + ")");
		} catch (RepositoryException e) {
			throw new AssertionFailedError("Repository exception on post instantiation tests. " +
					"Check initial values. (" + e.getMessage() + " ; " + e.getCause() + ")");
		}
		
		outputPerformances.calibrate();
		assertEquals("com.google.enterprise.connector.dctm.DctmQueryTraversalManager",qtm.getClass().getName());
	}
}
