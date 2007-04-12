package com.google.enterprise.connector.dctm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
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

	private String webtop_server_url;

	private String authentication_type;

	private String where_clause;

	private static Logger logger = null;

	public static boolean DEBUG = true;

	public static int DEBUG_LEVEL = 1;

	static {
		logger = Logger.getLogger(DctmConnector.class.getName());

		File propertiesFile = new File("../config/logging.properties");
		Properties properties = null;
		FileInputStream fileInputStream = null;
		if (propertiesFile.isFile() == true) {
			try {
				fileInputStream = new FileInputStream(propertiesFile);
			} catch (FileNotFoundException e) {
				logger.setLevel(Level.OFF);
			}
			if (fileInputStream != null) {
				properties = new Properties();
				try {
					properties.load(fileInputStream);
					DEBUG = properties.getProperty("DEBUG").equals("true");
					DEBUG_LEVEL = Integer.parseInt(properties
							.getProperty("LEVEL"));
				} catch (IOException e) {

					fileInputStream = null;
					properties = null;
				} finally {
					try {
						fileInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();

					}
				}
			}
		}

	}

	/**
	 * Setters for the data retrieved from Spring
	 * 
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDocbase(String docbase) {
		this.docbase = docbase;
	}

	public void setWebtop_server_url(String wsu) {
		this.webtop_server_url = wsu;
	}

	public void setClientX(String clientX) {
		this.clientX = clientX;
	}

	public DctmConnector() {
		;
	}

	public Session login() throws RepositoryException {
		if (DEBUG && DEBUG_LEVEL == 1) {
			logger.log(Level.INFO, "login in the docbase " + docbase
					+ " and user " + login);
		}

		if (DEBUG && DEBUG_LEVEL == 4) {
			OutputPerformances.setPerfFlag("conn",
					"DctmConnector.login() :\n\t\t\t\t Instantiates a new "
							+ "DctmSession from 9 String (~250 chars) and :",
					null);
		}
		Session sess = null;
		sess = new DctmSession(clientX, login, password, docbase,
				webtop_server_url, where_clause);

		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 4) {
			OutputPerformances.endFlag("conn",
					"return Session from DctmConnector.login()");
		}

		return (sess);
	}

	public void setAuthentication_type(String authenticationType) {
		this.authentication_type = authenticationType;
	}

	public void setWhere_clause(String additionalWhereClause) {
		this.where_clause = additionalWhereClause;
	}

}
