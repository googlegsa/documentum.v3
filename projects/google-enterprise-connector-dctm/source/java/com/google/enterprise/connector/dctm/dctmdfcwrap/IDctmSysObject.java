package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;

import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.RepositoryException;

public class IDctmSysObject extends IDctmPersistentObject implements ISysObject{
	
	IDfSysObject idfSysObject;
	
	public IDctmSysObject(IDfSysObject idfSysObject){
		super((IDfPersistentObject)idfSysObject);
		this.idfSysObject=idfSysObject;
	}
	
	public IFormat getFormat() throws RepositoryException{
		IDfFormat idfFormat = null;
		try{
			idfFormat = idfSysObject.getFormat();
			
		}catch(DfException e){
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
		
		return new IDctmFormat(idfFormat);
	}
	
	public long getContentSize() throws RepositoryException{
		long contentSize = 0;
		try{
			contentSize = idfSysObject.getContentSize();
		}catch(DfException e){
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
		return contentSize;
		
	}
	
	public ByteArrayInputStream getContent() throws RepositoryException{
		ByteArrayInputStream content = null;
		try{
			content=idfSysObject.getContent();
		}catch(DfException e){
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
		return content;
	}
	
	public int getPermitEx(String name) throws RepositoryException{
		int perm = 0;
		try{
			perm = idfSysObject.getPermitEx(name);
		}catch(DfException e){
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
		return(perm);
	}
	
	public Enumeration enumAttrs() throws RepositoryException{
		Enumeration attrs=null;
			try {
				attrs = idfSysObject.enumAttrs();
			} catch (DfException e) {
				RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
				re.setStackTrace(e.getStackTrace());
				throw re;
			}
		if (attrs!=null) return(attrs);
		return new StringTokenizer("");
	}
	
	
}
