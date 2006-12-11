package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;

public class IDctmQuery implements IQuery{
	IDfQuery idfQuery;
	
	
	public IDctmQuery(IDfQuery idfQuery){
		this.idfQuery=idfQuery;
	}
	
	public IDctmCollection execute(IDctmSession session, int queryType){
		
		IDfSession idfSession=session.getDfSession();
		IDfCollection DfCollection=null;
		
		try{
			DfCollection=idfQuery.execute(idfSession,queryType);
		}catch(DfException de){
			de.getMessage();
		}
		return new IDctmCollection(DfCollection);
	}
}
