package com.google.enterprise.connector.dctm;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IDocbaseMap;
import com.google.enterprise.connector.dctm.dfcwrap.ILoginInfo;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISessionManager;
import com.google.enterprise.connector.dctm.dfcwrap.IType;
import com.google.enterprise.connector.instantiator.EncryptedPropertyPlaceholderConfigurer;
import com.google.enterprise.connector.instantiator.InstanceInfo;
import com.google.enterprise.connector.manager.ConnectorStatus;
import com.google.enterprise.connector.manager.Context;
import com.google.enterprise.connector.manager.Manager;
import com.google.enterprise.connector.manager.ProductionManager;
import com.google.enterprise.connector.spi.ConfigureResponse;
import com.google.enterprise.connector.spi.ConnectorFactory;
import com.google.enterprise.connector.spi.ConnectorType;
import com.google.enterprise.connector.spi.RepositoryException;

public class DctmConnectorType implements ConnectorType {

	private static final String HIDDEN = "hidden";

	private static final String VALUE = "value";

	private static final String NAME = "name";

	private static final String TEXT = "text";

	private static final String TYPE = "type";

	private static final String INPUT = "input";

	private static final String CLOSE_ELEMENT = "/>";

	private static final String OPEN_ELEMENT = "<";

	private static final String TR_END = "</tr>\r\n";

	private static final String TD_END = "</td>\r\n";

	private static final String TD_START = "<td>";
	
	private static final String TD_START_COL2 = "<td colspan=\"2\">";

	private static final String TR_START = "<tr>\r\n";

	private static final String SELECT_START = "<select";

	private static final String SELECT_END = "</select>\r\n";

	private static final String TEXTAREA_START = "<textarea";

	private static final String TEXTAREA_END = "</textarea>\r\n";

	private static final String DCTMCLASS = "clientX";

	private static final String AUTHENTICATIONTYPE = "authentication_type";

	private static final String ISPUBLIC = "is_public";

	private static final String WHERECLAUSE = "where_clause";
	
	private static final String ADVANCEDCONF = "advanced_configuration";
	
	private static final String ACTIONUPDATE = "action_update";

	private static final String DOCBASENAME = "docbase";

	private static final String DISPLAYURL = "webtop_display_url";

	private static final String INCLUDED_META = "included_meta";

	private static final String INCLUDED_OBJECT_TYPE = "included_object_type";

	private static final String ROOT_OBJECT_TYPE = "root_object_type";
	
	private static final String LOGIN = "login";
	
	private static final String PASSWORD = "Password";

	private static final String CHECKBOX = "CHECKBOX";

	private static final String CHECKED = "CHECKED";
	
	private static final String ID = "ID";
	
	private IClient client;
	
	private IClientX cl = null;

	private ISession sess = null;
	
	private ISessionManager sessMag = null;

	private List keys = null;

	private Set keySet = null;

	private String initialConfigForm = null;
	
	private HashSet included_object_type = null;
	
	private HashSet included_meta = null;
	
	private String root_object_type = null;
	
	private String authentication_type = null;
	
	private String advanced_configuration = null;
	
	private String clientX = null;
	
	private static Logger logger = Logger.getLogger(DctmConnectorType.class
			.getName());

	ResourceBundle resource;

	/**
	 * Set the keys that are required for configuration. One of the overloadings
	 * of this method must be called exactly once before the SPI methods are
	 * used.
	 * 
	 * @param keys
	 *            A list of String keys
	 */
	public void setConfigKeys(List keys) {
		logger.log(Level.INFO, "setConfigKeys List");
		if (this.keys != null) {
			throw new IllegalStateException();
		}
		this.keys = keys;
		this.keySet = new HashSet(keys);

	}

	/**
	 * Set the keys that are required for configuration. One of the overloadings
	 * of this method must be called exactly once before the SPI methods are
	 * used.
	 * 
	 * @param keys
	 *            An array of String keys
	 */
	public void setConfigKeys(String[] keys) {
		logger.log(Level.INFO, "setConfigKeys keys");
		setConfigKeys(Arrays.asList(keys));
	}
	
	public void setIncluded_meta(HashSet included_meta) {
		this.included_meta=included_meta;
		logger.log(Level.INFO, "included_meta set to " +included_meta);
	}
	
	public void setIncluded_object_type(HashSet included_object_type) {
		this.included_object_type=included_object_type;
		logger.log(Level.INFO, "included_object_type set to " +included_object_type);
	}
	
	public HashSet getIncluded_meta() {
		
		return included_meta;
	}
	
	public HashSet getIncluded_object_type() {

		return included_object_type;
	}
	
	public void setRoot_object_type(String root_object_type) {
		this.root_object_type=root_object_type;
		logger.log(Level.INFO, "root_object_type set to " +root_object_type);
	}
	
	public String getRoot_object_type() {

		return root_object_type;
	}

	public ConfigureResponse getConfigForm(Locale language) {

		try {
			resource = ResourceBundle.getBundle("DctmConnectorResources",
					language);
		} catch (MissingResourceException e) {
			resource = ResourceBundle.getBundle("DctmConnectorResources");
		}
		if (initialConfigForm != null) {
			return new ConfigureResponse("", initialConfigForm);
		}
		if (keys == null) {
			throw new IllegalStateException();
		}
		
		this.initialConfigForm = makeValidatedForm(null);

		return new ConfigureResponse("", initialConfigForm);
	}

