package com.google.enterprise.connector.dctm;


import java.io.ByteArrayInputStream;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSysObject;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.pusher.DocPusher;
import com.google.enterprise.connector.pusher.MockPusher;
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

	private MockPusher mockPusher;
	private DocPusher docPusher;
	private ISession session;
	private IClient client;
	
	
	public DctmPusher(){
//		MockFeedConnection mockFeedConnection = new MockFeedConnection();

		//docPusher = new DocPusher(new GsaFeedConnection("swp-srv-gsa",19900));
		docPusher = new DocPusher(new DctmFeedConnection());
		
	}
	
	public void take(PropertyMap pm, String connectorName) {
		docPusher.take(pm,connectorName);
	}

	
	public void take(DctmPropertyMap pm) {
		Property prop;
		try {
		prop = pm.getProperty(SpiConstants.PROPNAME_DOCID);
		IId myId = client.getId(prop.getValue().getString());
		System.out.println(prop.getValue().getString() + " "+SpiConstants.PROPNAME_DOCID);
        IDctmSysObject sysObj = (IDctmSysObject)session.getObject(myId);
        prop = pm.getProperty(SpiConstants.PROPNAME_CONTENT);
        pm.remove(prop);
        ByteArrayInputStream array = sysObj.getContent();
        DctmValue val = null;
        if(array == null){
        	val = new DctmValue(ValueType.BINARY,new ByteArrayInputStream(new byte[1]));
        }else{
        	val = new DctmValue(ValueType.BINARY,array);
        }
        pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_CONTENT, val));
        prop = pm.getProperty(SpiConstants.PROPNAME_DOCID);
       
        take(pm,"dctm");
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public MockPusher getDocPusher() {
		return mockPusher;
	}

	public void setDocPusher(MockPusher docPusher) {
		this.mockPusher = docPusher;
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
