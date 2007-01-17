package com.google.enterprise.connector.dctm.dctmdfcwrap;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;

import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.spi.RepositoryException;

public class IDctmSysObject extends IDctmPersistentObject implements ISysObject{
	
	IDfSysObject idfSysObject;
	
	public IDctmSysObject(IDfSysObject idfSysObject){
		super((IDfPersistentObject)idfSysObject);
		this.idfSysObject=idfSysObject;
	}
	
	public IFormat getFormat() throws RepositoryException{
		System.out.println("--- IDctmSysObject getFormat ---");
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
		System.out.println("--- IDctmSysObject getContent ---");
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
		System.out.println("--- IDctmSysObject getPermitEx ---");
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
//		System.out.println("--- IDctmSysObject enumAttrs ---");
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

	public String getACLDomain() throws RepositoryException{
		try {
			return idfSysObject.getACLDomain();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
		
	}

	public String getACLName() throws RepositoryException{
		try {
			return idfSysObject.getACLName();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
	}
	
	public String getString(String name) throws RepositoryException{
		try {
			return idfSysObject.getString(name);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
		
	}

	public boolean getBoolean(String name) throws RepositoryException{
		try {
			return idfSysObject.getBoolean(name);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
		
	}

	public double getDouble(String name) throws RepositoryException{
		try {
			return idfSysObject.getDouble(name);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
	}

	public IId getId(String name) throws RepositoryException{
		try {
			return new IDctmId(idfSysObject.getId(name));
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
	}

	public int getInt(String name) throws RepositoryException{
		try {
			return idfSysObject.getInt(name);
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
	}

	public ITime getTime(String name) throws RepositoryException{
		try {
			return new IDctmTime(idfSysObject.getTime(name));
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
	}
	
	public String getTitle() throws RepositoryException{
		try {
			return idfSysObject.getTitle();
		} catch (DfException e) {
			RepositoryException re = new RepositoryException(e.getMessage(),e.getCause());
			re.setStackTrace(e.getStackTrace());
			throw re;
		}
	}
	
	
}
