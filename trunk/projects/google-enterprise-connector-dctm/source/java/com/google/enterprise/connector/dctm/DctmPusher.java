package com.google.enterprise.connector.dctm;


import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSysObject;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.pusher.DocPusher;
import com.google.enterprise.connector.pusher.Pusher;
import com.google.enterprise.connector.spi.Property;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.ValueType;

/**
 * Class to generate xml feed for a document from the Property Map and send it
 * to GSA.
 */

public class DctmPusher implements Pusher {

	private DocPusher docPusher;
	private ISession session;
	private IClient client;
	
	public void take(PropertyMap pm, String connectorName) {
		docPusher.take(pm,connectorName);

	}

	
	public void take(DctmPropertyMap pm) {
		Property prop;
		try {
		prop = pm.getProperty(SpiConstants.PROPNAME_DOCID);
		IId myId = client.getId(prop.getValue().getString());
        IDctmSysObject sysObj = (IDctmSysObject)session.getObject(myId);
        prop = pm.getProperty(SpiConstants.PROPNAME_CONTENT);
        pm.remove(prop);
        pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_CONTENT, new DctmValue(ValueType.BINARY,sysObj.getContent())));
        
        take(pm,"dctm");
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public DocPusher getDocPusher() {
		return docPusher;
	}

	public void setDocPusher(DocPusher docPusher) {
		this.docPusher = docPusher;
	}


	public IClient getClient() {
		return client;
	}


	public void setClient(IClient client) {
		this.client = client;
	}


	public ISession getSession() {
		return session;
	}


	public void setSession(ISession session) {
		this.session = session;
	}

}
