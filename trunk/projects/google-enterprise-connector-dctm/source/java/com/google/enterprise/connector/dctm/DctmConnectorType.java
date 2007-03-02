package com.google.enterprise.connector.dctm;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.spi.ConfigureResponse;
import com.google.enterprise.connector.spi.ConnectorType;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.SimpleConnectorType;

public class DctmConnectorType extends SimpleConnectorType implements
		ConnectorType {
	private static final String HIDDEN = "hidden";

	private static final String STARS = "*****";

	private static final String VALUE = "value";

	private static final String NAME = "name";

	private static final String TEXT = "text";

	private static final String TYPE = "type";

	private static final String INPUT = "input";

	private static final String CLOSE_ELEMENT = "/>";

	private static final String OPEN_ELEMENT = "<";

	private static final String PASSWORD = "password";

	private static final String TR_END = "</tr>\r\n";

	private static final String TD_END = "</td>\r\n";

	private static final String TD_START = "<td>";

	private static final String TR_START = "<tr>\r\n";

	private static final String DCTMCLASS = "clientX";

	private List keys = null;

	private Set keySet = null;

	private String initialConfigForm = null;

	private static HashMap mapError = null;

	private static Logger logger = null;

	static {

		mapError = new HashMap();
		mapError
				.put("DM_DOCBROKER_E_NO_SERVERS_FOR_DOCBASE",
						"Some required configuration is missing: Docbase name is incorrect.");
		mapError
				.put("DM_SESSION_E_START_FAIL",
						"Some required configuration is missing: Please check the credentials.");
		mapError
				.put(
						"status",
						"Some required configuration is missing: Please check the webtop server url and that the server is up and running.");
		mapError
				.put("IOException",
						"Some required configuration is missing: Please check the webtop server url.");
		mapError
				.put("HttpException",
						"Some required configuration is missing: Please check the webtop server url.");
		mapError
				.put(
						"additional",
						"Some required configuration is missing: Please check the additional where clause.");
		mapError
				.put("DM_QUERY_E_NOT_ATTRIBUTE",
						"Some required configuration is missing: Syntax error in DQL filter :");
		mapError
				.put(
						"additionalTooRestrictive",
						"Some required configuration is missing: DQL Filter is too restrictive, no documents were found with this filter.");

	}

	/**
	 * Set the keys that are required for configuration. One of the overloadings
	 * of this method must be called exactly once before the SPI methods are
	 * used.
	 * 
	 * @param keys
	 *            A list of String keys
	 */
	public void setConfigKeys(List keys) {
		if (this.keys != null) {
			throw new IllegalStateException();
		}
		this.keys = keys;
		this.keySet = new HashSet(keys);

	}

	/**
	 * Set the keys that are required for configuration. One of the overloadings
	 * of this method must be called exactly once before the SPI methods are
	 * used.
	 * 
	 * @param keys
	 *            An array of String keys
	 */
	public void setConfigKeys(String[] keys) {
		setConfigKeys(Arrays.asList(keys));
	}

	public ConfigureResponse getConfigForm(String language) {
		if (initialConfigForm != null) {
			return new ConfigureResponse("", initialConfigForm);
		}
		if (keys == null) {
			throw new IllegalStateException();
		}
		this.initialConfigForm = makeConfigForm(null);

		return new ConfigureResponse("", initialConfigForm);
	}

	public ConfigureResponse validateConfig(Map configData, String language) {
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 1) {
			logger.log(Level.INFO, "DCTM ValidateConfig");
		}
		String form = null;
		if (validateConfigMap(configData)) {
			try {
				if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 1) {
					logger.log(Level.INFO, "test connection to the docbase");
				}
				DctmSession session = new DctmSession(
						"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX",
						(String) configData.get("login"), (String) configData
								.get("password"), (String) configData
								.get("docbase"), (String) configData
								.get("webtopServerUrl"), (String) configData
								.get("additionalWhereClause"));

				DctmAuthenticationManager authentManager = (DctmAuthenticationManager) session
						.getAuthenticationManager();
				authentManager.authenticate((String) configData.get("login"),
						(String) configData.get("password"));

				testWebtopUrl((String) configData.get("webtopServerUrl"));
				if ((String) configData.get("additionalWhereClause") != null
						&& !((String) configData.get("additionalWhereClause"))
								.equals("")) {
					DctmQueryTraversalManager qtm = (DctmQueryTraversalManager) session
							.getQueryTraversalManager();
					checkAdditionalWhereClause((String) configData
							.get("additionalWhereClause"), qtm);
				}
			} catch (RepositoryException e) {
				String message = e.getMessage();
				String returnMessage = null;
				String extractErrorMessage = message.substring(message
						.indexOf("[") + 1, message.indexOf("]"));
				if (mapError.containsKey(extractErrorMessage)) {
					returnMessage = (String) mapError.get(extractErrorMessage)
							+ " " + e.getMessage();
				}
				if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 1) {
					logger.log(Level.WARNING, returnMessage);
				}
				form = makeValidatedForm(configData);
				return new ConfigureResponse(returnMessage, form);

			}
			return new ConfigureResponse(null, null);
		}
		form = makeValidatedForm(configData);
		return new ConfigureResponse("Some required configuration is missing",
				form);

	}

	private void checkAdditionalWhereClause(String additionalWhereClause,
			DctmQueryTraversalManager qtm) throws RepositoryException {
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 1) {
			logger.log(Level.INFO, "check additional where clause : "
					+ additionalWhereClause);
		}
		if (!additionalWhereClause.toLowerCase().startsWith("and")) {
			throw new RepositoryException("[additional]");
		}
		IQuery query = qtm.getClientX().getQuery();
		query
				.setDQL("select r_object_id from dm_sysobject where r_object_type='dm_document' "
						+ additionalWhereClause);
		DctmResultSet restult = (DctmResultSet) qtm.execQuery(query);
		Iterator iter = restult.iterator();
		int counter = 0;
		while (iter.hasNext()) {
			iter.next();
			counter++;
			break;
		}
		if (counter == 0) {
			throw new RepositoryException("[additionalTooRestrictive]");
		}

	}

	private void testWebtopUrl(String webtopServerUrl)
			throws RepositoryException {
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 1) {
			logger.log(Level.INFO, "test connection to the webtop server : "
					+ webtopServerUrl);
		}
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod(webtopServerUrl);
		try {
			int status = client.executeMethod(getMethod);
			if (status != 200) {
				if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL == 1) {
					logger.log(Level.INFO, "status " + status);
				}
				throw new RepositoryException(
						"[status] Http request returned a " + status
								+ " status");
			}
		} catch (HttpException e) {
			RepositoryException re = new RepositoryException("[HttpException]",
					e);
			throw new RepositoryException(re);
		} catch (IOException e) {
			RepositoryException re = new RepositoryException("[IOException]", e);
			throw new RepositoryException(re);
		}

	}

	private boolean validateConfigMap(Map configData) {
		for (Iterator i = keys.iterator(); i.hasNext();) {
			String key = (String) i.next();
			String val = (String) configData.get(key);
			if (val == null || val.length() == 0) {
				return false;
			}
		}
		return true;
	}

	private String makeValidatedForm(Map configMap) {
		StringBuffer buf = new StringBuffer(2048);
		for (Iterator i = keys.iterator(); i.hasNext();) {
			String key = (String) i.next();
			appendStartRow(buf, key);

			String value = (String) configMap.get(key);
			if (value == null) {
				buf.append(OPEN_ELEMENT);
				buf.append(INPUT);
				if (key.equalsIgnoreCase(PASSWORD)) {
					appendAttribute(buf, TYPE, PASSWORD);
				} else {
					appendAttribute(buf, TYPE, TEXT);
				}
			} else {
				if (key.equalsIgnoreCase(PASSWORD)) {
					buf.append(STARS);
				} else {
					buf.append(value);
				}
				buf.append(OPEN_ELEMENT);
				buf.append(INPUT);
				appendAttribute(buf, TYPE, HIDDEN);
				appendAttribute(buf, VALUE, value);
			}
			appendAttribute(buf, NAME, key);
			appendEndRow(buf);
		}
		// toss in all the stuff that's in the map but isn't in the keyset
		// taking care to list them in alphabetic order (this is mainly for
		// testability).
		Iterator i = new TreeSet(configMap.keySet()).iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			if (!keySet.contains(key)) {
				// add another hidden field to preserve this data
				String val = (String) configMap.get(key);
				buf.append("<input type=\"hidden\" value=\"");
				buf.append(val);
				buf.append("\" name=\"");
				buf.append(key);
				buf.append("\"/>\r\n");
			}
		}
		return buf.toString();

	}

	private void appendStartRow(StringBuffer buf, String key) {
		buf.append(TR_START);
		buf.append(TD_START);
		buf.append(key);
		buf.append(TD_END);
		buf.append(TD_START);
	}

	private void appendStartHiddenRow(StringBuffer buf) {
		buf.append(TR_START);
		buf.append(TD_START);

	}

	private void appendEndRow(StringBuffer buf) {
		buf.append(CLOSE_ELEMENT);
		buf.append(TD_END);
		buf.append(TR_END);
	}

	private void appendAttribute(StringBuffer buf, String attrName,
			String attrValue) {
		buf.append(" ");
		buf.append(attrName);
		buf.append("=\"");
		// TODO xml-encode the special characters (< > " etc.)
		buf.append(attrValue);
		buf.append("\"");
	}

	/**
	 * Make a config form snippet using the keys (in the supplied order) and, if
	 * passed a non-null config map, pre-filling values in from that map
	 * 
	 * @param configMap
	 * @return config form snippet
	 */
	private String makeConfigForm(Map configMap) {
		StringBuffer buf = new StringBuffer(2048);
		for (Iterator i = keys.iterator(); i.hasNext();) {
			String key = (String) i.next();
			if (!key.equals(DCTMCLASS)) {
				appendStartRow(buf, key);
			} else {
				appendStartHiddenRow(buf);
			}
			buf.append(OPEN_ELEMENT);
			buf.append(INPUT);
			if (key.equalsIgnoreCase(PASSWORD)) {
				appendAttribute(buf, TYPE, PASSWORD);
			} else if (key.equals(DCTMCLASS)) {
				appendAttribute(buf, TYPE, HIDDEN);
			} else {
				appendAttribute(buf, TYPE, TEXT);
			}

			appendAttribute(buf, NAME, key);

			if (configMap != null) {
				String value = (String) configMap.get(key);
				if (value != null) {
					appendAttribute(buf, VALUE, value);
				}
			}
			appendEndRow(buf);
		}
		return buf.toString();
	}

}
