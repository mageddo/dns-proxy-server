package com.mageddo.dnsproxyserver.docker;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.Hostname;
import com.mageddo.dnsproxyserver.templates.docker.InspectContainerResponseTemplates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContainerHostnameMatcherTest {

  @BeforeEach
  void beforeEach(){
    Configs.clear();
  }

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
  void mustSolveFromContainerHostnameAndDomain(){
    // arrange
    final var inspect = InspectContainerResponseTemplates.buildWithHostnameAndDomain("acme.com", "local");
    final var hostname = Hostname.of("acme.com.local");
    final var config = Configs.getInstance();

    // act
    final var test = ContainerHostnameMatcher.test(inspect, hostname, config);

    // assert
    assertTrue(test, String.valueOf(hostname));
  }

  @Test
  void mustSolveFromContainerHostnameEnv(){
    // arrange
    final var inspect = InspectContainerResponseTemplates.build();
    final var hostname = Hostname.of("nginx.com.br");
    final var config = Configs.getInstance();

    // act
    final var test = ContainerHostnameMatcher.test(inspect, hostname, config);

    // assert
    assertTrue(test, String.valueOf(hostname));
  }

  @Test
  void mustSolveFromContainerName(){
    // arrange
    final var inspect = InspectContainerResponseTemplates.build();
    final var hostname = Hostname.of("laughing_swanson.docker");
    final var config = Configs.buildAndRegister(new String[]{"--register-container-names"});

    // act
    final var test = ContainerHostnameMatcher.test(inspect, hostname, config);

    // assert
    assertTrue(test, String.valueOf(hostname));
    assertEquals("docker", config.getDomain());
    assertTrue(config.getRegisterContainerNames());
  }

  @Test
  void mustSolveFromServiceName(){
    // arrange
    final var inspect = InspectContainerResponseTemplates.build();
    final var hostname = Hostname.of("nginx-service.docker");
    final var config = Configs.buildAndRegister(new String[]{"--register-container-names"});

    // act
    final var test = ContainerHostnameMatcher.test(inspect, hostname, config);

    // assert
    assertTrue(test, String.valueOf(hostname));
    assertEquals("docker", config.getDomain());
    assertTrue(config.getRegisterContainerNames());
  }


  @Test
  void mustNOTSolveFromServiceNameWhenFeatureIsDisabled(){
    // arrange
    final var inspect = InspectContainerResponseTemplates.build();
    final var hostname = Hostname.of("shibata.docker");
    final var config = Configs.getInstance();

    // act
    final var test = ContainerHostnameMatcher.test(inspect, hostname, config);

    // assert
    assertFalse(test, String.valueOf(hostname));
    assertFalse(config.getRegisterContainerNames());
    assertEquals("docker", config.getDomain());
  }

}
