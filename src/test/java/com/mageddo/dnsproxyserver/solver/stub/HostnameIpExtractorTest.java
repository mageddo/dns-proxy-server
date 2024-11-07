package com.mageddo.dnsproxyserver.solver.stub;

import com.mageddo.net.IP;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testing.templates.HostnameTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HostnameIpExtractorTest {

  static final String SSLIP_IO = "sslip.io";
  static final String NIP_IO = "nip.io";

  @Test
  @DisplayName("Must extract ipv4 from hostname starting with name, using dot-decimal notation")
  void mustExtractIpv4IpFromHostnameStartingWithNameUsingDotDecimalNotation() {
    final var hostname = HostnameTemplates.startingWithNameDotDecimalNotation();

    final var addr = HostnameIpExtractor.extract(hostname, SSLIP_IO);

    assertEquals(IP.of("192.168.0.1"), addr);
  }

  @Test
  @DisplayName("Must extract ipv4 from hostname starting with name, separated by dash, using dot-decimal notation")
  void mustExtractIpv4IpFromHostnameStartingWithNameSeparatedByDashUsingDotDecimalNotation() {
    final var hostname = HostnameTemplates.startingWithNameDashSeparationDotDecimalNotation();

    final var addr = HostnameIpExtractor.extract(hostname, SSLIP_IO);

    assertEquals(IP.of("192.168.0.2"), addr);
  }

  @Test
  void mustExtractIpWhenAllUsingDots() {
    final var hostname = "customer1.app.10.0.0.1.nip.io";

    final var addr = HostnameIpExtractor.extract(hostname, NIP_IO);

    assertEquals(IP.of("10.0.0.1"), addr);
  }

  @Test
  void mustExtractIpWhenAllUsingShortIpv6() {
    final var hostname = "customer1.app.a--1.sslip.io";

    final var addr = HostnameIpExtractor.extract(hostname, SSLIP_IO);

    assertEquals(IP.of("a:0:0:0:0:0:0:1"), addr);
  }

  @Test
  void mustExtractIpv4FromTheEnd() {
    final var hostname = "116-116-203-255-68.nip.io";

    final var addr = HostnameIpExtractor.extract(hostname, NIP_IO);

    assertEquals(IP.of("116.203.255.68"), addr);
  }

  @Test
  void mustExtractIpWhenAllUsingShortIpv6EvenWhenNameIsAValidHexadecimal() {
    final var hostname = "bb.a--1.sslip.io";

    final var addr = HostnameIpExtractor.extract(hostname, SSLIP_IO);

    assertEquals(IP.of("a:0:0:0:0:0:0:1"), addr);
  }

  @Test
  void mustExtractIpv4IpFromLabeledHexadecimal() {
    final var hostname = "acme.c0a801fc.sslip.io";

    final var addr = HostnameIpExtractor.extract(hostname, SSLIP_IO);

    assertEquals(IP.of("192.168.1.252"), addr);
  }

  @Test
  void mustExtractIpv4IpFromHexadecimal() {
    final var hostname = "c0a801fc.sslip.io";

    final var addr = HostnameIpExtractor.extract(hostname, SSLIP_IO);

    assertEquals(IP.of("192.168.1.252"), addr);
  }

  @Test
  void mustExtractIpv6IpFromLabeledHexadecimal() {
    final var hostname = "acme.20010DB8000000000000000000000001.sslip.io";

    final var addr = HostnameIpExtractor.extract(hostname, SSLIP_IO);

    assertEquals(IP.of("2001:db8::1"), addr);
  }

  @Test
  void mustRemoveDomainFromHostname() {
    final var hostname = HostnameTemplates.startingWithNameDotDecimalNotation();

    final var subdomain = HostnameIpExtractor.removeDomainFrom(hostname, SSLIP_IO);

    assertEquals("www.192.168.0.1", subdomain);
  }

  @Test
  void mustKeepTheHostnameWhenDomainIsNotPresent() {
    final var hostname = "example.com";

    final var subdomain = HostnameIpExtractor.removeDomainFrom(hostname, SSLIP_IO);

    assertEquals(hostname, subdomain);
  }
}
