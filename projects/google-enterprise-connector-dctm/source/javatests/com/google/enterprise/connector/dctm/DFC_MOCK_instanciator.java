package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dfcwrap.IQuery;

public class DFC_MOCK_instanciator {
	private static final boolean isDFCavailable=true;
	public static IQuery getIQueryObject(){
		IQuery qObj = null;
		if (isDFCavailable) {
			try {
				qObj = (IQuery) Class.forName("com.google.enterprise.connector.dctm.dctmdfcwrap").newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				qObj = (IQuery) Class.forName("com.google.enterprise.connector.dctm.dctmmockwrap").newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (qObj!=null) return qObj;
		else return null;//Dans un premier temps
	}
}
