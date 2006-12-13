//Copyright (C) 2006 Google Inc.
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

package com.google.enterprise.connector.dctm.mock.dctmmockwrap;

import com.google.enterprise.connector.mock.MockRepositoryProperty;
import com.google.enterprise.connector.dctm.mock.mockwrap.IRepositoryProperty;
import com.google.enterprise.connector.mock.MockRepositoryProperty.*;

public class DctmMockRepositoryProperty implements IRepositoryProperty {
/**MockAccess**/
	private MockRepositoryProperty mrProperty;	
	public MockRepositoryProperty getmrProperty(){
		return mrProperty;
	}
/**MockAccess**/

/**Constructors**/
	public DctmMockRepositoryProperty(String name, IPropertyType type, String value) {
		mrProperty = new MockRepositoryProperty(name, type.getmrPropType(), value);
	}	
	public DctmMockRepositoryProperty(String name, Object o) {
		mrProperty = new MockRepositoryProperty(name, o);
	}	
	public DctmMockRepositoryProperty(MockRepositoryProperty mrP) {
		mrProperty = mrP;
	}
/**Constructors**/
	
	public static class DctmPropertyType implements IPropertyType {
		PropertyType mrProp_Type;
		public PropertyType getmrPropType(){
			return mrProp_Type;
		}
		public int compareTo(Object o){
			return mrProp_Type.compareTo(o);
		}
	}
	
	public String toString() {
		return mrProperty.toString();
	}
	
	public String getName() {
		return mrProperty.getName();
	}
	
	public PropertyType getType() {
		return mrProperty.getType();
	}
	
	public String getValue() {
		return mrProperty.getValue();
	}
	
	public String[] getValues() {
		return mrProperty.getValues();
	}
	
	public boolean isRepeating() {
		return mrProperty.isRepeating();
	}
	
}
