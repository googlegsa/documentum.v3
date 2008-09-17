package com.google.enterprise.connector.dctm.dfcwrap;

import com.google.enterprise.connector.spi.RepositoryException;

public interface IType {
	public static int BOOLEAN = 0;

	public static int INT = 1;

	public static int STRING = 2;

	public static int ID = 3;

	public static int TIME = 4;

	public static int DOUBLE = 5;

	public static int UNDEFINED = 6;
	
	public int 	getTypeAttrCount() throws RepositoryException;
	
	public IType getSuperType() throws RepositoryException;
    
	public IAttr getTypeAttr(int attrIndex) throws RepositoryException; 
	
	public String getDescription() throws RepositoryException; 
	
	public boolean isSubTypeOf(String type) throws RepositoryException;
	
	public String getName() throws RepositoryException;
}
