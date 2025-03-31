package com.mageddo.dnsproxyserver.config.provider.legacyenv;

import org.junit.jupiter.api.Test;
import testing.templates.config.ConfigEnvTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfigEnvMapperTest {

  @Test
  void mustMapStubSolverDomainName(){

    final var configEnv = ConfigEnvTemplates.withStubSolverDomainName();

    final var config = ConfigEnvMapper.toConfig(configEnv);

    final var solverStub = config.getSolverStub();
    assertNotNull(solverStub);
    assertEquals(configEnv.getSolverStubDomainName(), solverStub.getDomainName());
  }
}
