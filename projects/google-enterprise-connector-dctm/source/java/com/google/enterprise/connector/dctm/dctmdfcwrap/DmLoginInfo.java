package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.common.IDfLoginInfo;

import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;

public class DmLoginInfo implements ILoginInfo{
	IDfLoginInfo idfLoginInfo=null;
	
	public DmLoginInfo(IDfLoginInfo tmp){
		idfLoginInfo=tmp;
	}
	
	public void setUser(String u){
		idfLoginInfo.setUser(u);
	}
	
	public void setPassword(String p){
		idfLoginInfo.setPassword(p);
	}

	public IDfLoginInfo getIdfLoginInfo() {
		return idfLoginInfo;
	}

	public void setIdfLoginInfo(IDfLoginInfo idfLoginInfo) {
		this.idfLoginInfo = idfLoginInfo;
	}

	public String getUser() {
		return idfLoginInfo.getUser();
	}

	public String getPassword() {
		return idfLoginInfo.getPassword();
	}
	
}