	public ConfigureResponse validateConfig(Map configData, Locale language,
			ConnectorFactory connectorFactory) {
		
		sess= null; 
		
		sessMag= null;
		
		
		
		try {
			resource = ResourceBundle.getBundle("DctmConnectorResources",
					language);
			Context.getInstance().getCommonDirPath();
		} catch (MissingResourceException e) {
			resource = ResourceBundle.getBundle("DctmConnectorResources");
		}
		String form = null;
		
		Resource res = new ClassPathResource("config/connectorInstance.xml");
		XmlBeanFactory factory = new XmlBeanFactory(res);
		
		Properties p = new Properties();
		p.putAll(configData);
		
		PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
		cfg.setProperties(p);
		cfg.postProcessBeanFactory(factory);
		
	
		
		String validation = validateConfigMap(configData);
		String additionalWhereClause = null;
		boolean where_clause_config=false;
		int status = 0;
		if (validation.equals("")) {
			try {
				
				logger.info("LOGIN is "+configData.get(LOGIN));
				logger.info("PASSWORD is "+configData.get(PASSWORD));
				logger.info("ADVANCEDCONF is "+configData.get(ADVANCEDCONF));
				logger.info("WHERECLAUSE is "+configData.get(WHERECLAUSE));
				logger.info("INCLUDED_OBJECT_TYPE is "+configData.get(INCLUDED_OBJECT_TYPE));
				logger.info("INCLUDED_META is "+configData.get(INCLUDED_META));
				logger.info("ROOT_OBJECT_TYPE is "+configData.get(ROOT_OBJECT_TYPE));
				logger.info("ACTIONUPDATE is "+configData.get(ACTIONUPDATE));
				
				setSessionManager(configData);
				
				
				ILoginInfo myinfo=sessMag.getIdentity((String)configData.get(DOCBASENAME));
				String pass=myinfo.getPassword();
				String user=myinfo.getUser();
				logger.info("login user : "+user);
				logger.info("login pass : "+pass);
				
				
				setSession(configData);
				
				

				logger.log(Level.INFO, "test connection to the repository");
				

				String isPublic = (String) configData.get(ISPUBLIC);
				if (isPublic == null) { 
					configData.put(isPublic,"false");
				}
				
				
				
				logger.info("sess before testwebtop:"+sess);
				status=testWebtopUrl((String) configData.get(DISPLAYURL));
				logger.info("sess after testwebtop:"+sess);

				
//				display the form again when the advanced conf checkbox is checked and before that the user had saved the configuration
				if((((String)configData.get(ADVANCEDCONF)).equals("on") && (((String)configData.get(ACTIONUPDATE)).equals("checkadvconf")||
						((String)configData.get(ACTIONUPDATE)).equals("addmeta")))||((String)configData.get(ACTIONUPDATE)).equals("uncheckadvconf")){ 
					
					logger.info("CASE ADVANCEDCONF SET TO on or ACTIONUPDATE set to uncheckadvconf");
					
					
					form = makeValidatedForm(configData);
					

					return new ConfigureResponse("", form);
					
				}
				
				
				if ((String) configData.get(WHERECLAUSE) != null
						&& !((String) configData.get(WHERECLAUSE)).equals("") ) {

						///qtm = (DctmTraversalManager)((DctmSession)sess).getTraversalManager();
						
	
						///String additionalWhereClause= checkAdditionalWhereClause((String) configData.get("where_clause"), qtm);
					 	where_clause_config = true;
						additionalWhereClause= checkAdditionalWhereClause((String) configData.get("where_clause"));
					
					
						configData.put("where_clause",additionalWhereClause);
						configData.put((ACTIONUPDATE),"checkadvconf");
						logger.info("where_clause is now "+additionalWhereClause);
						
						
						p.putAll(configData);
						cfg.setProperties(p);
						logger.info("after set properties : where_clause of configData is now "+configData.get("where_clause"));
					
						
					///} catch (RepositoryException e) {
						//return the config form with an error message (written in red)
						///return createErrorMessage(configData, e);
					///}
					
					}
				
				
			
				
			} catch (RepositoryException e) {
				
				if (where_clause_config == false||(where_clause_config == true && additionalWhereClause != null)&&(status==200)){
					//uncheck the advanced configuration checkbox except if the 
					configData.put(ADVANCEDCONF,"off");
					logger.info("ADVANCEDCONF set to "+configData.get(ADVANCEDCONF));
				}
				
				
				logger.info("RepositoryException thrown in validateconfig "+e.getMessage());
//				return the config form with an error message (written in red)
				return createErrorMessage(configData, e);
			}
			
			
			
		logger.info("sess before return null"+sess);	
		return null;
		
		
		}

//		return the config form with an error message (written in red) indicating the name of the missing parameter
		logger.info("if validation returns smthg");
		logger.info("sess :"+sess);
		configData.put(ADVANCEDCONF,"off");
		
		form = makeValidatedForm(configData);
		
		return new ConfigureResponse(resource.getString(validation + "_error"),
				form);
		
	}

	public ConfigureResponse getPopulatedConfigForm(Map configMap,
			Locale language) {
		logger.info("getConfigForm");
			
		
		for (Iterator i = keys.iterator(); i.hasNext();) {
			String key = (String) i.next();
			String val = (String) configMap.get(key);
			if(key.equals("advanced_configuration")&&val==null){
				val=getAdvanced_configuration();
				configMap.put(key,val);
			}else if(key.equals("clientX")&&(val==null||val.equals(""))){
				val=getClientX();
				configMap.put(key,val);
			}else if(key.equals("authentication_type")&&(val==null||val.equals(""))){
				val=getAuthentication_type();
				configMap.put(key,val);
			}else if(key.equals("root_object_type")&&val==null){
				val=getRoot_object_type();
				configMap.put(key,val);
			}
			
			logger.info("in getConfigForm before spring process key "+key+" is "+val);
		}
		
		
		try {
			resource = ResourceBundle.getBundle("DctmConnectorResources",
					language);
		} catch (MissingResourceException e) {
			resource = ResourceBundle.getBundle("DctmConnectorResources");
		}
		
		
		ConfigureResponse result = new ConfigureResponse("",
				makeValidatedForm(configMap));
		return result;
	}

	private ConfigureResponse createErrorMessage(Map configData,
			RepositoryException e) {
		String form;
		String message = e.getMessage();
		String returnMessage = null;
		String extractErrorMessage = null;
		String bundleMessage = null;
		if (message.indexOf("[") != -1) {
			extractErrorMessage = message.substring(message.indexOf("[") + 1,
					message.indexOf("]"));
		} else {
			extractErrorMessage = e.getCause().getClass().getName();
		}
		try {
			bundleMessage = resource.getString(extractErrorMessage);
		} catch (MissingResourceException mre) {
			bundleMessage = resource.getString("DEFAULT_ERROR_MESSAGE") + " "
			+ e.getMessage();
		}
		returnMessage = "<p><font color=\"#FF0000\">" + bundleMessage
		+ "</font></p>";
		logger.log(Level.WARNING, returnMessage);

		form = makeValidatedForm(configData);
		return new ConfigureResponse(returnMessage, returnMessage + "<br>"
				+ form);
	}

	private String checkAdditionalWhereClause(String additionalWhereClause) throws RepositoryException {

		ICollection collec = null;
		int counter = 0;
		
		try{
			logger.info("check additional where clause : " + additionalWhereClause);
	
			IQuery query = cl.getQuery();
			String dql="select r_object_id from dm_sysobject where r_object_type='dm_document' ";
			if (!additionalWhereClause.toLowerCase().startsWith("and ")) {
				query
				.setDQL(dql+"and "
						+ additionalWhereClause);
			}else{
				query
				.setDQL(dql+ additionalWhereClause);
			}
			
			collec = query.execute(sessMag, IQuery.EXECUTE_READ_QUERY);
				
			
			while (collec.next()) {
				counter++;
				break;
			}
			
		}catch (RepositoryException re){
			logger.info("throw "+re);
			throw new RepositoryException (re);
		}finally{
			try{
				if(collec!=null){
					if (collec.getState() != ICollection.DF_CLOSED_STATE){
						collec.close();
						logger.info("after closing the collection");
						sessMag.release(collec.getSession());
						logger.info("after releasing the session");
					}	
				}	
			}catch (RepositoryException re){
				throw new RepositoryException (re);
			}
			
		}
		
		if (counter == 0) {
			throw new RepositoryException("[additionalTooRestrictive]");
		}
		
		return additionalWhereClause;

	}

