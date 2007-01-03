package com.google.enterprise.connector.dctm.dctmmockwrap;


import java.util.Calendar;
import java.util.Iterator;

import com.google.enterprise.connector.dctm.DctmProperty;
import com.google.enterprise.connector.dctm.DctmPropertyMap;
import com.google.enterprise.connector.dctm.DctmResultSet;
import com.google.enterprise.connector.dctm.DctmValue;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmFormat;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmSysObject;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmTime;
import com.google.enterprise.connector.dctm.dctmdfcwrap.IDctmValue;
import com.google.enterprise.connector.dctm.dfcwrap.*;
import com.google.enterprise.connector.jcradaptor.SpiResultSetFromJcr;
import com.google.enterprise.connector.spi.PropertyMap;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.Value;
import com.google.enterprise.connector.spi.ValueType;

import javax.jcr.query.QueryResult;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

public class DctmMockCollection implements ICollection {
	private NodeIterator collection;
	private Node currentNode;
	public DctmMockCollection(QueryResult mjQueryResult){
		try {
			collection = mjQueryResult.getNodes();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Needed as next() is called in DctmQTM. Will no longer be needed soon:
	//buildResultSet() method intends to avoid the necessity of parsing the collection
	//in a class common to DFC and Mock but rather in the Collection object (this)
	public boolean next(){
		if (collection.hasNext()){
			currentNode = collection.nextNode();
			return true;
		}
		return false;
	}
	
	//Needed as getValue() is called in DctmQTM. Will no longer be needed soon:
	//BuildResSet method intends to avoid the necessity of parsing the collection
	//in a class common to DFC and Mock but rather in the Collection object (this)
	public IValue getValue(String attrName){
		if (currentNode==null){
			return null;
		}else {
			String mockArg = "";
			//DFC calls a determined number of values.
			//The mapping between DFC properties names and MockRepository ones has to be done
			//This mapping comes from the com.google.enterprise.connector.jcradaptor.SpiPropertyMapFromJcr.
			if (attrName.equals("i_chronicle_id")){
				mockArg="jcr:uuid";
			}else if (attrName.equals("r_modify_date")){
				mockArg="jcr:lastModified";
			}
			
			try {
				Property py3 = currentNode.getProperty(SpiConstants.PROPNAME_LASTMODIFY);
				Property py = currentNode.getProperty(mockArg);
				return new DctmMockValue(py);
			} catch (PathNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public ResultSet buildResulSetFromCollection(ISession session) {
		SpiResultSetFromJcr test = new SpiResultSetFromJcr(collection);
		Iterator it=null;
		try {
			it = test.iterator();
		} catch (com.google.enterprise.connector.spi.RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PropertyMap pmu = (PropertyMap) it.next();
		try {
			com.google.enterprise.connector.spi.Property prop = pmu.getProperty(SpiConstants.PROPNAME_LASTMODIFY);
			String u = prop.getName();
			Value iu = prop.getValue();
			Calendar c = iu.getDate();
			long ooo = c.getTimeInMillis();
			String a = "pisdfgjoi";
		} catch (com.google.enterprise.connector.spi.RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		String modifDate = null;
		String crID = null;
		String mimetype = null;		
		DctmPropertyMap pm = null;
		ISysObject dctmSysObj = null;
		IFormat dctmForm = null;
		IDctmValue val = null;
		DctmResultSet resu = new DctmResultSet();
		while (this.next()){
			try{
				pm=new DctmPropertyMap();
				
				crID = this.getValue("i_chronicle_id").asString();
				pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_DOCID,new DctmValue(ValueType.STRING,crID)));
				
				val=(IDctmValue)this.getValue("r_modify_date");
				modifDate = val.asTime().asString(IDctmTime.DF_TIME_PATTERN45);
				pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_LASTMODIFY,new DctmValue(ValueType.DATE,modifDate)));
				
				dctmSysObj = session.getObject(new DctmMockId(crID));
				dctmForm = (IDctmFormat)dctmSysObj.getFormat();
				
				if(dctmForm.canIndex()){
					mimetype = dctmForm.getMIMEType();
					pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_CONTENT,new DctmValue(ValueType.BINARY,"")));
					pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_MIMETYPE,new DctmValue(ValueType.STRING,mimetype)));
				}
				
				resu.add(pm);
			}catch(com.google.enterprise.connector.spi.RepositoryException e){
				System.out.println(e.getMessage());
				return null;
			}
		}
		return resu;
	}

	public ITypedObject getTypedObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public IId getObjectId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(String colName) {
		// TODO Auto-generated method stub
		return null;
	}
	
}