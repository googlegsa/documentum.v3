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

	public boolean asBoolean() throws RepositoryException {
		
		boolean ret = false;
		try {
			ret = value.getBoolean();
		} catch (ValueFormatException e) {
			throw new RepositoryException(e);
		} catch (IllegalStateException e) {
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e);
		}
		return ret;
	}

	public double asDouble() throws RepositoryException {
		double ret = 0;
			try {
				ret = value.getLong();
			} catch (ValueFormatException e) {
				throw new RepositoryException(e);
			} catch (IllegalStateException e) {
				throw new RepositoryException(e);
			} catch (javax.jcr.RepositoryException e) {
				throw new RepositoryException(e);
			}
			return ret;
	}

	public long asInteger() throws RepositoryException {
		long ret = 0;
		try {
			ret = value.getLong();
		} catch (ValueFormatException e) {
			throw new RepositoryException(e);
		} catch (IllegalStateException e) {
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e);
		}
		return ret;
	}

	public ITime asTime() throws RepositoryException {
		MockDmTime ret = null;
		try {
			ret = new MockDmTime(value.getDate().getTime());
		} catch (ValueFormatException e) {
			throw new RepositoryException(e);
		} catch (IllegalStateException e) {
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e);
		}
		return ret;
	}

}
