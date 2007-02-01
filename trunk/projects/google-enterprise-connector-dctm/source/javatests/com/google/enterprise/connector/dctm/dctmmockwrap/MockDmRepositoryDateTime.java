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

package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.mock.MockRepositoryDateTime;

public class MockDmRepositoryDateTime implements ITime {
	//Let s emphasize ticks are a milli representation of a time
	// and that tocks==0 <=> Thu, 01 Jan 1970 00:00:00 GMT

	private MockRepositoryDateTime mrDT;
	
	protected MockDmRepositoryDateTime(MockRepositoryDateTime dt) {
		this.mrDT = dt;
	}

	public String asString(String pattern) {
		Date tmp = new Date(mrDT.getTicks());
		SimpleDateFormat localDate = new SimpleDateFormat(pattern);
		return localDate.format(tmp);
	}

	public Date getDate() {
		return new Date(mrDT.getTicks());
	}

}
