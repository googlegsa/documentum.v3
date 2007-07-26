package com.google.enterprise.connector.dctm;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.enterprise.connector.dctm.dfcwrap.IClient;
import com.google.enterprise.connector.dctm.dfcwrap.IClientX;
import com.google.enterprise.connector.dctm.dfcwrap.IDocbaseMap;
import com.google.enterprise.connector.dctm.dfcwrap.IQuery;
import com.google.enterprise.connector.spi.ConfigureResponse;
import com.google.enterprise.connector.spi.ConnectorType;
import com.google.enterprise.connector.spi.RepositoryException;

public class DctmConnectorType implements ConnectorType {

	private static final String HIDDEN = "hidden";

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

	private static final String SELECT_START = "<select";

	private static final String SELECT_END = "</select>\r\n";

	private static final String DCTMCLASS = "clientX";

	private static final String AUTHENTICATIONTYPE = "authentication_type";

	private static final String ISPUBLIC = "is_public";

	private static final String WHERECLAUSE = "where_clause";

	private static final String DOCBASENAME = "docbase";

	private static final String DISPLAYURL = "webtop_display_url";

	private static final String CHECKBOX = "CHECKBOX";

	private static final String CHECKED = "CHECKED";

	private List keys = null;

	private Set keySet = null;

	private String initialConfigForm = null;

	private static Logger logger = null;

	ResourceBundle resource;
	static {
		logger = Logger.getLogger(DctmConnectorType.class.getName());
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

	public ConfigureResponse getConfigForm(Locale language) {
		try {
			resource = ResourceBundle.getBundle("DctmConnectorType", language);
		} catch (MissingResourceException e) {
			resource = null;
			return new ConfigureResponse("",
					"The internationalization package is not installed. Please, install it.");
		}
		if (initialConfigForm != null) {
			return new ConfigureResponse("", initialConfigForm);
		}
		if (keys == null) {
			throw new IllegalStateException();
		}
		this.initialConfigForm = makeValidatedForm(null);

		return new ConfigureResponse("", initialConfigForm);
	}

	public ConfigureResponse validateConfig(Map configData, Locale language) {
		resource = ResourceBundle.getBundle("DctmConnectorType", language);
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
			logger.log(Level.INFO, "DCTM ValidateConfig");
		}
		String form = null;
		DctmSession session;
		String validation = validateConfigMap(configData);
		if (validation.equals("")) {
			try {

				if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
					logger.log(Level.INFO, "test connection to the docbase");
				}
				Properties p = new Properties();
				p.putAll(configData);
				String isPublic = (String) configData.get(ISPUBLIC);
				if (isPublic == null) {
					p.put(ISPUBLIC, "false");
				}
				Resource res = new ClassPathResource(
						"config/connectorInstanceDocumentum.xml");
				XmlBeanFactory factory = new XmlBeanFactory(res);
				PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
				cfg.setProperties(p);
				cfg.postProcessBeanFactory(factory);
				DctmConnector conn = (DctmConnector) factory
						.getBean("DctmConnectorInstance");
				session = (DctmSession) conn.login();
				testWebtopUrl((String) configData.get(DISPLAYURL));
				if ((String) configData.get(WHERECLAUSE) != null
						&& !((String) configData.get(WHERECLAUSE)).equals("")) {
					DctmTraversalManager qtm = (DctmTraversalManager) session
							.getTraversalManager();

					checkAdditionalWhereClause((String) configData
							.get("where_clause"), qtm);
				}

			} catch (RepositoryException e) {
				return createErrorMessage(configData, e);

			} finally {

			}
			return null;
		}

