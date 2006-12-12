package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;

public class IDctmQuery implements IQuery{
	IDfQuery idfQuery;
	
	
	public IDctmQuery(IDfQuery idfQuery){
		this.idfQuery=idfQuery;
	}
	
	public ICollection execute(ISession session, int queryType){	
		if (!(session instanceof IDctmSession)) {
			throw new IllegalArgumentException();
		}
		IDctmSession idctmsession = (IDctmSession) session;
		IDfSession idfSession=idctmsession.getDfSession();
		IDfCollection DfCollection=null;
		
		try{
			DfCollection=idfQuery.execute(idfSession,queryType);
		}catch(DfException de){
			de.getMessage();
		}
		return new IDctmCollection(DfCollection);
	}
}