	private int testWebtopUrl(String webtopServerUrl)
	throws RepositoryException {
		logger.log(Level.INFO, "test connection to the webtop server : "
				+ webtopServerUrl);
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod(webtopServerUrl);
		int status = 0;
		try {
			status = client.executeMethod(getMethod);
			
			
			if (status != 200) {

				logger.log(Level.INFO, "status " + status);

				throw new RepositoryException(
						"[status] Http request returned a " + status
						+ " status");
			}
		} catch (HttpException e) {
			RepositoryException re = new RepositoryException("[HttpException]",
					e);
			throw new RepositoryException(re);
		} catch (IOException e) {
			RepositoryException re = new RepositoryException("[IOException]", e);
			throw new RepositoryException(re);
		}
		
		return status;

	}

	private String validateConfigMap(Map configData) {
		for (Iterator i = keys.iterator(); i.hasNext();) {
			String key = (String) i.next();
			String val = (String) configData.get(key);
			if (!key.equals(DCTMCLASS) && !key.equals(AUTHENTICATIONTYPE)
					&& !key.equals(WHERECLAUSE) && !key.equals(ISPUBLIC) && !key.equals(INCLUDED_OBJECT_TYPE) 
					&& !key.equals(INCLUDED_META) && !key.equals(ROOT_OBJECT_TYPE) && !key.equals(WHERECLAUSE)
					&& (val == null || val.length() == 0)) {
				return key;
			}
		}
		return "";
	}

	private String makeValidatedForm(Map configMap){
		StringBuffer buf = new StringBuffer();
		
		String value = "";
		HashSet hashIncludedMeta;
		HashSet hashIncludedType;
		String rootType = "";
		String actionUpdate = "";
		Resource res = null;
		String returnMessage = "";

		int cpt=0;
		ICollection collecTypes = null;
		String advConf="";
	
		

		logger.info("in makeValidatedForm");
		
		//javascript functions used to 
		buf.append("<script type=\"text/javascript\"><!--    \n");
		
	
		buf.append("function insertIncludeMetas() { \n");
		buf.append("var txtIncludeMetas = document.getElementById('CM_included_meta');\n");
		buf.append("var selectedArray = new Array();\n");
		buf.append("var selObj = document.getElementById('CM_included_meta_bis');\n");
		
		buf.append("var i;\n");
		buf.append("var count = 0;\n");
		buf.append("for (i=0; i<selObj.options.length; i++) {\n");
		buf.append("selectedArray[count] = selObj.options[i].value;\n");
		buf.append("count++;\n");
		buf.append("}\n");
		buf.append("txtIncludeMetas.value = selectedArray;\n");
		buf.append("}\n"); 
		

		buf.append("function insertIncludeTypes() { \n");
		///buf.append("alert('includeTypes');");
		///buf.append("alert(document.getElementById('CM_included_object_type'));\n");
		buf.append("var txtIncludeTypes = document.getElementById('CM_included_object_type');\n");
		buf.append("var selectedArray = new Array();\n");
		buf.append("var selObj = document.getElementById('CM_included_object_type_bis');\n");
		
		buf.append("var i;\n");
		buf.append("var count = 0;\n");
		buf.append("for (i=0; i<selObj.options.length; i++) {\n");
		
		buf.append("selectedArray[count] = selObj.options[i].value;\n");
		buf.append("count++;\n");
	
		buf.append("}\n");
		buf.append("txtIncludeTypes.value = selectedArray;\n");
		buf.append("}\n"); 
		buf.append("  //--> </script>  ");
		

		try {
			
			
			
			//if configmap is not null : it is not the first time the form is displayed
			//parameters are loaded from the .properties file	
			if((configMap!=null)){
				rootType=(String)configMap.get(ROOT_OBJECT_TYPE);
				logger.info("rootType from configmap : "+rootType);
				advConf=(String)configMap.get(ADVANCEDCONF);
				logger.info("advConf from configmap : "+advConf);
				actionUpdate=(String)configMap.get(ACTIONUPDATE);
				logger.info("actionUpdate from configmap : "+actionUpdate);			
			//if configmap is null : it is the first time the form is displayed.
			//parameters are loaded from the connectorType.xml file	
			}else{
				logger.info("value de rootObjectType null : get from connectorInstance");
				rootType=getRoot_object_type();
				logger.info("rootType from getRoot_object_type() : "+rootType);
				logger.info("rootObjectType : "+rootType);
			}
			
			
			//loop of the config parameters 
			for (Iterator i = keys.iterator(); i.hasNext();) {
					
				String key = (String) i.next();
				///logger.log(Level.INFO,"key vaut "+key);
				if (configMap != null) {
					value = (String) configMap.get(key);
					///logger.log(Level.INFO,"key "+key+" vaut "+value);
				}
				if (key.equals(ISPUBLIC)) {
					appendCheckBox(buf, key, resource.getString(key), value);
					appendStartHiddenRow(buf);
					buf.append(OPEN_ELEMENT);
					buf.append(INPUT);
					appendAttribute(buf, TYPE, HIDDEN);
					appendAttribute(buf, VALUE, "false");
					appendAttribute(buf, NAME, key);
					appendEndRow(buf);
					
					value = "";
				} else {
					if (!key.equals(DCTMCLASS) && !key.equals(AUTHENTICATIONTYPE) && !key.equals(ADVANCEDCONF)) {
						appendStartRow(buf, resource.getString(key));
						
					//creation of a hidden fields for the checkboxes 	
					}else {
						appendStartHiddenRow(buf);
					}
					if (key.equals(DOCBASENAME)) {
						appendDropDownListAttribute(buf, TYPE, value);

					}else if(key.equals(INCLUDED_META)){
						//if the form is not displayed for the first time (modification) and the advanced conf checkbox is checked
						if((sess != null)&&(!actionUpdate.equals("uncheckadvconf")&&(advConf.equals("on")))){
						///if((sess != null)&&(!actionUpdate.equals("uncheckadvconf"))){
							logger.info("cas actionUpdate not uncheckadvconf");
							
							//properties are displayed according to the values stored in the .properties file
							appendSelectMultipleIncludeMetadatas(buf,INCLUDED_META,configMap);
							
							logger.info("after closing the collection");
							
							buf.append("</table>");
							buf.append("</DIV></td></tr>");
							
						}else{
							
							logger.info("cas actionUpdate uncheckadvconf or no sess");
							
							//properties from connectorType.xml file are loaded in a hashset
							hashIncludedMeta=getSetfromXML(INCLUDED_META);
	
							//properties are displayed according to the default values stored in the connectorType.xml file
							appendSelectMultipleIncludeMetadatas(buf,INCLUDED_META,hashIncludedMeta);
							
							buf.append("</table>");
							buf.append("</DIV></td></tr>");
						}


						
						
					///}else if(key.equals(ACTIONUPDATE)){
						
						
					}else if(key.equals(ADVANCEDCONF)){

						appendCheckBox(buf,ADVANCEDCONF,resource.getString(key),value);

					}else if(key.equals(WHERECLAUSE)){
						appendTextarea(buf,WHERECLAUSE,value);
					
				
					}else if(key.equals(INCLUDED_OBJECT_TYPE)){
						
						if((sess != null)&&(!actionUpdate.equals("uncheckadvconf")&&(advConf.equals("on")))){
						///if((sess != null)&&(!actionUpdate.equals("uncheckadvconf"))){			   
							logger.info("cas actionUpdate = "+actionUpdate);
							
							//creation of a collection of all Dctm types whose super_name attribute is not empty
							collecTypes=getListOfTypes(rootType);
							
							//properties are displayed according to the values stored in the .properties file
							appendSelectMultipleIncludeTypes(buf,INCLUDED_OBJECT_TYPE,collecTypes,configMap);
							
						}else{	
							logger.info("cas actionUpdate uncheckadvconf or no sess");
							hashIncludedType=null;
							
							//properties from connectorType.xml file are loaded in a hashset
							hashIncludedType=getSetfromXML(INCLUDED_OBJECT_TYPE);
							logger.info("after getSetfromXML de "+INCLUDED_OBJECT_TYPE);
							
							//properties are displayed according to the default values stored in the connectorType.xml file
							appendSelectMultipleIncludeTypes(buf,INCLUDED_OBJECT_TYPE,hashIncludedType,rootType);
							
							logger.info("after appendSelectMultiple");
						}	
						
					}else if(key.equals(ROOT_OBJECT_TYPE)){
						logger.info("makeValidatedForm - rootObjectType");
						
						buf.append(rootType);
						buf.append(TD_END);
						buf.append(TR_END);
						
						appendStartHiddenRow(buf);
						buf.append(OPEN_ELEMENT);
						buf.append(INPUT);
						appendAttribute(buf, TYPE, HIDDEN);
						appendAttribute(buf, VALUE, rootType);
						appendAttribute(buf, NAME, key);
						appendEndRow(buf);
						
						
					}else {
						buf.append(OPEN_ELEMENT);
						buf.append(INPUT);
						if (key.equals(PASSWORD)) {
							appendAttribute(buf, TYPE, PASSWORD);
							if (configMap != null && (String) configMap.get("password") != null) {
								value = (String) configMap.get("password");
								configMap.remove("password");
							}
						} else if (key.equals(DCTMCLASS)) {
							appendAttribute(buf, TYPE, HIDDEN);
							value=getClientX();
						} else if (key.equals(AUTHENTICATIONTYPE)) { 
							appendAttribute(buf, TYPE, HIDDEN);
							value=getAuthentication_type();
						} else if (key.equals(ACTIONUPDATE)) {
							appendAttribute(buf, TYPE, HIDDEN);
							value="save";
						} else {
							appendAttribute(buf, TYPE, TEXT);
						}
						appendAttribute(buf, VALUE, value);
						appendAttribute(buf, NAME, key);
						appendAttribute(buf, ID, key);
						appendEndRow(buf);
						value = "";
					}
				}
			}
			
			
			
			
			
			/*
			if (configMap != null) {
				Iterator i = new TreeSet(configMap.keySet()).iterator();
				while (i.hasNext()) {
					String key = (String) i.next();
					if (!keySet.contains(key)) {
						// add another hidden field to preserve this data
						String val = (String) configMap.get(key);
						buf.append("<input type=\"hidden\" value=\"");
						buf.append(val);
						buf.append("\" name=\"");
						buf.append(key);
						buf.append("\"/>\r\n");
					}
				}
			}
			*/


			
		 } catch (RepositoryException e1) {
			
			 e1.printStackTrace();
			 ///returnMessage =   createErrorMessage(e1);
			 ///return(returnMessage + "<br>"+ buf);
					
			// TODO Auto-generated catch block
			
		}finally{
			
				if(sess!=null){
				///if((advConf.equals("true"))&&(sess!=null)){
					
					sessMag.release(sess);
					logger.info("session released ");
					
				}
			
		}
		
		
		
		
		return buf.toString();
	}

