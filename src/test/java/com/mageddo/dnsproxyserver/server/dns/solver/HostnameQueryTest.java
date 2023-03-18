package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.templates.HostnameQueryTemplates;
import com.mageddo.dnsproxyserver.templates.HostnameTemplates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HostnameQueryTest {

  @Test
  void mustMatchWildcard(){

    // arrange
    final var acme = HostnameQueryTemplates.acmeCom();

    // act
    final var matches = acme.matches(HostnameTemplates.COM_WILDCARD);

    // assert
    assertTrue(matches);

  }

  @Test
  void mustMatchWildcardWhenUsingSubdomain(){

    // arrange
    final var acme = HostnameQueryTemplates.orangeAcmeCom();

    // act
    final var matches = acme.matches(acme.getHostname());

    // assert
    assertTrue(matches);

  }

  @Test
  void mustMatchExactHostname(){

    // arrange
    final var acme = HostnameQueryTemplates.acmeCom();

    // act
    final var matches = acme.matches(acme.getHostname());

    // assert
    assertTrue(matches);

  }

}
