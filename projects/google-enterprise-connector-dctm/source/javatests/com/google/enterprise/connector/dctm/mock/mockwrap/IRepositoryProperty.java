package com.google.enterprise.connector.dctm.mock.mockwrap;

import com.google.enterprise.connector.mock.MockRepositoryProperty;
import com.google.enterprise.connector.mock.MockRepositoryProperty.PropertyType;

public interface IRepositoryProperty{
	public MockRepositoryProperty getmrProperty();

	public static interface IPropertyType extends Comparable{
		PropertyType getmrPropType();
	}
}