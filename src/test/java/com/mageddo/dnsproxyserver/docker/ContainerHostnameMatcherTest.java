package com.mageddo.dnsproxyserver.docker;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.Hostname;
import com.mageddo.utils.templates.InspectContainerResponseTemplates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ContainerHostnameMatcherTest {


  @Test
  void mustSolveFromContainerHostnameButNoDomain(){
    // arrange
    final var inspect = InspectContainerResponseTemplates.buildWithHostnameAndWithoutDomain();
    final var hostname = Hostname.of("nginx-2.dev");
    final var config = Configs.getInstance();

    // act
    final var test = ContainerHostnameMatcher.test(inspect, hostname, config);

    // assert
    assertTrue(test, String.valueOf(hostname));
  }

  @Test
  void mustSolveContainerHostnameAndDomain(){
    // arrange
    final var inspect = InspectContainerResponseTemplates.buildWithHostnameAndDomain("acme.com", "local");
    final var hostname = Hostname.of("acme.com.local");
    final var config = Configs.getInstance();

    // act
    final var test = ContainerHostnameMatcher.test(inspect, hostname, config);

    // assert
    assertTrue(test, String.valueOf(hostname));
  }

}
