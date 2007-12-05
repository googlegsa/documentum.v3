package com.google.enterprise.connector.dctm;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

public class DctmConnector implements Connector {

	private String login;

	private String password;

	private String docbase;

	private String clientX;

	private String webtop_display_url;

	private String authentication_type;

	private String where_clause;

	private String is_public;

	private HashSet included_meta;

	private HashSet excluded_meta;

	private HashSet included_object_type;

	private String root_object_type;

	private static Logger logger = null;

	static {
		logger = Logger.getLogger(DctmConnector.class.getName());
	}

	/**
	 * Setters for the data retrieved from Spring
	 * 
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	public void setDocbase(String docbase) {
		this.docbase = docbase;
	}

	public void setWebtop_display_url(String wsu) {
		this.webtop_display_url = wsu;
	}

	public void setClientX(String clientX) {
		this.clientX = clientX;
	}

	public DctmConnector() {
		;
	}

	public Session login() throws RepositoryException {
		logger.log(Level.INFO, "login in the docbase " + docbase + " and user "
				+ login + " " + clientX + " " + docbase + " "
				+ webtop_display_url + " " + where_clause + " "
				+ is_public.equals("on"));

		Session sess = null;
		sess = new DctmSession(clientX, login, password, docbase,
				webtop_display_url, where_clause, is_public.equals("on"),
				included_meta, excluded_meta, root_object_type,
				included_object_type);

		return sess;
	}

	public void setAuthentication_type(String authenticationType) {
		this.authentication_type = authenticationType;
	}

	public void setWhere_clause(String additionalWhereClause) {
		this.where_clause = additionalWhereClause;
	}

	public String getIs_public() {
		return is_public;
	}

	public void setIs_public(String is_public) {
		this.is_public = is_public;
	}

	public String getAuthentication_type() {
		return authentication_type;
	}

	public HashSet getIncluded_meta() {

		return included_meta;
	}

	public void setIncluded_meta(HashSet included_meta) {
		this.included_meta = included_meta;
	}

	public HashSet getExcluded_meta() {
		return excluded_meta;
	}

	public void setExcluded_meta(HashSet excluded_meta) {
		this.excluded_meta = excluded_meta;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setIncluded_object_type(HashSet included_object_type) {
		this.included_object_type = included_object_type;
	}

	public void setRoot_object_type(String root_object_type) {
		this.root_object_type = root_object_type;
	}

}
