package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfIdentityException;
import com.documentum.fc.client.DfPrincipalException;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfAuditTrailManager;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfEnumeration;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfGroup;
import com.documentum.fc.client.IDfLocalModuleRegistry;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfRelationType;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.client.IDfVersionTreeLabels;
import com.documentum.fc.client.IDfWorkflowBuilder;
import com.documentum.fc.client.acs.IDfAcsTransferPreferences;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.fc.common.IDfTime;

public class IDctmSessionManager implements ISessionManager{
	IDfSessionManager DfSessionManager;
	
	public IDctmSessionManager (IDfSessionManager DfSessionManager){
		this.DfSessionManager=DfSessionManager;
	}
	
	public IDctmSession getSession(String docbase){
		IDfSession DfSession=null;
		try{
			DfSession=DfSessionManager.getSession(docbase);
		}catch(DfIdentityException di){
			di.getMessage();
		}catch(DfAuthenticationException da){
			da.getMessage();
		}catch(DfPrincipalException dp){
			dp.getMessage();
		}catch(DfServiceException ds){
			ds.getMessage();
		}
		return new IDctmSession(DfSession);
	}
	
	public void setIdentity(String docbase,IDctmLoginInfo identity){
		IDfLoginInfo idfLoginInfo=identity.getIdfLoginInfo();
		try{
			DfSessionManager.setIdentity(docbase,idfLoginInfo);
		}catch(DfServiceException dse){
			dse.getMessage();
		}
	}
	
	
}
