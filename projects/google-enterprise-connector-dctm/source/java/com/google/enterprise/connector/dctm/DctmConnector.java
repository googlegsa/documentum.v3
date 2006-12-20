package com.google.enterprise.connector.dctm;

import java.io.ByteArrayInputStream;

import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfSession;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSession;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;
import com.google.enterprise.connector.spi.SimpleProperty;
import com.google.enterprise.connector.spi.SimplePropertyMap;
import com.google.enterprise.connector.spi.SimpleResultSet;
import com.google.enterprise.connector.spi.SimpleValue;
import com.google.enterprise.connector.spi.SpiConstants;

public class DctmConnector implements Connector{
	public Session login() throws LoginException{
		Session sess=null;
		sess=new DctmSession();
		return (sess);
	 }
	
	 public static void main(String[] args){
		 DctmConnector myconn=new DctmConnector();
		 DctmSession sess=null;
		 ISession dctmsess=null;
		 SimpleResultSet myResu=null;
		 DctmQueryTraversalManager dctmquery=null;
		 try{
		 sess=(DctmSession)myconn.login();
		 }catch(LoginException le){
			 le.getMessage();
		 }	 
		
		 dctmquery=(DctmQueryTraversalManager)sess.getQueryTraversalManager(); 
		 dctmsess=sess.getISession();
		 dctmquery.setIDctmSession((IDctmSession)dctmsess);
		 
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
