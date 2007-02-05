package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;
import com.google.enterprise.connector.dctm.DebugFinalData;
import com.google.enterprise.connector.dctm.dfcwrap.IId;

public class DmId implements IId {
	IDfId idfId;

	public DmId(String id) {
		this.idfId = new DfId(id);
	}

	public DmId(IDfId idfId) {
		this.idfId = idfId;
	}

	public IDfId getidfId() {
		if (DebugFinalData.debugInEclipse) {
			System.out.println("--- DmId getidfId ---");
		}

		return idfId;
	}

	public void setidfId(IDfId idfId) {
		this.idfId = idfId;
	}

}
