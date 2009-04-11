package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.common.IDfValue;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;

public class DmValue implements IValue {

	IDfValue idfValue;

	public DmValue(IDfValue idfValue) {
		this.idfValue = idfValue;
	}

	public String asString() {
		return idfValue.asString();
	}

	public boolean asBoolean() {
		return idfValue.asBoolean();
	}

	public double asDouble() {
		return idfValue.asDouble();
	}

	public long asInteger() {
		return idfValue.asInteger();
	}

	public ITime asTime() {
		return new DmTime(idfValue.asTime());
	}

	public IId asId() {
		return new DmId(idfValue.asId());
	}
}
