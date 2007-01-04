package com.google.enterprise.connector.dctm.dctmdfcwrap;

import com.google.enterprise.connector.dctm.DctmProperty;
import com.google.enterprise.connector.dctm.DctmPropertyMap;
import com.google.enterprise.connector.dctm.DctmResultSet;
import com.google.enterprise.connector.dctm.DctmSimpleValue;
import com.google.enterprise.connector.dctm.dfcwrap.ICollection;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISession;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITypedObject;
import com.google.enterprise.connector.dctm.dfcwrap.IValue;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.ResultSet;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.ValueType;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfValue;

public class IDctmCollection extends IDctmTypedObject implements ICollection {
	
	IDfCollection idfCollection;

	public IDctmCollection(IDfCollection idfCollection) {
		super(idfCollection);

		this.idfCollection = idfCollection;

	}

	public IValue getValue(String attrName) {
		IDfValue dfValue = null;
		try {
			dfValue = idfCollection.getValue(attrName);
		} catch (DfException de) {
			de.getMessage();
		}
		return new IDctmValue(dfValue);
	}

	public boolean next() {
		boolean rep = false;
		try {
			rep = idfCollection.next();
		} catch (DfException de) {
			de.getMessage();
		}
		return (rep);
	}

	public ITypedObject getTypedObject() {
		IDfTypedObject dfTypedObj = null;
		try {
			dfTypedObj = idfCollection.getTypedObject();
		} catch (DfException de) {
			de.getMessage();
		}
		return new IDctmTypedObject(dfTypedObj);
	}

	public IDfCollection getIDfCollection() {
		return idfCollection;
	}

	public IId getObjectId() {
		IId id = null;
		try {
			id = new IDctmId(this.idfCollection.getObjectId());
		} catch (DfException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
		return id;
	}

	public String getString(String colName) {
		try {
			return this.idfCollection.getString(colName);
		} catch (DfException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet buildResulSetFromCollection(ISession session) {
		String modifDate = null;
		String crID = null;
		String mimetype = null;
		DctmPropertyMap pm = null;
		IDctmSysObject dctmSysObj = null;
		IFormat dctmForm = null;
		IDctmValue val = null;
		DctmResultSet resu = new DctmResultSet();
		// Building the IDctmCollection for error management only
		ICollection col = new IDctmCollection(idfCollection);
		try {
			while (col.next()) {
				pm = new DctmPropertyMap();
				crID = col.getValue("i_chronicle_id").asString();
				pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_DOCID,
						new DctmSimpleValue(ValueType.STRING, crID)));
				val = (IDctmValue) col.getValue("r_modify_date");

				modifDate = val.asTime().asString(IDctmTime.DF_TIME_PATTERN45);
				pm.putProperty(new DctmProperty(
						SpiConstants.PROPNAME_LASTMODIFY, new DctmSimpleValue(
								ValueType.DATE, modifDate)));
				
//				modifDate = itime.asString(IDctmTime.DF_TIME_PATTERN45);
				// Date mydate=itime.getDate();
				//System.out.println("modifdate vaut "+modifDate);
				// vlDate=new DctmValue(ValueType.DATE,modifDate);

				dctmSysObj = (IDctmSysObject) session
						.getObject(new IDctmId(crID));
				dctmForm = (IDctmFormat) dctmSysObj.getFormat();
				

				if (dctmForm.canIndex()) {
					mimetype = dctmForm.getMIMEType();
					
					
					pm.putProperty(new DctmProperty(
							SpiConstants.PROPNAME_MIMETYPE, new DctmSimpleValue(
									ValueType.STRING, mimetype)));
				}
				pm.putProperty(new DctmProperty(
						SpiConstants.PROPNAME_CONTENT, new DctmSimpleValue(
								ValueType.BINARY, dctmSysObj)));
				resu.add(pm);
			}
		} catch (RepositoryException re) {
			System.out.println(re.getMessage());
		}
		return resu;
	}

	// public ResultSet buildResulSetFromCollection(ISession session) {
	// String modifDate=null;
	// String crID=null;
	// String mimetype=null;
	// DctmValue vlDate=null;
	// DctmValue vlID=null;
	// DctmValue vlMime=null;
	// DctmPropertyMap pm=null;
	// ByteArrayInputStream content=null;
	// int size=0;
	// byte[] bufContent;
	// //ISession dctmSes = getIdctmses();
	// ISysObject dctmSysObj = null;
	// IFormat dctmForm = null;
	// IDctmValue val=null;
	// ITime itime=null;
	// DctmResultSet resu=new DctmResultSet();
	// //Building the IDctmCollection for error management only
	// ICollection col = new IDctmCollection(idfCollection);
	// try{
	// while (col.next()){
	// pm=new DctmPropertyMap();
	// crID = col.getValue("i_chronicle_id").asString();
	// int rep_Id=col.getValue("i_chronicle_id").getDataType();
	// vlID=new DctmValue(ValueType.STRING,crID);
	// pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_DOCID,vlID));
	//	
	// val=(IDctmValue)col.getValue("r_modify_date");
	// int rep=val.getDataType();
	// itime=val.asTime();
	// modifDate = itime.asString(IDctmTime.DF_TIME_PATTERN45);
	// Date mydate=itime.getDate();
	// ///System.out.println("modifdate vaut "+modifDate);
	// vlDate=new DctmValue(ValueType.DATE,modifDate);
	// pm.putProperty(new
	// DctmProperty(SpiConstants.PROPNAME_LASTMODIFY,vlDate));
	// dctmSysObj =
	// (IDctmSysObject)session.getObjectByQualification("dm_document where
	// i_chronicle_id = '" + crID + "'");
	// dctmForm = (IDctmFormat)dctmSysObj.getFormat();
	// if(dctmForm.canIndex()){
	// content=dctmSysObj.getContent();
	// mimetype=dctmForm.getMIMEType();
	// size=new Long(dctmSysObj.getContentSize()).intValue();
	// bufContent = new byte[size];
	// ByteArrayOutputStream output=new ByteArrayOutputStream();
	// try{
	// int count=-2;
	// while ((count = content.read(bufContent)) > -1){
	// output.write(bufContent, 0, count);
	// }
	// content.close();
	// }catch(IOException ie){
	// System.out.println(ie.getMessage());
	// }
	//	
	// DctmValue vlCont=null;
	// if(bufContent.length>0){
	// vlCont=new DctmValue(ValueType.BINARY,bufContent);
	// pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_CONTENT,vlCont));
	// }else{
	// vlCont=new DctmValue(ValueType.BINARY,"");
	// pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_CONTENT,vlCont));
	// }
	// }
	// vlMime=new DctmValue(ValueType.STRING,mimetype);
	// pm.putProperty(new DctmProperty(SpiConstants.PROPNAME_MIMETYPE,vlMime));
	// resu.add(pm);
	// }
	// }catch(RepositoryException re){
	//	System.out.println(re.getMessage());
	//	}
	//	int nb=resu.size();
	//	System.out.println("nb vaut "+nb);
	//	return resu;
	//	}
	//	}
}