	private void setSessionManager(Map LogMap) throws RepositoryException {
		ILoginInfo loginInfo;
		try {
			cl = (IClientX) Class
			.forName(
					"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX")
					.newInstance();
		} catch (InstantiationException e) {
			logger
			.log(
					Level.SEVERE,
					"error while building the configuration form. The docbase will be added manually. ",
					e);

		} catch (IllegalAccessException e) {
			logger
			.log(
					Level.SEVERE,
					"error while building the configuration form. The docbase will be added manually. ",
					e);

		} catch (ClassNotFoundException e) {
			logger
			.log(
					Level.SEVERE,
					"error while building the configuration form. The docbase will be added manually. ",
					e);

		} catch (NoClassDefFoundError e) {
			logger
			.log(
					Level.SEVERE,
					"error while building the configuration form. The docbase will be added manually. ",
					e);

		}
		
		;
		
		client = cl.getLocalClient();
		sessMag=client.newSessionManager();
		sessMag.clearIdentity((String)LogMap.get(DOCBASENAME));
		logger.info("after clearIdentity for docbase : "+(String)LogMap.get(DOCBASENAME));
		loginInfo = cl.getLoginInfo();
		loginInfo.setUser((String)LogMap.get(LOGIN));
		logger.info("after setIdentity for login : "+(String)LogMap.get(LOGIN));
		loginInfo.setPassword((String)LogMap.get(PASSWORD));
		sessMag.setIdentity((String)LogMap.get(DOCBASENAME),loginInfo);
		sessMag.setDocbaseName((String)LogMap.get(DOCBASENAME));
		logger.info("after setIdentity for docbase : "+(String)LogMap.get(DOCBASENAME));
		logger.info("new sessionManager");
	}
	
	
	private void setSession(Map LogMap) throws RepositoryException{

		if (LogMap != null) {
			Iterator i = new TreeSet(LogMap.keySet()).iterator();
			while (i.hasNext()) {
				String key = (String) i.next();
				String val = (String) LogMap.get(key);
				///logger.info("key "+key+" vaut "+val);
			}
		}
		
		logger.info("login vaut "+(String)LogMap.get(LOGIN));
		logger.info("password vaut "+(String)LogMap.get(PASSWORD));
		logger.info("docbase vaut "+(String)LogMap.get(DOCBASENAME));
		
		
		
		sess=sessMag.getSession((String)LogMap.get(DOCBASENAME));
		
	}
	
	
	private HashSet getSetfromXML(String setName){
		HashSet hash=new HashSet();
		logger.info("in getSetfromXML");
		
		if(setName.equals(INCLUDED_META)){
			logger.info("dans included_meta");
			hash=getIncluded_meta();
		}else if(setName.equals(INCLUDED_OBJECT_TYPE)){
			logger.info("dans Included_object_type");
			hash=getIncluded_object_type();
			
		}
		return(hash);
	}

private ICollection getListOfTypes(String root_object_type){
		
		
		IQuery que = null;
		String queryString="";
		ICollection collec = null;
		try {
			
			logger.info("docbase of sessMag vaut "+sessMag.getDocbaseName());
			que=cl.getQuery();
			queryString="select * from dm_type where super_name != '' order by r_object_id";
			logger.info("queryString : "+queryString);
			que.setDQL(queryString);
			
			boolean auth=sessMag.authenticate(sessMag.getDocbaseName());
			logger.info("AUTH VAUT "+auth);
			
			
			collec = que.execute(sessMag, IQuery.EXECUTE_READ_QUERY);
			
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			logger
			.log(
					Level.SEVERE,
					"error while building the configuration form. The docbase will be added manually. ",
					e);

		}
		return collec;
		
	}
	
	
	
private void appendSelectMultipleIncludeTypes(StringBuffer buf,String name,ICollection collecTypes,Map configMap) throws RepositoryException{
	
	logger.info("in SelectMultipleIncludeTypes with collectoin parameter");
	String type;
	String super_type;
	String typeList[];
	String stTypes = null;
	
	stTypes=((String)configMap.get(INCLUDED_OBJECT_TYPE));
	///stTypes="dm_document,dm_folder";
	typeList=stTypes.split(",");
	
	HashSet hashTypes=new HashSet();
	HashSet hashDctmTypes=new HashSet();
	for (int x=0; x<typeList.length; x++){
		hashTypes.add(typeList[x]);
	}
	
	//javascript functions used to pass an item from a select list to another one 
	buf.append("<script type=\"text/javascript\"> " +
			"var selOptions = new Array(); " +
			"function swap(listFrom, listTo){" +
				"fromList=document.getElementsByName(listFrom)[0];" +
				"toList = document.getElementsByName(listTo)[0];" +
				"var i;" +
				"var count = 0;" +
				
				///"alert(fromList.options.length);" +
				"for(i=0;i<fromList.options.length;i++){" +
					"if (fromList.options[i].selected) {" +
						"count++;" +
						///"alert('option '+i+' selected');" +
						///"alert('count = '+count);" +
					"}" +
					"if ((count == fromList.options.length)&&(listFrom=='CM_included_object_type_bis')){" +
						"alert('You need to select at least one object type');" +
						"return false;" +  
					"}" +
					"if ((count == fromList.options.length)&&(listFrom=='CM_included_meta_bis')){" +
						"alert('You need to select at least one property');" +
						"return false;" +  
					"}" +
					
				"}" +
				///"alert('selectedIndex = '+fromList.selectedIndex);" +
				"while (fromList.selectedIndex != -1)" +
				"{ " +
					"addOption(toList,fromList.options[fromList.selectedIndex]); " +
					"fromList.options[fromList.selectedIndex] = null;" +
				" }" +
			" } " +
			"function addOption(list, option){" +
				" list.options[list.options.length]=new Option(option.value,option.value);" +
			" } " +
			"function selectAll(select){"+
				"for (var i = 0; i < document.getElementById(select).length; i++)"+
				"{"+
					"document.getElementById(select).options[i].selected = true;"+
				"}"+
			"}"+
	"</script>");
	buf.append(SELECT_START);
	buf.append(" " + NAME);
	buf.append("=\"");
	buf.append("included_object_type_toinclude");
	buf.append("\" STYLE=\"width:270px\" MULTIPLE size=\"10\" >\n");
	
	int nbtypes=0;
	///try {
		if(((String)configMap.get(ADVANCEDCONF)).equals("on")){
			//loop of the Dctm types whose super_name field is not empty
			while(collecTypes.next()) {
				type = collecTypes.getString("name");
				logger.info("type : "+type);
				super_type = collecTypes.getString("super_name");
				logger.info("super type : "+super_type);
				///if(!hashTypes.contains(type)&&(hashDctmTypes.contains(super_type)||super_type.equals("dm_sysobject")||hashTypes.contains(super_type))){
				//exclusion of the selected types (=in the second select list and stored in the .properties file) and of the types whose super_name attribute is emty (the where clause in the query is not enough since a type can have a super_type whose super_name is empty)
				if(!hashTypes.contains(type)&&!super_type.equals("")){
					hashDctmTypes.add(type);
					nbtypes++;
					logger.info("added type : "+type);
					//Creation of the select list of the types available for selection
					buf.append("<option value=\""+type+"\">");
					buf.append(type+"\n");
				}	
			}
		}	
	///} catch (RepositoryException e) {
		// TODO Auto-generated catch block
		///e.printStackTrace();
	///}
	
	logger.info("nbtypes is : "+nbtypes);
	buf.append(SELECT_END);
	buf.append("<input type=\"button\" value=\">\"  onClick=\"swap('CM_included_object_type_toinclude','CM_included_object_type_bis');insertIncludeTypes();insertIncludeMetas();document.getElementById('action_update').value='addmeta';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();\"></input>");
	buf.append("<input type=\"button\" value=\"<\"  onClick=\"swap('CM_included_object_type_bis','CM_included_object_type_toinclude');insertIncludeTypes();insertIncludeMetas();document.getElementById('action_update').value='addmeta';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();\"></input>");
	
	
	buf.append(SELECT_START);
	buf.append(" " + NAME);
	buf.append("=\"");
	buf.append("included_object_type_bis");
	buf.append("\" STYLE=\"width:270px\" MULTIPLE size=\"10\" ID=\"CM_included_object_type_bis\" >\n");
	
	
	//C
	if (!hashTypes.isEmpty()) {
		Iterator iter = hashTypes.iterator();
		
		while (iter.hasNext()) {
			///logger.info("appendSelectMultiple "+name+" type vaut "+type);
			type = (String) iter.next();
			//Creation of the select list of the types previously selected
			buf.append("<option value=\""+type+"\">");
			buf.append(type+"\n");
		}
	}
	
	buf.append(SELECT_END);
	buf.append(TD_END);
	buf.append(TR_END);
	buf.append("<tr><td><input type=\"hidden\" id=\"CM_included_object_type\" name=\"included_object_type\" value=\""+stTypes+"\"></td></tr>");	
	
	//session released (necessary to avoid session leaks)
	try{
		if (collecTypes.getState() != ICollection.DF_CLOSED_STATE) {
			collecTypes.close();
			logger.fine("collection closed");
			sessMag.release(collecTypes.getSession());
			logger.fine("collection session released");
		}	
	}catch(RepositoryException re1){
		logger.severe("Error while closing "+re1);
	}

	
}

private void appendSelectMultipleIncludeTypes(StringBuffer buf, String name,
		HashSet hash, String superData){

	logger.info("in appendSelectMultipleIncludeTypes");

	//javascript functions used to pass an item from a select list to another one 

	String stTypes="";
	String type;
	buf.append("<script type=\"text/javascript\"> " +
			"var selOptions = new Array(); " +
			"function swap(listFrom, listTo){" +
				"fromList=document.getElementsByName(listFrom)[0]; " +
				"toList = document.getElementsByName(listTo)[0]; " +
				"while (fromList.selectedIndex != -1)" +
				"{ " +
					"addOption(toList,fromList.options[fromList.selectedIndex]); " +
					"fromList.options[fromList.selectedIndex] = null;" +
				" }" +
			" } " +
			"function addOption(list, option){" +
				///" list.options[list.options.length]=new Option(option.innerHTML,option.value);" +
			" list.options[list.options.length]=new Option(option.value,option.value);" +
			" } " +
			"function selectAll(select){"+
				"for (var i = 0; i < document.getElementById(select).length; i++)"+
				"{"+
					"document.getElementById(select).options[i].selected = true;"+
				"}"+
			"}"+
	"</script>");
	buf.append(SELECT_START);
	buf.append(" " + NAME);
	buf.append("=\"");
	buf.append("included_object_type_toinclude");
	buf.append("\" STYLE=\"width:270px\" MULTIPLE size=\"10\" >\n");

	
	buf.append(SELECT_END);
	
	buf.append("<input type=\"button\" value=\">\"  onClick=\"swap('CM_included_object_type_toinclude','CM_included_object_type_bis');\"></input>");
	buf.append("<input type=\"button\" value=\"<\"  onClick=\"swap('CM_included_object_type_bis','CM_included_object_type_toinclude');\"></input>");
	
	//Creation of the list of the types available for selection (empty)
	buf.append(SELECT_START);
	buf.append(" " + NAME);
	buf.append("=\"");
	buf.append("included_object_type_bis");
	buf.append("\" STYLE=\"width:270px\" MULTIPLE size=\"10\" ID=\"CM_included_object_type_bis\" >\n");
	

	
	///Creation of the select list of the default types (listed in the connectorType.xml file)
	if (!hash.isEmpty()) {
		Iterator iter = hash.iterator();
		
		while (iter.hasNext()) {
			///logger.info("appendSelectMultiple "+name+" type vaut "+type);
			
			type = (String) iter.next();
			stTypes=stTypes.concat(type+",");
			buf.append("<option value=\""+type+"\">");
			buf.append(type+"\n");
		}
	}
	
	
	///
	logger.info("stTypes before substring "+stTypes);
	stTypes=stTypes.substring(0,stTypes.length()-1);
	logger.info("stTypes after substring "+stTypes);
	
	buf.append(SELECT_END);
	buf.append(TD_END);
	buf.append(TR_END);
	buf.append("<tr><td><input type=\"hidden\" id=\"CM_included_object_type\" name=\"included_object_type\" value=\""+stTypes+"\"></td></tr>");	
	
}
	
private void appendSelectMultipleIncludeMetadatas(StringBuffer buf, String name, Map configMap) throws RepositoryException {
	logger.info("in appendSelectMultipleIncludeMetadatas collection");
	
	String meta;
	String stType;
	IType mytype;
	IType currentType;
	IType dmsysType;
	IAttr attr;
	IAttr dmsysattr;
	String data;
	int type_cnt = 0;
	int i = 0;
	StringBuffer buf2=new StringBuffer();
	String stMeta=null;
	String typeList[];
	Iterator iterTypes=null;
	String stCurrentType = null;
	String dmsysattrname = null;
	
	
	logger.info("string type : "+(String)configMap.get(INCLUDED_OBJECT_TYPE));
	typeList=((String)configMap.get(INCLUDED_OBJECT_TYPE)).split(",");
	
	
	
	HashMap metasByTypes=new HashMap();
	HashSet hashTypes=new HashSet();
	HashSet tempTypes=new HashSet();
	HashSet hashDmSysMeta=new HashSet();
	HashSet hashMetasOfSelectedTypes=new HashSet();
	
	HashMap typesByMetas=new HashMap();
	HashSet tempMetas=new HashSet();

	
	for (int x=0; x<typeList.length; x++){
		hashTypes.add(typeList[x]);
	}
	

	String metaList[];
	logger.info("string meta : "+(String)configMap.get(INCLUDED_META));
	stMeta = ((String)configMap.get(INCLUDED_META));
	metaList=stMeta.split(",");
	
	HashSet hashMetas=new HashSet();
	for (int x=0; x<metaList.length; x++){
		hashMetas.add(metaList[x]);
	}
	
	buf2.append(SELECT_START);
	buf2.append(" " + NAME);
	buf2.append("=\"");
	buf2.append("included_meta_bis");
	buf2.append("\" STYLE=\"width:270px\" MULTIPLE size=\"10\" id=\"CM_included_meta_bis\">\n");
	
	
	buf.append(SELECT_START);
	buf.append(" " + NAME);
	buf.append("=\"");
	buf.append("included_meta_toinclude");
	buf.append("\" STYLE=\"width:270px\" MULTIPLE size=\"10\">\n");
	
	dmsysType=sess.getType("dm_sysobject");
	hashDmSysMeta=new HashSet();
	for(int j=0;j<dmsysType.getTypeAttrCount();j++){
		dmsysattr=dmsysType.getTypeAttr(j);
		dmsysattrname=dmsysattr.getName();
		logger.info("dmsysattrname "+dmsysattrname+" is metadata of dm_sysobject");
		hashDmSysMeta.add(dmsysattrname);
	}
	
	
	if(((String)configMap.get(ADVANCEDCONF)).equals("on")){
		
		///loop of the selected types list
		for (int x=0; x<typeList.length; x++){
			stType=typeList[x];
			mytype=sess.getType(stType);
			///mytype=sess.getType(stType);
			///mytype=(getSession(configMap)).getType(stType);
			logger.info("stType is "+stType);
			///loop of the properties of each selected type
			for(i=0;i<mytype.getTypeAttrCount();i++){
				logger.info("compteur: "+mytype.getTypeAttrCount());
				attr=mytype.getTypeAttr(i);
				///logger.info("attr vaut "+attr.toString());
				data=attr.getName();
				logger.info("attr is "+data+" - attr of the type "+stType);		
				if(!hashMetasOfSelectedTypes.contains(data)){
					hashMetasOfSelectedTypes.add(data);
				}
				///if the property is a dm_sysobject one, dm_sysobject is added to the temporary types hashset
				if(hashDmSysMeta.contains(data)){
					tempTypes.add("dm_sysobject");
					logger.info("attr "+data+" is a dm_sysobject attribute");
				///if the property is not already present in the list of available properties : the type is added to the temporary types hashset
				}else if(!metasByTypes.containsKey(data)){
					tempTypes.add(stType);
					logger.info("attr "+data+" is a new attribute for the metas list");			
				///if the property is not already present in the list of available properties
				}else{
					logger.info("attr "+data+" is not a new attribute for the metas list");		
					hashTypes=(HashSet)metasByTypes.get(data);
					iterTypes = hashTypes.iterator();
					///loop of the hashset of types whom the property can belong to (among the selected types)
					while (iterTypes.hasNext()) {
						stCurrentType=(String)iterTypes.next();
						logger.info("the type "+stCurrentType+" is already known to have the meta "+data);
						currentType=sess.getType(stCurrentType);
						///is the selected type is dm_sysobject : dm_sysobject is added to the temporary types hashset
						if(stCurrentType.equals("dm_sysobject")){
							logger.info(stCurrentType+" is "+stCurrentType);
							tempTypes.add(stCurrentType);
						///if the selected type is the supertype of one type whom the property can belong to : the selected type is added to the temporary types hashset
						}else if(((currentType.getSuperType()).getName()).equals(stType)){
							logger.info(stType+" is supertype of "+stCurrentType);
							tempTypes.add(stType);
							logger.info("so supertype "+stType+" is added");
						///if the selected type is the subtype of one type whom the property can belong to : the type whom the property can belong to is added to the temporary types hashset
						}else if(mytype.isSubTypeOf(stCurrentType)){
							logger.info(stType+" is  subtype of "+stCurrentType);
							tempTypes.add(stCurrentType);
							logger.info(" so supertype "+stCurrentType+" is added");
                        ///if the selected type is one of the types whom the property can belong to  : the type whom the property can belong to is added to the temporary types hashset
						}else if(stType.equals(stCurrentType)){
							logger.info(stType+" is "+stCurrentType);
							tempTypes.add(stCurrentType);
							logger.info(" so type "+stCurrentType+" is added");
						///if the selected type and one of the types whom the property can belong to don't have any hierarchical link : the type whom the property can belong to and the selected type are added to the temporary types hashset
						}else{
							logger.info("type "+stCurrentType+" is just another type with the attribute "+data);
							tempTypes.add(stType);
							tempTypes.add(stCurrentType);
							logger.info(" so type "+stType+" is added and type "+stCurrentType+" is also added");
						}
						
					}
				}
				
				logger.info("adding tempTypes to metasByTypes hashMap");		
				metasByTypes.put(data,tempTypes);
				///temporary hashset of types reinitiated
				tempTypes = new HashSet(); 
			}
		}
		
		
		
		//Creation of the select list of the available properties (properties of the selected types) with the names of the types it belongs to 
		if (!metasByTypes.isEmpty()) {	
			logger.info("writing the select"); 
			Set dataSet = metasByTypes.keySet();
			Iterator iter = dataSet.iterator();
			while (iter.hasNext()) {
				///logger.info("appendSelectMultiple "+name+" type vaut "+type);
				data = (String) iter.next();
				hashTypes=(HashSet)metasByTypes.get(data);
				iterTypes = hashTypes.iterator();
				if(!hashMetas.contains(data)){
					buf.append("<option value=\""+data+"\">");
					buf.append(data+" (");
					while (iterTypes.hasNext()) {
						stType=(String)iterTypes.next();
						buf.append(" "+stType+" ");
					}
					buf.append(") </option>\n");
				}
			}
		}
		
		
	}	
	
	//Creation of the select list of the selected properties
	if (!hashMetas.isEmpty()) {
		Iterator iterMeta = hashMetas.iterator();
		
		while (iterMeta.hasNext()) {
			data=(String) iterMeta.next();
			
			if(hashMetasOfSelectedTypes.contains(data)){
				buf2.append("<option value=\""+data+"\">");
				buf2.append(data+"</option>\n");
				
			}
		}
	}	

	///logger.info("appendSelectMultipleIncludeMetadatas"+name);

	
	logger.info("before select");
	buf.append(SELECT_END);
	logger.info("after select");

	
	buf.append("<input type=\"button\" value=\">\"  onClick=\"swap('CM_included_meta_toinclude','CM_included_meta_bis');insertIncludeMetas();insertIncludeTypes();document.getElementById('action_update').value='addmeta';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();\"></input>");
	buf.append("<input type=\"button\" value=\"<\"  onClick=\"swap('CM_included_meta_bis','CM_included_meta_toinclude');insertIncludeMetas();insertIncludeTypes();document.getElementById('action_update').value='addmeta';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();\"></input>");
	
	
	
	buf2.append(SELECT_END);
	buf.append(buf2);
	
	buf.append(TD_END);
	buf.append(TR_END);
	buf.append("<tr><td><input type=\"hidden\" id=\"CM_included_meta\" name=\"included_meta\" value=\""+stMeta+"\"></td></tr>");		
	
	
	}
	
