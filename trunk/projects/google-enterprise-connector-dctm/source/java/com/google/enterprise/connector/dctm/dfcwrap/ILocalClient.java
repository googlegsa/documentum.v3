package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSessionManager;

public interface ILocalClient {
	public IDctmSessionManager newSessionManager();
}
