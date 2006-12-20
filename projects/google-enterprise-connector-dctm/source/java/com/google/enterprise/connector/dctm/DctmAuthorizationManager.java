package com.google.enterprise.connector.dctm;
import java.util.List;

import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmFormat;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmId;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.spi.*;
import com.google.enterprise.connector.spi.*;
import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfIdentityException;
import com.documentum.fc.client.DfPrincipalException;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

public class DctmAuthorizationManager implements AuthorizationManager{
	ISession session;
	/**
	 * @param args
	 */
	public DctmAuthorizationManager(){
		
	}
	
	public DctmAuthorizationManager(ISession session){
		setSession(session);
	}
	
	 public ResultSet authorizeDocids(List docidList, String username)
	    throws RepositoryException{
		 int i=0;
		  String objIDEnCours=null;
		  ISysObject objectauto=null;
		  int userPermit=0;
		  boolean result = false;
		  SimpleResultSet docresu=null;
		  DctmPropertyMap docmap=null;
		  
			  for(i=0;i<=docidList.size();i++){
				  objIDEnCours=docidList.get(i).toString();
				  objectauto = session.getObject(new IDctmId(objIDEnCours));
				  userPermit=objectauto.getPermitEx(username);
				  result = userPermit > IDfACL.DF_PERMIT_BROWSE;
				  if (result){
					  docmap=new DctmPropertyMap();
					  docmap.put(SpiConstants.PROPNAME_DOCID,((IDctmSysObject)objectauto).getObjectId());
					  //docmap.put(objectauto.getObjectName());
					  docmap.put(SpiConstants.PROPNAME_CONTENT,((IDctmSysObject)objectauto).getContent());
					  docmap.put(SpiConstants.PROPNAME_CONTENTURL,((IDctmSysObject)objectauto).getObjectId());
					  //docresu.add();
					  //"http://"+tabPartUrl[2]+"/webtop/drl/objectId"+"/"+tabPartUrl[6];
				  }
			  }
		 
			 return docresu;
		 
		 
		 
	 }
	 
	  public ResultSet authorizeTokens(List tokenList, String username)
	    throws RepositoryException{
		  ResultSet responses=null;
			 return responses;
	  }
	  
	  public void setSession(ISession session){
			this.session=session;
	}

}