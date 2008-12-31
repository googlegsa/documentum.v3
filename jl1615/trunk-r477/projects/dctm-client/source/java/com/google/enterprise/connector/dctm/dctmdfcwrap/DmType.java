package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfType;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.spi.RepositoryException;

public class DmType implements IType {
	IDfType idfType;
	
	public DmType(IDfType idfType) {
		this.idfType = idfType;
	}
	
	public int getTypeAttrCount() throws RepositoryException {
		int attrCount=0;
		try {
			attrCount = idfType.getTypeAttrCount();
			
		} catch (DfException de) {
			RepositoryException re = new RepositoryException(de);
			throw re;
		}
		return attrCount;
	}

	public IType getSuperType() throws RepositoryException {
		IDfType idfSuperType = null;
		try {
			idfSuperType = idfType.getSuperType();
		} catch (DfException de) {
			RepositoryException re = new RepositoryException(de);
			throw re;
		}
		return (new DmType(idfSuperType));
	}

	public boolean isSubTypeOf(String type) throws RepositoryException {
		boolean subTypeOrNot = false;
		try {
			subTypeOrNot = idfType.isSubTypeOf(type);
		} catch (DfException de) {
			RepositoryException re = new RepositoryException(de);
			throw re;
		}
		return (subTypeOrNot);
	}
	
	public String getName() throws RepositoryException {
		String name = null;
		try {
			name = idfType.getName();
		} catch (DfException de) {
			RepositoryException re = new RepositoryException(de);
			throw re;
		}
		return (name);
	}
	
	public IAttr getTypeAttr(int attrIndex) throws RepositoryException {
		IDfAttr idfAttr = null;
		try {
			idfAttr = idfType.getTypeAttr(attrIndex);
		} catch (DfException de) {
			RepositoryException re = new RepositoryException(de);
			throw re;
		}
		return (new DmAttr(idfAttr));
		
	}
	
	
	public String getDescription() throws RepositoryException {
		String desc = "";
		try {
			desc = idfType.getDescription();
		} catch (DfException de) {
			RepositoryException re = new RepositoryException(de);
			throw re;
		}
		return desc;
		
	}
	
}	
