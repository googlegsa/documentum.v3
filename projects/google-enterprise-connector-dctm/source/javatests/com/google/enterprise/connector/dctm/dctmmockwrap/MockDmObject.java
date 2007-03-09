package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;

import javax.jcr.ValueFormatException;

import com.google.enterprise.connector.dctm.DctmSysobjectProperty;
import com.google.enterprise.connector.dctm.DctmSysobjectValue;
import com.google.enterprise.connector.dctm.dfcwrap.IAttr;
import com.google.enterprise.connector.dctm.dfcwrap.IFormat;
import com.google.enterprise.connector.dctm.dfcwrap.IId;
import com.google.enterprise.connector.dctm.dfcwrap.ISysObject;
import com.google.enterprise.connector.dctm.dfcwrap.ITime;
import com.google.enterprise.connector.mock.MockRepositoryDateTime;
import com.google.enterprise.connector.mock.MockRepositoryDocument;
import com.google.enterprise.connector.mock.MockRepositoryProperty;
import com.google.enterprise.connector.mock.MockRepositoryPropertyList;
import com.google.enterprise.connector.mock.jcr.MockJcrRepository;
import com.google.enterprise.connector.mock.jcr.MockJcrSession;
import com.google.enterprise.connector.mock.jcr.MockJcrValue;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SpiConstants;
import com.google.enterprise.connector.spi.ValueType;
	
public class MockDmObject implements ISysObject {
	private MockRepositoryDocument mockDocument;
	
	public MockDmObject(MockRepositoryDocument mRD) {
		this.mockDocument = mRD;
	}
	
