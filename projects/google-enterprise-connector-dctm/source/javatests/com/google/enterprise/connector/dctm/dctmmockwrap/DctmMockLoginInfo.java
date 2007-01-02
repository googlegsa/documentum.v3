package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;

public class DctmMockLoginInfo implements ILoginInfo {

	private String user;
	private String password;
	
	public DctmMockLoginInfo(){
		user=null;
		password=null;
	}
	
	public void setUser(String u) {
		user=u;
	}

	public void setPassword(String p) {
		password=p;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

}
