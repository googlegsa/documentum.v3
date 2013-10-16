package com.google.enterprise.connector.dctm;

import com.google.enterprise.connector.dctm.dctmmockwrap.DmInitialize;
import com.google.enterprise.connector.spi.AuthenticationIdentity;
import com.google.enterprise.connector.spi.SimpleAuthenticationIdentity;

import junit.framework.TestCase;

public class IdentityUtilTest extends TestCase {

  public void testgetFirstDomainFromDN() {
    assertEquals(null, IdentityUtil.getFirstDomainFromDN(null));

    assertEquals(null, IdentityUtil.getFirstDomainFromDN(""));

    String dnString = "CN=Test User1,CN=users";
    assertEquals(null, IdentityUtil.getFirstDomainFromDN(dnString));

    dnString = "CN=Test User1,cn=users,dc=corp,dc=example,dc=com";
    assertEquals("corp", IdentityUtil.getFirstDomainFromDN(dnString));

    dnString = "CN=Test User1, CN=users, DC=corp, DC=example, DC=com";
    assertEquals("corp", IdentityUtil.getFirstDomainFromDN(dnString));

    dnString = "CN=Test User1 , CN=users , DC=corp , DC=example , DC=com";
    assertEquals("corp", IdentityUtil.getFirstDomainFromDN(dnString));

    dnString = "CN=Test User1,CN=users,DC=North America,DC=example,DC=com";
    assertEquals("North America", IdentityUtil.getFirstDomainFromDN(dnString));

    dnString =
        "CN = Test User1,CN = users,DC = North America,DC = example,DC = com";
    assertNotSame("North America", IdentityUtil.getFirstDomainFromDN(dnString));

    dnString = "CN=Test User1,CN=users,dC=North America,DC=example,DC=com";
    assertEquals("example", IdentityUtil.getFirstDomainFromDN(dnString));

    dnString = ",,,DC=corp,,DC=example,DC=com";
    assertEquals("corp", IdentityUtil.getFirstDomainFromDN(dnString));

    dnString = "CN=Test User1,,,DC=example,DC=com";
    assertEquals("example", IdentityUtil.getFirstDomainFromDN(dnString));
  }

  public void testGetCanonicalUsername() {
    AuthenticationIdentity identity =
        new SimpleAuthenticationIdentity("", DmInitialize.DM_PWD_OK1);
    String result = IdentityUtil.getCanonicalUsername(identity);
    assertEquals(null, result);

    identity =
        new SimpleAuthenticationIdentity(null, DmInitialize.DM_PWD_OK1);
    result = IdentityUtil.getCanonicalUsername(identity);
    assertEquals(null, result);

    identity =
        new SimpleAuthenticationIdentity("Joe@corp", DmInitialize.DM_PWD_OK1);
    result = IdentityUtil.getCanonicalUsername(identity);
    assertEquals("Joe", result);

    identity =
        new SimpleAuthenticationIdentity("Mary@corp.example.com",
            DmInitialize.DM_PWD_OK1);
    result = IdentityUtil.getCanonicalUsername(identity);
    assertEquals("Mary", result);
  }

  public void testGetDomain() {
    AuthenticationIdentity identity =
        new SimpleAuthenticationIdentity("", DmInitialize.DM_PWD_OK1);
    String result = IdentityUtil.getDomain(identity);
    assertEquals(null, result);

    identity =
        new SimpleAuthenticationIdentity("Joe@corp", DmInitialize.DM_PWD_OK1);
    result = IdentityUtil.getDomain(identity);
    assertEquals("corp", result);

    identity =
        new SimpleAuthenticationIdentity("Mary@entops.example.com",
            DmInitialize.DM_PWD_OK1);
    result = IdentityUtil.getDomain(identity);
    assertEquals("entops", result);

    identity =
        new SimpleAuthenticationIdentity("Mary@entops.example.com",
            DmInitialize.DM_PWD_OK1, "");
    result = IdentityUtil.getDomain(identity);
    assertEquals("entops", result);

    identity =
        new SimpleAuthenticationIdentity("Mary@entops.example.com",
            DmInitialize.DM_PWD_OK1, "eng");
    result = IdentityUtil.getDomain(identity);
    assertEquals("eng", result);
  }
}
