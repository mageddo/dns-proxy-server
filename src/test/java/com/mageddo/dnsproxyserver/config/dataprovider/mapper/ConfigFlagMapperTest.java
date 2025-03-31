package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import com.mageddo.dnsproxyserver.config.cmdargs.dataprovider.mapper.ConfigFlagMapper;
import org.junit.jupiter.api.Test;
import testing.templates.ConfigFlagTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigFlagMapperTest {
  @Test
  void mustMapStubSolverDomainName(){

    final var configFlag = ConfigFlagTemplates.withStubSolverDomainName();

    final var config = ConfigFlagMapper.toConfig(configFlag);

    assertEquals("stub", config.getSolverStubDomainName());

  }
}