	private void appendSelectMultipleIncludeMetadatas(StringBuffer buf, String name,
			HashSet hashMeta){
		logger.info("in appendSelectMultipleIncludeMetadatas hashMeta");
		
		String meta;
		String stMeta="";
		
		String type;
		
		
		StringBuffer buf2=new StringBuffer();
		
		buf2.append(SELECT_START);
		buf2.append(" " + NAME);
		buf2.append("=\"");
		buf2.append("included_meta_bis");
		buf2.append("\" STYLE=\"width:270px\" MULTIPLE size=\"10\" id=\"CM_included_meta_bis\">\n");
		
		//Creation of the select list of the available properties (empty)
		buf.append(SELECT_START);
		buf.append(" " + NAME);
		buf.append("=\"");
		buf.append("included_meta_toinclude");
		buf.append("\" STYLE=\"width:270px\" MULTIPLE size=\"10\">\n");
			
		
		//Creation of the select list of the selected properties
		if (name.equals(INCLUDED_META)){
	
			if (!hashMeta.isEmpty()) {
				Iterator iter = hashMeta.iterator();
					
				while (iter.hasNext()){
					meta = (String) iter.next();
					stMeta=stMeta.concat(meta+",");
					
					buf2.append("<option value=\""+meta+"\">");
					buf2.append(meta+"</option>\n");
					
				}
			}
		}	
		
		stMeta=stMeta.substring(0,stMeta.length()-1);
		
		logger.info("before select");
		buf.append(SELECT_END);
		logger.info("after select");
	
		buf.append("<input type=\"button\" value=\">\"  onClick=\"swap('CM_included_meta_toinclude','CM_included_meta_bis');\"></input>");
		buf.append("<input type=\"button\" value=\"<\"  onClick=\"swap('CM_included_meta_bis','CM_included_meta_toinclude');\"></input>");
		
		buf.append(buf2);
		buf2.append(SELECT_END);
		buf.append(TD_END);
		buf.append(TR_END);
		buf.append("<tr><td><input type=\"hidden\" id=\"CM_included_meta\" name=\"included_meta\" value=\""+stMeta+"\"></td></tr>");		
	}
	
	
	private void appendCheckBox(StringBuffer buf, String key, String label,
			String value) {
		buf.append(TR_START);
		buf.append(TD_START);
		buf.append(OPEN_ELEMENT);
		buf.append(INPUT);
		buf.append(" " + TYPE + "=" + CHECKBOX);
		buf.append(" " + NAME + "=\"" + key + "\" ");
		
		if(key.equals(ADVANCEDCONF)){
			buf.append("ID=\"ADVC\"");
			if (value != null && value.equals("on")) {
			///if (value != null) {
				logger.info("advanced conf not null");	
					logger.info("advanced conf set to on");
					///buf.append("onClick=\"if(document.getElementById('more').style.display == 'none'){document.getElementById('more').style.display='block';document.getElementById('action_update').value='checkadvconf';insertIncludeMetas();insertIncludeTypes();document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();}else{document.getElementById('more').style.display='none';document.getElementById('action_update').value='uncheckadvconf';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();insertIncludeMetas();insertIncludeTypes();}\" checked>");
					///insertIncludeMetas();insertIncludeTypes();
					buf.append("onClick=\"if(document.getElementById('more').style.display == 'none'){if((document.getElementById('login').value != '')&&(document.getElementById('Password').value != '')&&(document.getElementById('webtop_display_url').value != '')){document.getElementById('more').style.display='block';document.getElementById('action_update').value='checkadvconf';insertIncludeMetas();insertIncludeTypes();" +
							"document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();}else{alert('"+resource.getString("advanced_config_error")+"');this.checked=false;}}else{if(confirm('"+resource.getString("confirm_uncheck_advanced")+"')){document.getElementById('more').style.display='none';document.getElementById('action_update').value='uncheckadvconf';" +
									"document.getElementById('where_clause').value='';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();insertIncludeMetas();insertIncludeTypes();}}\" checked>");
					
					
					buf.append(label + TD_END);
	
					buf.append(TR_END);
					
					buf.append(TR_START);
					buf.append("<td colspan=\"2\"><input name=\"advanced_configuration\" type=\"hidden\" value=\"true\"></td>");
					buf.append(TR_END);
					
					/*
					buf.append(TR_START);
					buf.append("<td colspan=\"2\"><input name=\"action_update\" ID=\"action_update\" type=\"hidden\" value=\"save\"></td>");
					buf.append(TR_END);
					*/
					buf.append("<tr><td colspan=\"2\"><DIV ID=\"more\" style=\"DISPLAY: block\">");
					buf.append("<table>");
			}else{
					logger.info("advanced conf set to off");
					///insertIncludeMetas();insertIncludeTypes();
					///buf.append("onClick=\"if(document.getElementById('more').style.display == 'none'){document.getElementById('more').style.display='block';document.getElementById('action_update').value='checkadvconf';insertIncludeMetas();insertIncludeTypes();document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();}else{document.getElementById('more').style.display='none';document.getElementById('action_update').value='uncheckadvconf';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();insertIncludeMetas();insertIncludeTypes();}\">");
					buf.append("onClick=\"if(document.getElementById('more').style.display == 'none'){if((document.getElementById('login').value != '')&&(document.getElementById('Password').value != '')&&(document.getElementById('webtop_display_url').value != '')){" +
							"document.getElementById('more').style.display='block';document.getElementById('action_update').value='checkadvconf';insertIncludeMetas();insertIncludeTypes();document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();}" +
							"else{alert('"+resource.getString("advanced_config_error")+"');this.checked=false;}}else{if(confirm('"+resource.getString("confirm_uncheck_advanced")+"')){document.getElementById('more').style.display='none';document.getElementById('action_update').value='uncheckadvconf';" +
									"document.getElementById('where_clause').value='';document.getElementsByTagName('input')[document.getElementsByTagName('input').length-1].click();insertIncludeMetas();insertIncludeTypes();}}\">");
					
					buf.append(label + TD_END);
					
					buf.append(TR_END);
					
					buf.append(TR_START);
					buf.append("<td colspan=\"2\"><input name=\"advanced_configuration\" type=\"hidden\" value=\"false\"></td>");
					buf.append(TR_END);
					
					/*
					buf.append(TR_START);
					buf.append("<td colspan=\"2\"><input name=\"action_update\" ID=\"action_update\" type=\"hidden\" value=\"save\"></td>");
					buf.append(TR_END);
					*/
					
					buf.append("<tr><td colspan=\"2\"><DIV ID=\"more\" style=\"DISPLAY: none\">");
					buf.append("<table>");
			}	
		}else{
			if (value != null && value.equals("on")) {
				buf.append(CHECKED);
			}
			buf.append(CLOSE_ELEMENT);
			
			buf.append(label + TD_END);

			buf.append(TR_END);
		}
		
	}

