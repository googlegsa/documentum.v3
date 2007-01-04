package com.google.enterprise.connector.dctm;

import java.util.HashMap;

import com.google.enterprise.connector.persist.ConnectorStateStore;

public class DctmConnectorStateStore extends HashMap implements ConnectorStateStore {
	
	public String getConnectorState(String connectorName) {
		return (String) this.get(connectorName);
	}
	
	public void storeConnectorState(String connectorName, String connectorState) {
		this.put(connectorName, connectorState);
	}
	
	public void removeConnectorState(String connectorName) {
		this.remove(connectorName);
	}
}
