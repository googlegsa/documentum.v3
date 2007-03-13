package com.google.enterprise.connector.dctm.dctmmockwrap;

import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

public class MockDmValue implements IValue {

	private Value value;

	protected MockDmValue(Value val) {
		value = val;
	}

	public String asString() throws RepositoryException {
		String ret = "";
		try {
			ret = value.getString();
		} catch (ValueFormatException e) {
			throw new RepositoryException(e);
		} catch (IllegalStateException e) {
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e);
		}
		return ret;
	}

	public int getDataType(){
		// TODO Auto-generated method stub
		return value.getType();
	}

}