	public long getContentSize() throws RepositoryException {
		//TODO Auto-generated method stub
		ByteArrayInputStream contentStream=null;
		int avail=0;
		try {
			contentStream=(ByteArrayInputStream)mockDocument.getContentStream();
			avail=contentStream.available();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return avail;
	}

	public ByteArrayInputStream getContent() throws RepositoryException {
		ByteArrayInputStream contentStream=null;
		try {
			contentStream=(ByteArrayInputStream)mockDocument.getContentStream();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return contentStream;
		/*
		MockRepositoryPropertyList pml=mockDocument.getProplist();
		String nom=null;
		String valeur=null;
		
		for(Iterator myIt=pml.iterator();myIt.hasNext();){
			MockRepositoryProperty myPm=(MockRepositoryProperty)myIt.next();
			nom=myPm.getName();
			System.out.println("le nom vaut "+nom);
			valeur=myPm.getValue();
			System.out.println("la valeur vaut "+valeur);
		}
		
		///MockRepositoryProperty pm=mockDocument.getProplist().getProperty("jcr:content");
		///MockRepositoryProperty pm=mockDocument.getProplist().getProperty("google:ispublic");
		String name= pm.getName();
		System.out.println("name vaut "+name);
		MockJcrValue propVal=new MockJcrValue(pm);
		ByteArrayInputStream propISVal=null;
		try {
			propISVal=(ByteArrayInputStream)propVal.getStream();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (javax.jcr.RepositoryException e) {
			e.printStackTrace();
		}
		return propISVal;
		*/
		
		
	}

	public String getACLDomain() throws RepositoryException {
		// TODO Auto-generated method stub
		System.out.println("getACLDomain");
		return "ACLDomain";
	}

	public String getACLName() throws RepositoryException {
		// TODO Auto-generated method stub
		System.out.println("getACLName");
		return "ACLName";
	}

	public String getString(String name) throws RepositoryException{
		///faire les remplacements requis entre attributs Mock et attributs Dctm
		String propStrVal=null;
		if (name.equals("object_name")){
			name="name";
			MockRepositoryProperty pm=mockDocument.getProplist().getProperty(name);
			MockJcrValue propVal=new MockJcrValue(pm);
			try {
				propStrVal=propVal.getString();
			} catch (ValueFormatException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (javax.jcr.RepositoryException e) {
				e.printStackTrace();
			}
		}else if(name.equals(SpiConstants.PROPNAME_DOCID)){
			name="docid";
			propStrVal=mockDocument.getDocID();
		}else if(name.equals(SpiConstants.PROPNAME_SECURITYTOKEN)){
			name="acl";
			MockRepositoryProperty pm=mockDocument.getProplist().getProperty(name);
			MockJcrValue propVal=new MockJcrValue(pm);
			try {
				propStrVal=propVal.getString();
			} catch (ValueFormatException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (javax.jcr.RepositoryException e) {
				e.printStackTrace();
			}
		}else{
			MockRepositoryProperty pm=mockDocument.getProplist().getProperty(name);
			MockJcrValue propVal=new MockJcrValue(pm);
			try {
				propStrVal=propVal.getString();
			} catch (ValueFormatException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (javax.jcr.RepositoryException e) {
				e.printStackTrace();
			}
		}
	
		return propStrVal;
	/*
	else if (SpiConstants.PROPNAME_DISPLAYURL.equals(name)) {
		return new DctmSysobjectProperty(name, new DctmSysobjectValue(
				ValueType.STRING, sessionManager.getServerUrl() + docid));
		SpiConstants.PROPNAME_SECURITYTOKEN
		return new DctmSysobjectProperty(name, new DctmSysobjectValue(
				ValueType.STRING, object.getACLDomain() + " "
		SpiConstants.PROPNAME_ISPUBLIC
		SpiConstants.PROPNAME_LASTMODIFY
		return new DctmSysobjectProperty(name, new DctmSysobjectValue(
				object, "r_modify_date", ValueType.DATE));
		SpiConstants.PROPNAME_MIMETYPE
		dctmForm = object.getFormat();
		mimetype = dctmForm.getMIMEType();
		return new DctmSysobjectProperty(name, new DctmSysobjectValue(
				ValueType.STRING, mimetype));
		SpiConstants.PROPNAME_SEARCHURL
		SpiConstants.PROPNAME_AUTH_VIEWPERMIT
	*/	
		
		
	
	}

	public int getInt(String name) throws RepositoryException {
		MockRepositoryProperty pm=mockDocument.getProplist().getProperty(name);
		MockJcrValue propVal=new MockJcrValue(pm);
		int propIntVal=0;
		try {
			propIntVal=(int)propVal.getLong();
		} catch (ValueFormatException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (javax.jcr.RepositoryException e) {
			e.printStackTrace();
		}
		return propIntVal;
	}

	public ITime getTime(String name) throws RepositoryException {
		// TODO Auto-generated method stub
		//return null;
		Date propDateVal=null;
		if (name.equals("r_modify_date")){
			System.out.println("name origine vaut "+name);
			name="google:lastmodify";
		}
		
		MockRepositoryProperty pm=mockDocument.getProplist().getProperty(name);
		long time=0;
		if (pm==null){
			System.out.println("pm vaut null");
			MockRepositoryDateTime dateTime=mockDocument.getTimeStamp();
			System.out.println("datetime vaut "+dateTime);
			time=dateTime.getTicks();
			System.out.println("time vaut "+time);
			propDateVal=new Date(time);
			System.out.println("propDateVal vaut "+propDateVal.getTime());
		}else{
			String propVal=pm.getValue();
			System.out.println("propVal vaut "+propVal);
			SimpleDateFormat simple=new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z",new Locale("EN"));
			ParsePosition myPos=new ParsePosition(0);
			///Date propDateVal=simple.parse("Tue, 15 Nov 1994 12:45:26 GMT",myPos);
			propDateVal=simple.parse(propVal,myPos);
			time=propDateVal.getTime();
			System.out.println("time vaut "+time);
		}	
	
		return new MockDmTime(propDateVal);
		///return new MockDmTime(time);
	}

	public double getDouble(String name) throws RepositoryException {
		MockRepositoryProperty pm=mockDocument.getProplist().getProperty(name);
		MockJcrValue propVal=new MockJcrValue(pm);
		double propDblVal=0;
		try {
			propDblVal=propVal.getDouble();
		} catch (ValueFormatException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (javax.jcr.RepositoryException e) {
			e.printStackTrace();
		}
		return propDblVal;
	}

	public boolean getBoolean(String name) throws RepositoryException {
		String newName="";
		if (name.equals(SpiConstants.PROPNAME_ISPUBLIC)){
			newName="google:ispublic";
		}
		MockRepositoryProperty pm=mockDocument.getProplist().getProperty(name);
		MockJcrValue propVal=new MockJcrValue(pm);
		boolean propBlVal=true;
		try {
			propBlVal=propVal.getBoolean();
		} catch (ValueFormatException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (javax.jcr.RepositoryException e) {
			e.printStackTrace();
		}
		return propBlVal;
	}

	public IId getId(String id) throws RepositoryException {
		return new MockDmId(id);
	}

	public IFormat getFormat() throws RepositoryException {
		///return new MockDmFormat("text/plain");
		return new MockDmFormat("application/octet-stream");
	}

	public int getAttrDataType(String name) throws RepositoryException {
		MockRepositoryProperty pm=mockDocument.getProplist().getProperty(name);
		MockJcrValue propVal=new MockJcrValue(pm);
		return propVal.getType();
	}

	public int getAttrCount() throws RepositoryException {
		MockRepositoryPropertyList Mockpm=mockDocument.getProplist();
		MockRepositoryProperty pm=null;
		int counter=0;
		for (Iterator mockIt=Mockpm.iterator(); mockIt.hasNext();) {
			pm=(MockRepositoryProperty)mockIt.next();
			String name=pm.getName();
			System.out.println("name vaut "+name);
			counter++;
		}
		return counter;
	}

	public IAttr getAttr(int attrIndex) throws RepositoryException {
		MockRepositoryPropertyList Mockpm=mockDocument.getProplist();
		MockRepositoryProperty pm=null;
		int counter=0;
		for (Iterator mockIt=Mockpm.iterator(); mockIt.hasNext();) {
			pm=(MockRepositoryProperty)mockIt.next();
			if (counter==attrIndex){
				return new MockDmAttr(pm);
			}
			counter++;
		}
		return null;
	}
	
}
