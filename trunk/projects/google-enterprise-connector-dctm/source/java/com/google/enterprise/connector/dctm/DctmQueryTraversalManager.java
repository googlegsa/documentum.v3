package com.google.enterprise.connector.dctm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.enterprise.connector.spi.*;
import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfIdentityException;
import com.documentum.fc.client.DfPrincipalException;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

public class DctmQueryTraversalManager implements QueryTraversalManager{
	 /**
	   * Starts (or restarts) traversal from the beginning. This action will return
	   * objects starting from the very oldest, or with the smallest IDs, or
	   * whatever natural order the implementation prefers. The caller may consume
	   * as many or as few of the results as it wants, but it gurantees to call
	   * {@link #checkpoint(PropertyMap)} passing in the past object is has
	   * successfully processed.
	   * 
	   * @return A ResultSet of documents from the repository in natural order
	   * @throws RepositoryException if the Repository is unreachable or similar
	   *           exceptional condition.
	   */
	IDfSession session;
	
	public DctmQueryTraversalManager(){
	}
	
	public DctmQueryTraversalManager(IDfSession session){
		setSession(session);
	}
	
	public IDfSession getSession(){
		return session;
	}
	public void setSession(IDfSession session){
		this.session=session;
	}
	
	private String CreateQuery(){
		String query;
		/*
		for (int i = 0; i < types.length; i++) {
			if (types[i][0].equals("true")) {
				query = "insert into dm_dbo.google_future_data select r_object_id, i_chronicle_id, i_vstamp, r_object_type, r_modify_date from dm_sysobject where r_object_type='"
						+ types[i][1] + "' " + types[i][3];
				execQuery(query, this.session);
			}
		}
		*/
		query="select title, object_name, r_object_id, i_chronicle_id, r_object_type, r_modify_date, r_content_size from dm_sysobject where r_object_type='dm_document' and r_creator_name!='Administrator' order by r_modify_date, r_object_id";
		///select object_name, owner_name, owner_permit, group_name, group_permit, world_permit, acl_domain, acl_name from dm_sysobject where r_object_type='dm_document' and r_creator_name!='Administrator' order by r_modify_date
		return(query);
	}
	
	public static IDfCollection execQuery(String queryString, IDfSession session) {
		IDfCollection col = null; // Collection for the result
		IDfQuery q = new DfQuery(); // Create query object
		q.setDQL(queryString); // Give it the query

		try {
			col = q.execute(session, IDfQuery.DF_READ_QUERY);
			return col;
		} catch (DfException e) {
			System.err.println(" ======================================= ");
			System.err.println("[GSACRAWLER] ERROR DURING THE JOB EXECUTION");
			e.printStackTrace();
			System.err.println(" ======================================= ");
		} catch (NullPointerException e) {
			System.err.println(" ======================================= ");
			System.err.println("[GSACRAWLER] ERROR DURING THE JOB EXECUTION");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.err.println(" ======================================= ");
		}
		return null;
	}
	
