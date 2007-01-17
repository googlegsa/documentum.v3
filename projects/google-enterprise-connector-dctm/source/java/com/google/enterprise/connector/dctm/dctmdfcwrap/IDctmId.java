package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.dfcwrap.IId;

public class IDctmId implements IId{
	IDfId idfId;
	
	public IDctmId(String id){
		this.idfId = new DfId(id);
	}

	public IDctmId(IDfId idfId){
		this.idfId=idfId;
	}
	
	public IDfId getidfId() {
		System.out.println("--- IDctmId getidfId ---");
		return idfId;
	}

	public void setidfId(IDfId idfId) {
		this.idfId = idfId;
	}
}
