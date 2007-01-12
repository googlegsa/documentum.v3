package com.google.enterprise.connector.dctm;


import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.LoginException;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;


public class DctmConnector implements Connector{
	
	private String login;
	private String password;
	public String docbase;
	public IClient client;
	
	public void setLogin(String login) {
		this.login=login;
	}
	
	public void setPassword(String password) {
		this.password=password;
	}
	
	public void setClient(String client) throws RepositoryException {
		boolean repoExcep = false;
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
			RepositoryException re = new LoginException(message,rootCause);
			re.setStackTrace(stack);
			throw re;
		}
		this.client = cl;
	}
	
	public void setDocbase(String docbase) {
		this.docbase = docbase;
	}
	
	public String getLogin(){
		return login;
	}
	
	public String getPassword(){
		return password;
	}
	
	
	public IClient getClient() {
		return client;
	}
	
	public DctmConnector(){;}
	
	public String getDocbase() {
		return docbase;
	}
	
	public Session login() throws RepositoryException{
		Session sess = null;
		if (!(client==null||login==null||password==null||docbase==null)){
			
			sess = new DctmSession(client,login,password,docbase);
		} else {
			sess = new DctmSession();
		}
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
//	dctmquery.setIDctmSession((IDctmSession)dctmsess);
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
