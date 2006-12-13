package com.google.enterprise.connector.dctm.mock.dctmmockwrap;

import com.google.enterprise.connector.dctm.mock.mockwrap.*;
import com.google.enterprise.connector.mock.MockRepositoryEvent.EventType;

public class DctmEventType implements IEventType {
/**MockAccess**/
	EventType mrEventType;
	public EventType getmrEventType(){
		return mrEventType;
	}
/**MockAccess**/
	
/**Constructors**/	
	public DctmEventType(EventType et){
		mrEventType = et;
	}
/**Constructors**/
}