	private void appendTextarea(StringBuffer buf, String name,
			String value) {
		buf.append(TEXTAREA_START);
		buf.append(" name"+ "=\"" + name + "\"");
		buf.append(" rows=\"10\" ");
		buf.append(" STYLE=\"width:270px\" ");
		buf.append(CLOSE_ELEMENT);
		buf.append(value);
		buf.append(TEXTAREA_END);
		buf.append(TD_END);
	}

	private void appendDropDownListAttribute(StringBuffer buf, String type2,
			String value) {
		IClientX cl = null;
		try {
			cl = (IClientX) Class
			.forName(
					"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX")
					.newInstance();
		} catch (InstantiationException e) {
			logger
			.log(
					Level.SEVERE,
					"error while building the configuration form. The docbase will be added manually. ",
					e);

		} catch (IllegalAccessException e) {
			logger
			.log(
					Level.SEVERE,
					"error while building the configuration form. The docbase will be added manually. ",
					e);

		} catch (ClassNotFoundException e) {
			logger
			.log(
					Level.SEVERE,
					"error while building the configuration form. The docbase will be added manually. ",
					e);

		} catch (NoClassDefFoundError e) {
			logger
			.log(
					Level.SEVERE,
					"error while building the configuration form. The docbase will be added manually. ",
					e);

		}
		IClient client;

		try {
			client = cl.getLocalClient();

			IDocbaseMap mapOfDocbasesName = client.getDocbaseMap();
			if (!(mapOfDocbasesName.getDocbaseCount() > 0)) {
				appendAttribute(buf, type2, value);
			} else {
				buf.append(SELECT_START);
				buf.append(" " + NAME);

				buf.append("=\"");
				buf.append(DOCBASENAME);
				buf.append("\">\n");
				for (int i = 0; i < mapOfDocbasesName.getDocbaseCount(); i++) {
					buf.append("\t<option ");
					if (value != null
							&& mapOfDocbasesName.getDocbaseName(i)
							.equals(value)) {
						buf.append("selected ");
					}
					buf.append("value=\"" + mapOfDocbasesName.getDocbaseName(i)
							+ "\"" + ">" + mapOfDocbasesName.getDocbaseName(i)
							+ "</option>\n");
				}
				buf.append(SELECT_END);
				buf.append(TD_END);
				buf.append(TR_END);
			}
		} catch (RepositoryException e) {
			logger
			.log(
					Level.SEVERE,
					"error while building the configuration form. The docbase will be added manually. ",
					e);

		}

	}



