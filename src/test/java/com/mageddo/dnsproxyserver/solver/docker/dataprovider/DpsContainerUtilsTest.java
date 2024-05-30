package com.mageddo.dnsproxyserver.solver.docker.dataprovider;

import com.mageddo.dnsproxyserver.solver.docker.dataprovider.DpsContainerUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DpsContainerUtilsTest {
  @Test
  void mustParseWhenSingleHostname() {
    // arrange
    final var envs = new String[]{"HOSTNAME=xxx", "HOSTNAMES=acme.com"};

    // act
    final var hostnames = DpsContainerUtils.findHostnamesFromEnv(envs);

    // assert
    assertEquals(1, hostnames.size(), String.valueOf(hostnames));
    assertEquals("[acme.com]", String.valueOf(hostnames));
  }

  @Test
  void mustParseWhenMultipleHostnamesWithWhiteSpaces() {
    // arrange
    final var envs = new String[]{"HOSTNAME=xxx", "HOSTNAMES=acme.com, mageddo.com , ahnegao.com.br"};

    // act
    final var hostnames = DpsContainerUtils.findHostnamesFromEnv(envs);

    // assert
    assertEquals(3, hostnames.size(), String.valueOf(hostnames));
    assertEquals("[acme.com, mageddo.com, ahnegao.com.br]", String.valueOf(hostnames));
  }

  @Test
  void mustParseWhenMultipleHostnamesWithoutWhiteSpaces() {
    // arrange
    final var envs = new String[]{"HOSTNAME=xxx", "HOSTNAMES=acme.com,mageddo.com"};

    // act
    final var hostnames = DpsContainerUtils.findHostnamesFromEnv(envs);

    // assert
    assertEquals(2, hostnames.size(), String.valueOf(hostnames));
    assertEquals("[acme.com, mageddo.com]", String.valueOf(hostnames));
  }

  @Test
  void mustParseWhenMultipleWildcardHostnames() {
    // arrange
    final var envs = new String[]{"HOSTNAME=xxx", "HOSTNAMES=.localhost,.subdomain"};

    // act
    final var hostnames = DpsContainerUtils.findHostnamesFromEnv(envs);

    // assert
    assertEquals(2, hostnames.size(), String.valueOf(hostnames));
    assertEquals("[.localhost, .subdomain]", String.valueOf(hostnames));
  }
}
