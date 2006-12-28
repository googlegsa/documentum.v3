package com.google.enterprise.connector.dctm;

import java.util.List;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.spi.*;


public class DctmAuthorizationManager implements AuthorizationManager{
	ISession session;
	IClient client;
	/**
	 * @param args
	 */
	public DctmAuthorizationManager(){
		
	}
	
	public DctmAuthorizationManager(ISession session, IClient client){
		setSession(session);
		setClient(client);
	}
	
		
	public ResultSet authorizeDocids(List docidList, String username)
	throws RepositoryException{
		int i=0;
		DctmResultSet resultSet = null;
		DctmPropertyMap docmap = null;
		IQuery query = this.getClient().getQuery();
		String dqlQuery ="select r_object_id from dm_document where r_object_id in (";
		ICollection collec = null;
		
		
		String ticket = session.getLoginTicketForUser(username);
		ILoginInfo logInfo = new IDctmLoginInfo();
		logInfo.setUser(username);
		logInfo.setPassword(ticket);
		ISession sessionUser = client.newSession(session.getDocbaseName(),logInfo);
		
		
		for(i=0;i<docidList.size()-1;i++){
			dqlQuery += "'" + docidList.get(i).toString() + "', ";
			
		}
		dqlQuery += "'" + docidList.get(i).toString() + "')";
		query.setDQL(dqlQuery);
		collec = (ICollection) query.execute(sessionUser,IQuery.DF_READ_QUERY);
		String ids = "";
		while(collec!=null && collec.next()){
			ids += collec.getString("r_object_id") + " ";
		}
		resultSet = new DctmResultSet();
		for(i = 0; i < docidList.size() ; i++){
			System.out.println("index " + i);
			docmap = new DctmPropertyMap();
			docmap.putProperty(new DctmProperty("PROPNAME_DOCID",docidList.get(i).toString()));
			docmap.putProperty(new DctmProperty("PROPNAME_AUTH_VIEWPERMIT", (ids.indexOf(docidList.get(i).toString()) != -1)));
			resultSet.add(docmap);
		}
		return resultSet;
		
		
		
	}
	
	public ResultSet authorizeTokens(List tokenList, String username)
	throws RepositoryException{
		ResultSet responses=null;
		return responses;
	}
	
	public void setSession(ISession session){
		this.session=session;
	}

	public IClient getClient() {
		return client;
	}

	public void setClient(IClient client) {
		this.client = client;
	}
	
}
