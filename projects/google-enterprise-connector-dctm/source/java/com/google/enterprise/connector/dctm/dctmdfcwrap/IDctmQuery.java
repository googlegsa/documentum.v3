package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.common.DfException;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;

public class IDctmQuery implements IQuery{
	IDfQuery idfQuery;
	public static int DF_READ_QUERY = IDfQuery.DF_READ_QUERY; 
	
	public IDctmQuery(IDfQuery idfQuery){
		this.idfQuery=idfQuery;
	}
	
	public IDctmQuery(){
		this.idfQuery=new DfQuery();
	}
	
	public ICollection execute(ISession session, int queryType){	
		if (!(session instanceof IDctmSession)) {
			throw new IllegalArgumentException();
		}
		IDctmSession idctmsession = (IDctmSession) session;
		IDfSession idfSession=idctmsession.getDfSession();
		IDfCollection DfCollection=null;
		
		try{
			System.out.println("Querytype vaut "+queryType);
			DfCollection=idfQuery.execute(idfSession,queryType);
		}catch(DfException de){
			System.out.println(de.getMessage());
		}
		return new IDctmCollection(DfCollection);
	}
	
	public void setDQL(String dqlStatement){
		idfQuery.setDQL(dqlStatement);
	}
	
	public int getDF_READ_QUERY() {
		return DF_READ_QUERY;
	}

	public void setDF_READ_QUERY(int df_read_query){
		DF_READ_QUERY = df_read_query;
	}

}
