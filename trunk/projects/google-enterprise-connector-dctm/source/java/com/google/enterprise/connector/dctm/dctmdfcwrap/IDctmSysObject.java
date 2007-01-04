package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.io.ByteArrayInputStream;

import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;

public class IDctmSysObject extends IDctmPersistentObject implements ISysObject{
	
	IDfSysObject idfSysObject;
	
	public IDctmSysObject(IDfSysObject idfSysObject){
		super((IDfPersistentObject)idfSysObject);
		this.idfSysObject=idfSysObject;
	}
	
	public IFormat getFormat(){
		IDfFormat idfFormat = null;
		try{
			idfFormat = idfSysObject.getFormat();
			
		}catch(DfException de){
			System.out.println(de.getMessage());
		}
		
		return new IDctmFormat(idfFormat);
	}
	
	public long getContentSize(){
		long contentSize = 0;
		try{
			contentSize = idfSysObject.getContentSize();
		}catch(DfException de){
			de.getMessage();
		}
		return contentSize;
		
	}
	
	public ByteArrayInputStream getContent(){
		ByteArrayInputStream content = null;
		System.out.println("getContent");
		try{
			content=idfSysObject.getContent();
		}catch(DfException de){
			de.getMessage();
		}
		return content;
	}
	
	public int getPermitEx(String name){
		int perm = 0;
		try{
			perm = idfSysObject.getPermitEx(name);
		}catch(DfException de){
			de.getMessage();
		}
		return(perm);
	}
	
	
}
