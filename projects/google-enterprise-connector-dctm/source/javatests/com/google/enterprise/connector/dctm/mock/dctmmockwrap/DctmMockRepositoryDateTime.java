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
import com.google.enterprise.connector.mock.MockRepositoryDateTime;
/**
 * Mock time object to replace Calendar in unit tests.
 * <p>
 * Implemented with a single int "ticks"
 */
public class DctmMockRepositoryDateTime implements IRepositoryDateTime {
	private MockRepositoryDateTime mrDT;
	public String toString() {
		if (mrDT==null) return null;
		return Integer.toString(getTicks());
	}
	
	public DctmMockRepositoryDateTime(int ticks) {
		mrDT = new MockRepositoryDateTime(ticks);
	}
	
	public int getTicks() {
		return mrDT.getTicks();
	}
	
	public int compareTo(Object o) {
		/*DctmMockRepositoryDateTime t = (DctmMockRepositoryDateTime) o;
		return this.getTicks() - t.getTicks();*/
		return 0;
	}
	
	/*public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DctmMockRepositoryDateTime)) {
			return false;
		}
		return (compareTo(obj) == 0);
	}
	
	public int hashCode() {
		long lticks = ticks;
		return (int)(lticks ^ (lticks >>> 32));  // See Bloch, EJ, p. 38
	}*/
}
