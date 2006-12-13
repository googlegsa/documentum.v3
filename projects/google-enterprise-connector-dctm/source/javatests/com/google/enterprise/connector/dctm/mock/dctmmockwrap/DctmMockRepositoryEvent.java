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

import java.util.Map;

import com.google.enterprise.connector.dctm.mock.mockwrap.*;
import com.google.enterprise.connector.mock.MockRepositoryEvent;
import com.google.enterprise.connector.mock.MockRepositoryDateTime;

public class DctmMockRepositoryEvent implements IRepositoryEvent {
/**MockAccess**/
	private MockRepositoryEvent mrEvent;
/**MockAccess**/
	
/**Constructors**/
	public DctmMockRepositoryEvent(IEventType type, 
			IId docID, 
			String content,
			IRepositoryPropertyList propertyList, 
			IRepositoryDateTime timeStamp) {
		mrEvent = new MockRepositoryEvent(type.getmrEventType(), 
				docID.getValue(), 
				content,
				propertyList.getmrPropertyList(), 
				new MockRepositoryDateTime(timeStamp.getTicks()));
	}	
	public DctmMockRepositoryEvent(Map params) {
		mrEvent = new MockRepositoryEvent(params);
	}
/**Constructors**/
	
	public String toString() {
		return mrEvent.toString();
	}	
	
	public String getContent() {
		return mrEvent.getContent();
	}
	
	public IId getDocID() {
		return new DctmMockId(mrEvent.getDocID());
	}
	
	public IRepositoryPropertyList getPropertyList() {
		return new DctmMockRepositoryPropertyList(mrEvent.getPropertyList());
	}
	
	public IEventType getType() {
		return new DctmEventType(mrEvent.getType());
	}
	
	public IRepositoryDateTime getTimeStamp() {
		return new DctmMockRepositoryDateTime(mrEvent.getTimeStamp().getTicks());
	}
}
