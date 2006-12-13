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
import com.google.enterprise.connector.mock.MockRepositoryEventList;
import java.util.List;

public class DctmMockRepositoryEventList implements IRepositoryEventList {
/**MockAccess**/
	private MockRepositoryEventList mrEventList;	
	public MockRepositoryEventList getmrEventList(){
		return mrEventList;
	}
/**MockAccess**/	

/**Constructors**/
	public DctmMockRepositoryEventList() {
		mrEventList = new MockRepositoryEventList();
	}	
	public DctmMockRepositoryEventList(String filename) {
		mrEventList = new MockRepositoryEventList(filename);
	}
/**Constructors**/
	
	public void setRepositoryFileName(String repositoryFileName) {
		mrEventList.setRepositoryFileName(repositoryFileName);
	}
	
	public void setWorkDirName(String workDirName) {
		mrEventList.setWorkDirName(workDirName);
	}
	
	public List getEventList() {
		return mrEventList.getEventList();
	}
	
}
