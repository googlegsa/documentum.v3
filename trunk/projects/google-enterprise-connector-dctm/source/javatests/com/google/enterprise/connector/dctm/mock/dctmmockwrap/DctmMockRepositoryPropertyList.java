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

import com.google.enterprise.connector.dctm.mock.mockwrap.*;
import com.google.enterprise.connector.mock.MockRepositoryPropertyList;
import com.google.enterprise.connector.mock.MockRepositoryProperty;

import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

public class DctmMockRepositoryPropertyList implements IRepositoryPropertyList{
/**MockAccess**/
	private MockRepositoryPropertyList mrPropertyList;	
	public MockRepositoryPropertyList getmrPropertyList(){
		return mrPropertyList;
	}
	public DctmMockRepositoryPropertyList(MockRepositoryPropertyList l) {
		mrPropertyList = l;
	}
/**MockAccess**/

/**Constructors**/	
	public DctmMockRepositoryPropertyList(List l) {
		mrPropertyList = new MockRepositoryPropertyList(l);
	}	
	public DctmMockRepositoryPropertyList(JSONObject jo) {
		mrPropertyList = new MockRepositoryPropertyList(jo);
	}	
	public DctmMockRepositoryPropertyList() {
		mrPropertyList = new MockRepositoryPropertyList();
	}	
	public DctmMockRepositoryPropertyList(IRepositoryProperty[] l) {
		int lstS = l.length;
		MockRepositoryProperty[] mrP = new MockRepositoryProperty[lstS];
		for (int i=0 ; i<lstS  ; i++){
			mrP[i] = l[i].getmrProperty();
		}
		mrPropertyList = new MockRepositoryPropertyList(mrP);
	}
/**Constructors**/
	
	public String toString() {
		return mrPropertyList.toString();
	}
	
	public void setProperty(IRepositoryProperty p) {
		mrPropertyList.setProperty(p.getmrProperty());
	}
	
	public void merge(IRepositoryPropertyList pl) {
		mrPropertyList.merge(pl.getmrPropertyList());
	}
	
	public String lookupStringValue(String name) {
		return mrPropertyList.lookupStringValue(name);
	}
	
	public Iterator iterator() {
		return mrPropertyList.iterator();
	}
	
	public IRepositoryProperty getProperty(String name) {
		return new DctmMockRepositoryProperty((MockRepositoryProperty) mrPropertyList.getProperty(name));
	}
}
