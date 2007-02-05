package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

public class DctmConnector implements Connector {

	private String login;

	private String password;

	private String docbase;

	private String clientX;

	private String webtopServerUrl;

	/**
	 * Setters and getters for the data retrieved from Spring
	 * 
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setDocbase(String docbase) {
		this.docbase = docbase;
	}

	public String getDocbase() {
		return docbase;
	}

	public void setWebtopServerUrl(String wsu) {
		this.webtopServerUrl = wsu;
	}

	public String getWebtopServerUrl() {
		return webtopServerUrl;
	}

	public void setClientX(String clientX) {
		this.clientX = clientX;
	}

	public String getClientX() {
		return clientX;
	}

	public DctmConnector() {
		;
	}

	public Session login() throws RepositoryException {
		System.out.println("--- DctmConnector login ---");

		if (DebugFinalData.debug) {
			OutputPerformances.setPerfFlag("conn",
					"DctmConnector.login() :\n\t\t\t\t Instantiates a new "
							+ "DctmSession from 9 String (~250 chars) and :",
					null);
		}
		Session sess = null;
		sess = new DctmSession(clientX, login, password, docbase,
				webtopServerUrl);

		if (DebugFinalData.debug)
			OutputPerformances.endFlag("conn",
					"return Session from DctmConnector.login()");

		return (sess);
	}

}