		form = makeValidatedForm(configData);
		return new ConfigureResponse(resource.getString(validation + "_error"),
				"<p><font color=\"#FF0000\">"
						+ resource.getString(validation + "_error")
						+ "</font></p><br>" + form);

	}

	private ConfigureResponse createErrorMessage(Map configData,
			RepositoryException e) {
		String form;
		String message = e.getMessage();
		String returnMessage = null;
		String extractErrorMessage = null;
		String bundleMessage = null;
		if (message.indexOf("[") != -1) {
			extractErrorMessage = message.substring(message.indexOf("[") + 1,
					message.indexOf("]"));
		} else {
			extractErrorMessage = e.getCause().getClass().getName();
		}
		try {
			bundleMessage = resource.getString(extractErrorMessage);
		} catch (MissingResourceException mre) {
			bundleMessage = resource.getString("DEFAULT_ERROR_MESSAGE") + " "
					+ e.getMessage();
		}
		returnMessage = "<p><font color=\"#FF0000\">" + bundleMessage
				+ "</font></p>";
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
			logger.log(Level.WARNING, returnMessage);
		}
		form = makeValidatedForm(configData);
		return new ConfigureResponse(returnMessage, returnMessage + "<br>"
				+ form);
	}

	private void checkAdditionalWhereClause(String additionalWhereClause,
			DctmTraversalManager qtm) throws RepositoryException {
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
			logger.log(Level.INFO, "check additional where clause : "
					+ additionalWhereClause);
		}
		if (!additionalWhereClause.toLowerCase().startsWith("and")) {
			throw new RepositoryException("[additional] ");
		}
		IQuery query = qtm.getClientX().getQuery();
		query
				.setDQL("select r_object_id from dm_sysobject where r_object_type='dm_document' "
						+ additionalWhereClause);
		DctmPropertyMapList result = (DctmPropertyMapList) qtm.execQuery(query);
		Iterator iter = result.iterator();
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
		if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
			logger.log(Level.INFO, "test connection to the webtop server : "
					+ webtopServerUrl);
		}
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod(webtopServerUrl);
		try {
			int status = client.executeMethod(getMethod);
			if (status != 200) {
				if (DctmConnector.DEBUG && DctmConnector.DEBUG_LEVEL >= 1) {
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

	private String validateConfigMap(Map configData) {
		for (Iterator i = keys.iterator(); i.hasNext();) {
			String key = (String) i.next();
			String val = (String) configData.get(key);
			if (!key.equals(DCTMCLASS) && !key.equals(AUTHENTICATIONTYPE)
					&& !key.equals(WHERECLAUSE) && !key.equals(ISPUBLIC)
					&& (val == null || val.length() == 0)) {
				return key;
			}
		}
		return "";
	}

	private String makeValidatedForm(Map configMap) {
		StringBuffer buf = new StringBuffer(2048);
		String value = "";
		for (Iterator i = keys.iterator(); i.hasNext();) {
			String key = (String) i.next();
			if (configMap != null) {
				value = (String) configMap.get(key);
			}
			if (key.equals(ISPUBLIC)) {
				appendCheckBox(buf, key, resource.getString(key), value);
				appendStartHiddenRow(buf);
				buf.append(OPEN_ELEMENT);
				buf.append(INPUT);
				appendAttribute(buf, TYPE, HIDDEN);
				appendAttribute(buf, VALUE, "false");
				appendAttribute(buf, NAME, key);
				appendEndRow(buf);
				value = "";
			} else {
				if (!key.equals(DCTMCLASS) && !key.equals(AUTHENTICATIONTYPE)
						&& !key.equals(WHERECLAUSE)) {
					appendStartRow(buf, resource.getString(key));
				} else {
					appendStartHiddenRow(buf);
				}
				if (key.equals(DOCBASENAME)) {
					appendDropDownListAttribute(buf, TYPE, value);
				} else {
					buf.append(OPEN_ELEMENT);
					buf.append(INPUT);
					if (key.equalsIgnoreCase(PASSWORD)) {
						appendAttribute(buf, TYPE, PASSWORD);
					} else if (key.equals(DCTMCLASS)
							|| key.equals(AUTHENTICATIONTYPE)
							|| key.equals(WHERECLAUSE)) {
						appendAttribute(buf, TYPE, HIDDEN);
					} else {
						appendAttribute(buf, TYPE, TEXT);
					}

					appendAttribute(buf, VALUE, value);
					appendAttribute(buf, NAME, key);
					appendEndRow(buf);
					value = "";
				}
			}
		}
		if (configMap != null) {
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
		}
		return buf.toString();

	}

	private void appendCheckBox(StringBuffer buf, String key, String label,
			String value) {
		buf.append(TR_START);
		buf.append(TD_START);
		buf.append(OPEN_ELEMENT);
		buf.append(INPUT);
		buf.append(" " + TYPE + "=" + CHECKBOX);
		buf.append(" " + NAME + "=\"" + key + "\" ");
		if (value != null && value.equals("on")) {
			buf.append(CHECKED);
		}
		buf.append(CLOSE_ELEMENT);
		buf.append(label + TD_END);

		buf.append(TR_END);

	}

	private void appendDropDownListAttribute(StringBuffer buf, String type2,
			String value) {
		IClientX cl = null;
		try {
			cl = (IClientX) Class
					.forName(
							"com.google.enterprise.connector.dctm.dctmdfcwrap.DmClientX")
					.newInstance();
		} catch (InstantiationException e) {
			logger
					.info("minor error while building the configuration form. The docbase will be added manually. "
							+ e.getLocalizedMessage());
			return;
		} catch (IllegalAccessException e) {
			logger
					.info("minor error while building the configuration form. The docbase will be added manually. "
							+ e.getLocalizedMessage());
			return;
		} catch (ClassNotFoundException e) {
			logger
					.info("minor error while building the configuration form. The docbase will be added manually. "
							+ e.getLocalizedMessage());
			return;
		} catch (NoClassDefFoundError e) {
			logger
					.info("minor error while building the configuration form. The docbase will be added manually. "
							+ e.getLocalizedMessage());
			return;
		}
		IClient client;
		buf.append(SELECT_START);
		buf.append(" " + NAME);

		buf.append("=\"");
		buf.append(DOCBASENAME);
		buf.append("\">\n");
		try {
			client = cl.getLocalClient();

			IDocbaseMap myMap = client.getDocbaseMap();

			for (int i = 0; i < myMap.getDocbaseCount(); i++) {
				buf.append("\t<option ");
				if (value != null && myMap.getDocbaseName(i).equals(value)) {
					buf.append("selected ");
				}
				buf.append("value=\"" + myMap.getDocbaseName(i) + "\"" + ">"
						+ myMap.getDocbaseName(i) + "</option>\n");
			}
		} catch (RepositoryException e) {

			logger
					.info("minor error while building the configuration form. The docbase will be added manually. "
							+ e.getLocalizedMessage());
		}
		buf.append(SELECT_END);

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

	public ConfigureResponse getPopulatedConfigForm(Map configMap,
			Locale language) {
		resource = ResourceBundle.getBundle("DctmConnectorType", language);
		ConfigureResponse result = new ConfigureResponse("",
				makeValidatedForm(configMap));
		return result;
	}

}
