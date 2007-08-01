package com.google.enterprise.connector.dctm.dfcwrap;

public interface IAttr {
	
	public static final int DM_BOOLEAN = 0;
    public static final int DM_INTEGER = 1;
    public static final int DM_STRING = 2;
    public static final int DM_ID = 3;
    public static final int DM_TIME = 4;
    public static final int DM_DOUBLE = 5;
    public static final int DM_UNDEFINED = 6;
	public String getName();
	public int getDataType();
}
