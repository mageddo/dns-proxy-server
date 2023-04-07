package com.mageddo.dnsproxyserver.docker;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.server.dns.solver.HostnameQuery;
import testing.templates.HostnameQueryTemplates;
import testing.templates.HostnameTemplates;
import testing.templates.docker.InspectContainerResponseTemplates;
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
  void mustSolveFromContainerHostnameWithoutNoDomain(){
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
  void mustSolveFromContainerHostnameWithDomain(){
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
    final var hostname = HostnameQueryTemplates.nginxComBrWildcard();
    final var config = Configs.getInstance();

    // act
    final var test = ContainerHostnameMatcher.test(inspect, hostname, config);

    // assert
    assertTrue(test, String.valueOf(hostname));
  }

  @Test
  void mustSolveRegexFromContainerHostnameEnv(){
    // arrange
    final var inspect = InspectContainerResponseTemplates.buildWithHostnamesEnv("/nginx.+/,/acme.+/");
    final var hostname = HostnameQuery.ofRegex(HostnameTemplates.NGINX_COM_BR);
    final var hostnameAcme = HostnameQuery.ofRegex(HostnameTemplates.NGINX_COM_BR);
    final var config = Configs.getInstance();

    // act
    final var testNginx = ContainerHostnameMatcher.test(inspect, hostname, config);
    final var testAcme = ContainerHostnameMatcher.test(inspect, hostnameAcme, config);

    // assert
    assertTrue(testNginx, String.valueOf(hostname));
    assertTrue(testAcme, String.valueOf(hostnameAcme));
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