	private void appendStartRow(StringBuffer buf, String key) {
		buf.append(TR_START);
		buf.append(TD_START);
		buf.append(key);
		buf.append(TD_END);
		buf.append(TD_START);
	}

	

	private void appendStartHiddenRow(StringBuffer buf) {
		buf.append(TR_START);
		buf.append(TD_START);
		buf.append(TD_END);
		buf.append(TD_START);

	}

	private void appendEndRow(StringBuffer buf) {
		buf.append(CLOSE_ELEMENT);
		buf.append(TD_END);
		buf.append(TR_END);
	}

	private void appendAttribute(StringBuffer buf, String attrName,
			String attrValue) {
		buf.append(" ");
		buf.append(attrName);
		buf.append("=\"");
		buf.append(attrValue);
		buf.append("\"");
		if (attrName == TYPE && attrValue == TEXT) {
			buf.append(" size=\"50\"");
		}
	}

	
	public String getAuthentication_type() {

		return authentication_type;
	}
	
	public void setAuthentication_type(String authentication_type) {
		this.authentication_type = authentication_type;
		logger.log(Level.INFO, "authentication_type set to " +authentication_type);
	}
	
	public String getClientX() {

		return clientX;
	}
	
	public void setClientX(String clientX) {
		this.clientX = clientX;
		logger.log(Level.INFO, "clientX set to " +clientX);
	}
	
	public String getAdvanced_configuration() {

		return advanced_configuration;
	}
	
	public void setAdvanced_configuration(String advanced_configuration) {
		this.advanced_configuration = advanced_configuration;
		logger.info("advanced_configuration set to " +advanced_configuration);
	}
	
}