package com.google.enterprise.connector.dctm.dctmmockwrap;

import java.text.MessageFormat;

import junit.framework.TestCase;

public class MockDmQueryTest extends TestCase {
	private static final String XPATH_QUERY_STRING_BOUNDED_DEFAULT = "//*[@jcr:primaryType = 'nt:resource' and @jcr:lastModified >= ''{0}'' and @jcr:uuid >= ''{1}''] order by @jcr:lastModified, @jcr:uuid";
	public void testMakeBoundedQuery(){
		String dqlStatement = "select i_chronicle_id, r_object_id, r_modify_date from dm_sysobject where r_object_type='dm_document' and r_modify_date >= 'ThisIsATestDate' and i_chronicle_id >= 'ThisIsATestId'";
		int bound1 = dqlStatement.indexOf(" and r_modify_date >= '")+" and r_modify_date >= '".length();
		int bound2 = dqlStatement.indexOf("' and i_chronicle_id >= '");
		int bound3 = bound2+"' and i_chronicle_id >= '".length();
		String date = dqlStatement.substring(bound1,bound2);
		String id = dqlStatement.substring(bound3,dqlStatement.lastIndexOf("'"));
		String test = MessageFormat.format(XPATH_QUERY_STRING_BOUNDED_DEFAULT,new Object[] {date,id});
		assertEquals(test,"//*[@jcr:primaryType = nt:resource and @jcr:lastModified >= 'ThisIsATestDate' and @jcr:uuid >= 'ThisIsATestId'] order by @jcr:lastModified, @jcr:uuid");
	}
}
