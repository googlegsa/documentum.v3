package com.google.enterprise.connector.dctm;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

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

public class DctmConnector implements Connector{

	 public Session login() throws LoginException{
		 Session sess=null;
		 return sess;
	 }
	 
	 public Session login(String username, String password, String docbase){
		 Session sess=null;
		 try{
			 sess=new DctmSession(username, password, docbase);
			 
		 }catch(DfAuthenticationException Dae){
			 LoginException Le=new LoginException("erreur d'authentification");
		 }catch(DfServiceException Dse){
			 RepositoryException Re=new RepositoryException("erreur connexion repository");
		 }	  
		 return (sess);
	 }
	
	 public static void main(String[] args){
		 DctmConnector myconn=new DctmConnector();
		 DctmSession sess=(DctmSession)myconn.login("raph","raph","gdoc");
		 IDfSession dctmsess=sess.getSession();
		 SimpleResultSet myResu=null;
		 /* test authentification
		 String dctmID="";
		 String dmclID="";
		 String logtick="";
		 try{
			  dctmID=dctmsess.getSessionId();
			  dmclID=dctmsess.getDMCLSessionId();
			  logtick=dctmsess.getLoginTicket();
		 }catch(DfException de){
			 System.out.println("dfException vaut "+de.getMessage());
		 }
		 System.out.println("dctmID vaut "+dctmID);
		 System.out.println("dmclID vaut "+dmclID);
		 System.out.println("logtick vaut "+logtick);
		 DctmAuthenticationManager dctmauth=(DctmAuthenticationManager)sess.getAuthenticationManager(); 
		 boolean authent=dctmauth.authenticate("raph","raph","gdoc");
		 System.out.println("authentification vaut "+authent);
		 DctmAuthorizationManager dctmauto=(DctmAuthorizationManager)sess.getAuthorizationManager(); 
		 */
		 
		 DctmQueryTraversalManager dctmquery=(DctmQueryTraversalManager)sess.getQueryTraversalManager(); 
		 dctmquery.setSession(dctmsess);
		 try{
			 myResu=(SimpleResultSet)dctmquery.startTraversal();
			 SimplePropertyMap pm=null;
			 SimpleProperty propname=null;
			 SimpleProperty propdate=null;
			 SimpleProperty propID=null;
			 SimpleProperty propcontent=null;
			 SimpleProperty propmimetype=null;
			 String docname=null;
			 String docdate=null;
			 String docId=null;
			 String docmime=null;
			 ByteArrayInputStream doccontent=null;
			 int cpt;
			 byte[]buf=null;
			 int count = 0;
			 ///while(myResu.iterator().hasNext()){
			 for(cpt=0;cpt<myResu.size();cpt++){
				 pm=(SimplePropertyMap)myResu.get(cpt);
				 ///pm=(SimplePropertyMap)myResu.iterator().next();
				 //propname=(SimpleProperty)(pm.getProperty(SpiConstants.PROPNAME_DOCNAME));
				 docname=((SimpleValue)propname.getValue()).getString();
				 propdate=(SimpleProperty)(pm.getProperty(SpiConstants.PROPNAME_LASTMODIFY));
				 docdate=((SimpleValue)propdate.getValue()).getString();
				 propID=(SimpleProperty)(pm.getProperty(SpiConstants.PROPNAME_DOCID));
				 docId=((SimpleValue)propID.getValue()).getString();
				 propcontent=(SimpleProperty)(pm.getProperty(SpiConstants.PROPNAME_CONTENT));
				 //doctaille=((SimpleValue)propcontent.getValue()).
				 
				 if(propcontent!=null){
				propmimetype=(SimpleProperty)(pm.getProperty(SpiConstants.PROPNAME_MIMETYPE));
				 docmime=((SimpleValue)propmimetype.getValue()).getString();
				 
				 
				 System.out.println("nom vaut "+docname+" - ID vaut "+docId+" modifDate vaut "+docdate);
				 
				 doccontent=(ByteArrayInputStream)((SimpleValue)propcontent.getValue()).getStream();
				 
				 /*
				 try{
				 
				  test parcours du contenu
				 buf = new byte[4096];
				 
				 while ((count = doccontent.read(buf)) > -1){
				   System.out.write(buf, 0, count);
				 }
				 doccontent.close();
				 
				 }catch(IOException ie){
					 System.out.println(ie.getMessage());
				 }
				 */
				 }
			 //While(myResu.Iterator().hasNext()){
			 }
			 
		 }catch(RepositoryException Re){
			 System.out.println("Re vaut "+Re.getMessage());
		 }
	 }
	 
}
