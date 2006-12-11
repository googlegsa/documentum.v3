package com.google.enterprise.connector.dctm.dfcwrap;
import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfAuditTrailManager;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfEnumeration;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfFormat;
import com.documentum.fc.client.IDfGroup;
import com.documentum.fc.client.IDfLocalModuleRegistry;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfRelationType;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.client.IDfVersionTreeLabels;
import com.documentum.fc.client.IDfWorkflowBuilder;
import com.documentum.fc.client.acs.IDfAcsTransferPreferences;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.fc.common.IDfTime;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmId;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmLoginInfo;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSession;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSysObject;

public interface ISession{
	
	
	public IDctmSysObject getObjectByQualification(String qualification);
	
	public void authenticate(IDctmLoginInfo loginInfo);
	
	public IDctmSysObject getObject(IDctmId objectId);
	
	//IDfSession machin =new IDctmSession(); 
/*
	public void abortTrans();

	void 	addDynamicGroup(String groupName);
	boolean 	apiExec(String cmd, String args); 
	IDfCollection 	apply(String objId, String functionName, IDfList args, IDfList dataType, IDfList values);
	IDfId 	archive(String predicate, String operatorName, int priority, boolean sendMail, IDfTime dueDate); 
	void 	assume(IDfLoginInfo loginInfo);
	void 	authenticate(IDfLoginInfo loginInfo);
	void 	beginTrans();
     
void 	changePassword(String oldPasswd, String newPasswd);
    
void 	commitTrans();
    
void 	dequeue(IDfId stampId);
    
String 	describe(String type, String objType);
    
void 	disconnect();

String 	exportTicketKey(String password);
    
void 	flush(String flushType, String cacheKey);
void 	flushCache(boolean discardChanged);

IDfACL 	getACL(String aclDomain, String aclName);
IDfAcsTransferPreferences 	getAcsTransferPreferences();

String 	getAliasSet();
String 	getApplicationToken(String userName, String scope, int timeout, String appId, boolean machine_only);
IDfAuditTrailManager 	getAuditTrailManager();

IDfClient 	getClient();

IDfTypedObject 	getClientConfig();

IDfTypedObject 	getConnectionConfig();

String 	getDBMSName();
   
int 	getDefaultACL();
   
String 	getDMCLSessionId();
   
IDfTypedObject 	getDocbaseConfig();
     
String 	getDocbaseId();
   
String 	getDocbaseName();
    
String 	getDocbaseOwnerName();
    
String 	getDocbaseScope();
  
IDfTypedObject 	getDocbrokerMap();
    
String 	getDynamicGroup(int index);
     
int 	getDynamicGroupCount();
   
IDfCollection 	getEvents();
 
IDfFolder 	getFolderByPath(String folderPath);
    
IDfFormat 	getFormat(String formatName);
    
IDfGroup 	getGroup(String groupName);
     
IDfId 	getIdByQualification(String qualification);
    
IDfCollection 	getLastCollection();
   
IDfLoginInfo 	getLoginInfo();
    
//String 	getLoginTicket();
    
String 	getLoginTicketEx(String userName, String scope, int timeout, boolean single_use, String serverName);
    
String 	getLoginTicketForUser(String userName);

String 	getLoginUserName();

String 	getMessage(int severityLevel);
     
IDfLocalModuleRegistry 	getModuleRegistry();
   
IDfPersistentObject 	getObject(IDfId objectId);
     IDfPersistentObject 	getObjectByPath(String objectPath);

IDfPersistentObject 	getObjectByQualification(String qualification);

IDfPersistentObject 	getObjectByQualificationWithInterface(String qualification, String interfaceName);
     
IDfEnumeration 	getObjectPaths(IDfId objectId);
    
IDfPersistentObject 	getObjectWithCaching(IDfId objectId, String typeName, String className, String currencyCheckValue, boolean usePersistentCache, boolean useSharedCache);
    
IDfPersistentObject 	getObjectWithInterface(IDfId objectId, String interfaceName);
    
IDfPersistentObject 	getObjectWithType(IDfId objectId, String typeName, String className);
 
IDfRelationType 	getRelationType(String relationName);
    
IDfCollection 	getRunnableProcesses(String additionalAttributes);
    
String 	getSecurityMode();
     
IDfTypedObject 	getServerConfig();
    
IDfTypedObject 	getServerMap(String docbaseName);
     
String 	getServerVersion();
     
IDfTypedObject 	getSessionConfig();

String 	getSessionId();
    
IDfSessionManager 	getSessionManager();
     
IDfCollection 	getTasks(String userName, int filter, String additionalAttributes, String orderBy);

IDfCollection 	getTasksEx(String userName, int filter, IDfList orderByList, IDfList ascendingList);
     
IDfType 	getType(String typeName);

IDfTypedObject 	getTypeDescription(String typeName, String attribute, IDfId businessPolicyId, String state);
   
IDfUser 	getUser(String userName);
     
IDfUser 	getUserByOSName(String userOSName, String userOSDomain);
    
IDfVersionTreeLabels 	getVersionTreeLabels(IDfId chronicleId);
  
boolean 	hasEvents();
     
boolean 	importTicketKey(String key, String password);
     
boolean 	isACLDocbase();
   
boolean 	isAdopted();
     
boolean 	isConnected();
    
boolean 	isRemote();
    
boolean 	isShared();
    
boolean 	isTransactionActive();
     
boolean 	lock(int timeoutInMsec);
     
IDfPersistentObject 	newObject(String typeName);
     
IDfPersistentObject 	newObjectWithType(String typeName, String className);
    
IDfWorkflowBuilder 	newWorkflowBuilder(IDfId processId);

void 	purgeLocalFiles();
    
void 	reInit(String serverConfigName);
    
void 	removeDynamicGroup(String groupName);
    
boolean 	resetPassword(String password);
    
boolean 	resetTicketKey();
    
String 	resolveAlias(IDfId sysObject, String scopeAlias);

void 	reStart(String serverConfigName, boolean restartClient);
    
IDfId 	restore(String predicate, String dumpFile, String operatorName, int priority, boolean sendMail, IDfTime dueDate);

IDfId 	sendToDistributionList(IDfList toUsers, IDfList toGroups, String instructions, IDfList objectIDs, int priority, boolean endNotification);
    
IDfId 	sendToDistributionListEx(IDfList toUsers, IDfList toGroups, String instructions, IDfList objectIDs, int priority, int flags);

void 	setAcsTransferPreferences(IDfAcsTransferPreferences acsTransferPreferences);

void 	setAliasSet(String name);

void 	setBatchHint(int batchSize);
    
String 	setDocbaseScope(String docbaseName);
    
String 	setDocbaseScopeById(IDfId objectId);
   
void 	shutdown(boolean immediate, boolean deleteEntry);

void 	traceDMCL(int level, String traceFile);

boolean 	unlock();
*/
}
