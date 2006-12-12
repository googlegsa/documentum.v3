package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.io.ByteArrayInputStream;

import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;

public class IDctmSysObject implements ISysObject{
	IDfSysObject DfSysObject;
	
	
	public IDctmSysObject(){
		super();
	}
	
	public IDctmSysObject(IDfSysObject DfSysObject){
		this.DfSysObject=DfSysObject;
	}
	
	public IFormat getFormat(){
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
	
	public IId getObjectId(){
		IDfId idfId=null;
		try{
			idfId=DfSysObject.getObjectId();
		}catch(DfException de){
			de.getMessage();
		}
		return new IDctmId(idfId);
	}
}
