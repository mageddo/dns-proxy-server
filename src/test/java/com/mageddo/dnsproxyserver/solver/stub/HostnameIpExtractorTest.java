package com.mageddo.dnsproxyserver.solver.stub;

import com.mageddo.net.IpAddr;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testing.templates.HostnameTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HostnameIpExtractorTest {

  static final String DOMAIN = "sslip.io";

  @Test
  @DisplayName("Must extract ipv4 from hostname starting with name, using dot-decimal notation")
  void mustExtractIpv4IpFromHostnameStartingWithNameUsingDotDecimalNotation() {
    final var hostname = HostnameTemplates.startingWithNameDotDecimalNotation();

    final var addr = HostnameIpExtractor.extract(hostname, DOMAIN);

    assertEquals(IpAddr.of("192.168.0.1"), addr);
  }

  @Test
  @DisplayName("Must extract ipv4 from hostname starting with name, separated by dash, using dot-decimal notation")
  void mustExtractIpv4IpFromHostnameStartingWithNameSeparatedByDashUsingDotDecimalNotation() {
    final var hostname = HostnameTemplates.startingWithNameDashSeparationDotDecimalNotation();

    final var addr = HostnameIpExtractor.extract(hostname, DOMAIN);

    assertEquals(IpAddr.of("192.168.0.2"), addr);
  }

  @Test
  void mustRemoveDomainFromHostname() {
    final var hostname = HostnameTemplates.startingWithNameDotDecimalNotation();

    final var subdomain = HostnameIpExtractor.removeDomainFrom(hostname, DOMAIN);

    assertEquals("www.192.168.0.1", subdomain);
  }

  @Test
  void mustKeepTheHostnameWhenDomainIsNotPresent() {
    final var hostname = "example.com";

    final var subdomain = HostnameIpExtractor.removeDomainFrom(hostname, DOMAIN);

    assertEquals(hostname, subdomain);
  }
}
