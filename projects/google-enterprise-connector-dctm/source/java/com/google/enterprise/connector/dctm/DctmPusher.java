package com.google.enterprise.connector.dctm;


import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSysObject;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
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
	private IClient client;
	
	
	
	public DctmPusher(){
		docPusher = new DocPusher(new DctmFeedConnection());
		
		
	}
	
	public void take(PropertyMap pm, String connectorName) {
		DctmPropertyMap dctmPm = (DctmPropertyMap) pm;
		Property prop;
		
		IDctmSysObject sysObj;
		ByteArrayInputStream array;
		try {
			prop = dctmPm.getProperty(SpiConstants.PROPNAME_DOCID);
			IId myId = client.getId(prop.getValue().getString());
	        sysObj = (IDctmSysObject)this.client.getSession().getObject(myId);
	        prop = dctmPm.getProperty(SpiConstants.PROPNAME_CONTENT);	    
	        dctmPm.remove(prop);
	        array = sysObj.getContent();
	        DctmValue val = null;
	        if(array == null){
	        	val = new DctmValue(ValueType.BINARY,new ByteArrayInputStream(new byte[1]));
	        }else{
	        	val = new DctmValue(ValueType.BINARY,array);
	        }
	        dctmPm.putProperty(new DctmProperty(SpiConstants.PROPNAME_CONTENT, val));
	        prop = dctmPm.getProperty(SpiConstants.PROPNAME_DOCID);
	        docPusher.take(dctmPm,connectorName);
	        
	        if(array != null){
	        	array.close();
	        }
	        dctmPm.clear();
	        
	       
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	
	
	public IClient getClient() {
		return client;
	}


	public void setClient(IClient client) {
		this.client = client;
	}


	

}
