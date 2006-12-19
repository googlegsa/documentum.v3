package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;

public class DctmMockLoginInfo implements ILoginInfo {
	private String user;
	private String password;
	public void setUser(String usr){
		this.user=usr;
	}
	public void setPassword(String pwd){
		this.password=pwd;
	}
}
