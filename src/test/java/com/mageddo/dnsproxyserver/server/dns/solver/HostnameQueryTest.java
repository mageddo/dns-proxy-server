package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.templates.HostnameQueryTemplates;
import com.mageddo.dnsproxyserver.templates.HostnameTemplates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HostnameQueryTest {

  @Test
  void mustMatchWildcard(){

    // arrange
    final var acme = HostnameQueryTemplates.acmeComWildcard();

    // act
    final var matches = acme.matches(HostnameTemplates.COM_WILDCARD);

    // assert
    assertTrue(matches);

  }

  @Test
  void mustMatchWildcardWhenUsingSubdomain(){

    // arrange
    final var acme = HostnameQueryTemplates.orangeAcmeComWildcard();

    // act
    final var matches = acme.matches(acme.getHostname());

    // assert
    assertTrue(matches);

  }

  @Test
  void mustMatchExactHostname(){

    // arrange
    final var acme = HostnameQueryTemplates.acmeComWildcard();

    // act
    final var matches = acme.matches(acme.getHostname());

    // assert
    assertTrue(matches);

  }

  @Test
  void mustMatchRegex(){

    // arrange
    final var acme = HostnameQuery.ofRegex(HostnameTemplates.ACME_HOSTNAME);
    final var queryRegex = "[a-z]+\\.com";

    // act
    final var matches = acme.matches(queryRegex);

    // assert
    assertTrue(matches);

  }

}
