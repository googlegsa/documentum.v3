package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;


public class DctmConnector implements Connector{
	
	private String login;
	private String password;
	private String docbase;
	private String client;
	private String queryStringUnboundedDefault;
	private String queryStringBoundedDefault;
	private String queryStringAuthoriseDefault;
	private String attributeName;
	private String webtopServerUrl;
	/**
	 * Setters and getters for the data retrieved from Spring
	 */	
	public void setLogin(String login) {this.login=login;}
	public String getLogin(){return login;}
	
	public void setPassword(String password) {this.password=password;}
	public String getPassword(){return password;}
	
	public void setDocbase(String docbase) {this.docbase = docbase;}
	public String getDocbase() {return docbase;}
	
	public void setQueryStringUnboundedDefault(String qsud) {this.queryStringUnboundedDefault = qsud;}
	public String getQueryStringUnboundedDefault() {return queryStringUnboundedDefault;}
	
	public void setQueryStringBoundedDefault(String qsbd) {this.queryStringBoundedDefault = qsbd;}
	public String getQueryStringBoundedDefault() {return queryStringBoundedDefault;}
	
	public void setQueryStringAuthoriseDefault(String qsad) {this.queryStringAuthoriseDefault = qsad;}
	public String getQueryStringAuthoriseDefault() {return queryStringAuthoriseDefault;}
	
	public void setWebtopServerUrl(String wsu) {this.webtopServerUrl = wsu;}
	public String getWebtopServerUrl() {return webtopServerUrl;}
	
	public void setAttributeName(String an) {this.attributeName = an;}
	public String getAttributeName() {return attributeName;}
	
	public void setClient(String client) /*throws RepositoryException*/ {
		/*boolean repoExcep = false;
		Throwable rootCause=null;
		String message="";
		StackTraceElement[] stack = null;
		IClient cl = null;
		try {
			cl = (IClient) Class.forName(client).newInstance();
		} catch (InstantiationException e) {
			repoExcep=true;
			rootCause=e.getCause();
			message=e.getMessage();
			stack=e.getStackTrace();
		} catch (IllegalAccessException e) {
			repoExcep=true;
			rootCause=e.getCause();
			message=e.getMessage();
			stack=e.getStackTrace();
		} catch (ClassNotFoundException e) {
			repoExcep=true;
			rootCause=e.getCause();
			message=e.getMessage();
			stack=e.getStackTrace();
		}
		if (repoExcep) {
			RepositoryException re = new RepositoryException(message,rootCause);
			re.setStackTrace(stack);
			throw re;
		}*/
		this.client = client;
	}	
	public String getClient() {return client/*.getClass().getName()*/;}
	
	public DctmConnector(){;}
	
	public Session login() throws RepositoryException{
		System.out.println("--- DctmConnector login ---");
		if (DebugFinalData.debug) {
			OutputPerformances.setPerfFlag(this,"DctmConnector.login() :\n\t\t\t\t Instantiates a new DctmSession from 9 String (~250 chars) and :");
		}
		Session sess = null;
		
		if (!(client==null||login==null||password==null||docbase==null)){
			
			sess = new DctmSession(client,login,password,docbase,
					queryStringUnboundedDefault,
					queryStringBoundedDefault,
					queryStringAuthoriseDefault,
					attributeName,
					webtopServerUrl);
		} else {
			sess = new DctmSession();
		}
		if (DebugFinalData.debug) OutputPerformances.endFlag(this,"return Session from DctmConnector.login()");
		return (sess);
	}
	
	
//	public static void main(String[] args){
//	DctmConnector myconn=new DctmConnector();
//	DctmSession sess=null;
//	ISession dctmsess=null;
//	SimpleResultSet myResu=null;
//	DctmQueryTraversalManager dctmquery=null;
//	try{
//	sess=(DctmSession)myconn.login();
//	}catch(LoginException le){
//	le.getMessage();
//	}	 
//	
//	dctmquery=(DctmQueryTraversalManager)sess.getQueryTraversalManager(); 
//	dctmsess=sess.getISession();
//	dctmquery.setIDctmSession((DmSession)dctmsess);
//	
//	try{
//	myResu=(SimpleResultSet)dctmquery.startTraversal();
//	SimplePropertyMap pm=null;
//	SimpleProperty propname=null;
//	SimpleProperty propdate=null;
//	SimpleProperty propID=null;
//	SimpleProperty propcontent=null;
//	SimpleProperty propmimetype=null;
//	String docname=null;
//	String docdate=null;
//	String docId=null;
//	String docmime=null;
//	ByteArrayInputStream doccontent=null;
//	int cpt;
//	byte[]buf=null;
//	int count = 0;
//	///while(myResu.iterator().hasNext()){
//	for(cpt=0;cpt<myResu.size();cpt++){
//	pm=(SimplePropertyMap)myResu.get(cpt);
//	///pm=(SimplePropertyMap)myResu.iterator().next();
//	//propname=(SimpleProperty)(pm.getProperty(SpiConstants.PROPNAME_DOCNAME));
//	docname=((SimpleValue)propname.getValue()).getString();
//	propdate=(SimpleProperty)(pm.getProperty(SpiConstants.PROPNAME_LASTMODIFY));
//	docdate=((SimpleValue)propdate.getValue()).getString();
//	propID=(SimpleProperty)(pm.getProperty(SpiConstants.PROPNAME_DOCID));
//	docId=((SimpleValue)propID.getValue()).getString();
//	propcontent=(SimpleProperty)(pm.getProperty(SpiConstants.PROPNAME_CONTENT));
//	//doctaille=((SimpleValue)propcontent.getValue()).
//	
//	if(propcontent!=null){
//	propmimetype=(SimpleProperty)(pm.getProperty(SpiConstants.PROPNAME_MIMETYPE));
//	docmime=((SimpleValue)propmimetype.getValue()).getString();
//	
//	
//	System.out.println("nom vaut "+docname+" - ID vaut "+docId+" modifDate vaut "+docdate);
//	
//	doccontent=(ByteArrayInputStream)((SimpleValue)propcontent.getValue()).getStream();
//	
//	/*
//	try{
//	
//	test parcours du contenu
//	buf = new byte[4096];
//	
//	while ((count = doccontent.read(buf)) > -1){
//	System.out.write(buf, 0, count);
//	}
//	doccontent.close();
//	
//	}catch(IOException ie){
//	System.out.println(ie.getMessage());
//	}
//	*/
//	}
//	//While(myResu.Iterator().hasNext()){
//	}
//	
//	}catch(RepositoryException Re){
//	System.out.println("Re vaut "+Re.getMessage());
//	}
//	
//	}	 
}
