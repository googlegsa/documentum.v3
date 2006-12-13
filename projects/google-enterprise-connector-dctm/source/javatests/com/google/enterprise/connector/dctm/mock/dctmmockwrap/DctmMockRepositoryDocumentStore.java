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
import com.google.enterprise.connector.mock.*;

import java.util.Iterator;
import java.util.List;

public class DctmMockRepositoryDocumentStore implements IRepositoryDocumentStore {
/**MockAccess**/
	private MockRepositoryDocumentStore mockRDS;
	public DctmMockRepositoryDocumentStore(MockRepositoryDocumentStore mockRDSInstance) {
		mockRDS = mockRDSInstance;
	}
/**MockAccess**/

/**Constructors**/
	public DctmMockRepositoryDocumentStore() {
		mockRDS = new MockRepositoryDocumentStore();
	}
/**Constructors**/
	
	public void reinit() {
		mockRDS.reinit();
	}
	
	public void applyEvent(IRepositoryEvent event) {
		mockRDS.applyEvent((MockRepositoryEvent) event);
	}

	public MockRepositoryDocument getDocByID(IId docid) {
		return mockRDS.getDocByID(docid.getValue());
	}

	public Iterator iterator() {
		return mockRDS.iterator();
	}
	
	public int size() {
		return mockRDS.size();
	}

	public List dateRange(final IRepositoryDateTime from, final IRepositoryDateTime to) {
		return mockRDS.dateRange(from.getmrDateTime() ,
				to.getmrDateTime());
	}

	public List dateRange(final IRepositoryDateTime from) {
		return mockRDS.dateRange(from.getmrDateTime());
	}
}
