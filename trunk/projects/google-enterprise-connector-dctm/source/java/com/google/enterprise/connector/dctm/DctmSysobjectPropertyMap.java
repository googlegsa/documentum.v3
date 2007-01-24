package com.google.enterprise.connector.dctm;

import java.util.Iterator;

import junit.framework.Assert;

import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSession;
import com.google.enterprise.connector.dctm.dctmdfcwrap.DmSysObject;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;

public class DctmSysobjectPropertyMap implements PropertyMap {
	
	private String docid;
	private ISysObject object = null;
	private ISessionManager sessionManager = null;
	private IClientX clientX;
	
	public DctmSysobjectPropertyMap(String docid, ISessionManager sessionManager, IClientX clientX) {
		this.docid = docid;
		this.sessionManager = sessionManager;
		this.clientX = clientX;
	}
	
	private void fetch() throws RepositoryException {
		if (object != null) {
			return;
		}
		ISession session = null;
		try {
			String docbaseName = sessionManager.getDocbaseName();			
			session = sessionManager.getSession(docbaseName);
			IId id = clientX.getId(docid);
			object = session.getObject(id);
		} finally {
			if (session != null) {
				sessionManager.release(session);
			}
		}			
	}
	
	public Property getProperty(String name) throws RepositoryException {
		fetch();
		if (name == SpiConstants.PROPNAME_DOCID) {
			return getProperty("r_object_id");
		}
		return null;
	}
	
	public Iterator getProperties() throws RepositoryException {
		// get the list of atrrs from the sysobject
		// for each one - check whether you want to omit it
		// add the ones you like to a 
//		HashSet propNames = new HashSet();
//		propNames.add(thisone);
//		return propNames.iterator();
		throw new UnsupportedOperationException();
	}
	
}
