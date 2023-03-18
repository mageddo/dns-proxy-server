package com.mageddo.dnsproxyserver.docker;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import com.mageddo.dnsproxyserver.templates.HostnameQueryTemplates;
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
    final var hostname = HostnameQueryTemplates.nginxWildcard();
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
    final var hostname = HostnameQueryTemplates.acmeComLocal();
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
    final var hostname = HostnameQueryTemplates.nginxComBr();
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
    final var hostname = HostnameQuery.ofWildcard("laughing_swanson.docker");
    final var config = Configs.build(new String[]{"--register-container-names"});

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
    final var hostname = HostnameQuery.of("nginx-service.docker");
    final var config = Configs.build(new String[]{"--register-container-names"});

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
    final var hostname = HostnameQuery.of("shibata.docker");
    final var config = Configs.getInstance();

    // act
    final var test = ContainerHostnameMatcher.test(inspect, hostname, config);

    // assert
    assertFalse(test, String.valueOf(hostname));
    assertFalse(config.getRegisterContainerNames());
    assertEquals("docker", config.getDomain());
  }

}