	  public ResultSet startTraversal() throws RepositoryException{
		  SimpleResultSet resu= null;
		  String query=null;
		  IDfCollection col = null;
		  Collection c=null;
		  String name=null;
		  String ID=null;
		  String title=null; 
		  String modifDate=null;
		  String crID=null;
		  String mimetype=null;
		  SimplePropertyMap pm=null;
		  SimpleValue vlname=null;
		  SimpleValue vlID=null;
		  SimpleValue vlDate=null;
		  SimpleValue vlDisp=null;
		  SimpleValue vlCont=null;
		  SimpleValue vlMime=null;
		  //long size=0;
		  int size=0;
		  byte[] bufContent;
		  ByteArrayInputStream content=null;
		  
		  IDfSysObject object;
		  query=CreateQuery();
		  col=execQuery(query,session);
		  
		  byte[]buf=null;
		  int count = 0;
		  
		  //LinkedList resuColl=new LinkedList();
		  resu=new SimpleResultSet(); 
		  try{
			  while (col.next()) {
					//entier++;				  	
				  	 title = col.getValue("title").asString();
					 name = col.getValue("object_name").asString();
					 ID = col.getValue("r_object_id").asString();
					 crID = col.getValue("i_chronicle_id").asString();
					 modifDate = col.getValue("r_modify_date").asString();
					 pm=new SimplePropertyMap();
					 /*
					 PROPNAME_DOCNAME
					 PROPNAME_DOCID
					 PROPNAME_LASTMODIFY
					 PROPNAME_CONTENTURL
					 PROPNAME_CONTENT
					 PROPNAME_SECURITYTOKEN
					 PROPNAME_DISPLAYURL
					 PROPNAME_AUTH_VIEWPERMIT
					 */
					 vlname=new SimpleValue(ValueType.STRING,name);
					 //pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_DOCNAME,vlname));
					 vlID=new SimpleValue(ValueType.STRING,crID);
					 pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_DOCID,vlID));
					 vlDate=new SimpleValue(ValueType.DATE,modifDate);
					 
					 //System.out.println("nom vaut "+name+" - ID vaut "+ID+" name vaut "+title+" modifDate vaut "+modifDate+" i_chronicle_id vaut "+crID);
					 //--select * from dm_sysobject,dm_user where world_permit < 5
					 pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_LASTMODIFY,vlDate)); 
					 object = (IDfSysObject)session.getObjectByQualification("dm_document where i_chronicle_id = '" + crID + "'");
					 content=object.getContent();
					 
					 if(content!=null){
					 mimetype=object.getFormat().getMIMEType();
					 System.out.println("nom vaut "+name+" - mimetype "+mimetype);
					 ///test content
					 /*
					 try{
						 buf = new byte[4096];
						 System.out.println("nom vaut "+name+" - ID vaut "+ID+" name vaut "+title+" modifDate vaut "+modifDate+" i_chronicle_id vaut "+crID);
						 
						 while ((count = content.read(buf)) > -1){
						 
						   System.out.write(buf, 0, count);
						 }
						 content.close();
					 }catch(IOException ie){
						 System.out.println(ie.getMessage());
					 }
					 */
					 ///
					 
					 
					 size=new Long(object.getContentSize()).intValue();
					 
					 bufContent = new byte[size];
					 ByteArrayOutputStream output=new ByteArrayOutputStream(); 
					 try{
						 
						 while ((count = content.read(bufContent)) > -1){
						 
							 output.write(bufContent, 0, count);
						 }
						 content.close();
					 }catch(IOException ie){
						 System.out.println(ie.getMessage());
					 }
					 //content.
					 if(bufContent.length>0){
						 vlCont=new SimpleValue(ValueType.BINARY,bufContent);
						 pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_CONTENT,vlCont));
					 }else{
						 vlCont=new SimpleValue(ValueType.BINARY,"");
						 pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_CONTENT,vlCont));
					 }
					 vlMime=new SimpleValue(ValueType.STRING,mimetype);
					 pm.putProperty(new SimpleProperty(SpiConstants.PROPNAME_MIMETYPE,vlMime));
					 
					 }
					 resu.add(pm);
			  }
		  }catch(DfException de){
			  System.err.println("De exception "+de.getMessage());
		  }
		  
		  
		  //c=(Collection)col;
		 //c=(Collection)col;
		 /// resu=new SimpleResultSet(col); 
		  /*
		  int entier = 0;
			int num = 0;
			while (col.next()) {
				entier++;
				num = col.getInt("num");
			}
			col.close();
			//return num;
		  */
		  System.out.println("retour du resultset");
		  return resu;
		  //lancer un select sur la base :
		  
		  
		  
	  }

	  /**
	   * Continues traversal from a supplied checkpoint. The checkPoint parameter
	   * will have been created by a call to the {@link #checkpoint(PropertyMap)}
	   * method. The ResultSet object returns objects from the repository in natural
	   * order starting just after the document that was used to create the
	   * checkpoint string.
	   * 
	   * @param checkPoint String that indicates from where to resume traversal.
	   * @return ResultSet object that returns documents starting just after the
	   *         checkpoint.
	   * @throws RepositoryException
	   */
	  public ResultSet resumeTraversal(String checkPoint)
	      throws RepositoryException{
		  ResultSet resu=null;
		  return resu;
	  }

	  /**
	   * Checkpoints the traversal process. The caller passes in a property map
	   * taken from the {@link ResultSet} object that it obtained from either the
	   * startTraversal or resumeTraversal methods. This property map is the last
	   * document that the caller successfully processed. This is NOT necessarily
	   * the last object from the result set - the caller may consume as much or as
	   * little of a result set as it chooses. If the implementation wants the
	   * caller to persist the traversal state, then it should write a string
	   * representation of that state and return it. If the implementation prefers
	   * to maintain state itself, it should use this call as a signal to commit its
	   * state, up to the document passed in.
	   * 
	   * @param pm A property map obtained from a ResultSet obtained from either
	   *          {@link #startTraversal()} or {link
	   *          {@link #resumeTraversal(String)}.
	   * @return A string that can be used by a subsequent call to the
	   *         {@link #resumeTraversal(String)} method.
	   * @throws RepositoryException
	   */
	  public String checkpoint(PropertyMap pm) throws RepositoryException{
		  String resu=null;
		  return resu;
	  }

	  /**
	   * Sets the preferred batch size. The caller advises the implementation that
	   * the result sets returned by startTraversal or resumeTraversal need not be
	   * larger than this number. The implementation may ignore this call or do its
	   * best to return approximately this number.
	   * 
	   * @param batchHint
	   * @throws RepositoryException
	   */
	  public void setBatchHint(int batchHint) throws RepositoryException{
		  
	  }
	

}
