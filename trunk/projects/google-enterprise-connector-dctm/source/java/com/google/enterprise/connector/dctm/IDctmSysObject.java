package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.io.ByteArrayInputStream;

import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
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
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.client.IDfVersionTreeLabels;
import com.documentum.fc.client.IDfWorkflowBuilder;
import com.documentum.fc.client.acs.IDfAcsTransferPreferences;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.fc.common.IDfTime;

public class IDctmSysObject implements ISysObject{
	IDfSysObject DfSysObject;
	
	
	public IDctmSysObject(){
		super();
	}
	
	public IDctmSysObject(IDfSysObject DfSysObject){
		this.DfSysObject=DfSysObject;
	}
	
	public IDctmFormat getFormat(){
		IDfFormat dfFormat=null;
		try{
			dfFormat=DfSysObject.getFormat();
		}catch(DfException de){
			de.getMessage();
		}
		return new IDctmFormat(dfFormat);
	}
	
	public long getContentSize(){
		long contentSize=0;
		try{
			contentSize=DfSysObject.getContentSize();
		}catch(DfException de){
			de.getMessage();
		}
		return contentSize;
		
	}
	
	public ByteArrayInputStream getContent(){
		ByteArrayInputStream content=null;
		try{
			content=DfSysObject.getContent();
		}catch(DfException de){
			de.getMessage();
		}
		return content;
	}
	
	public int getPermitEx(String name){
		int perm=0;
		try{
			perm=DfSysObject.getPermitEx(name);
		}catch(DfException de){
			de.getMessage();
		}
		return(perm);
	}
	
	public IDctmId getObjectId(){
		IDfId idfId=null;
		try{
			idfId=DfSysObject.getObjectId();
		}catch(DfException de){
			de.getMessage();
		}
		return new IDctmId(idfId);
	}
}
