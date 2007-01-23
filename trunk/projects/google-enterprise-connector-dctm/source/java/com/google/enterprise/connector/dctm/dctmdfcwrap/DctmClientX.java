package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.spi.RepositoryException;

public class DctmClientX implements IClientX {
	
	private IDfClientX clientX = null;
	
	public DctmClientX() {
		this.clientX = new DfClientX();
	}
	
	public IId getId(String id) {
		IDfId dfid = clientX.getId(id);
		return new DmId(dfid);
	}
	
	public IClient getLocalClient() throws RepositoryException {
		
		IDfClient localClient;
		try {
			localClient = clientX.getLocalClient();
		} catch (DfException e) {
			throw new RepositoryException(e);
		}
		DmClient dctmClient = new DmClient(localClient, clientX);
		return dctmClient;
	}
	
}
