package com.mageddo.dnsproxyserver.config.dataprovider.mapper;

import org.junit.jupiter.api.Test;
import testing.templates.ConfigFlagTemplates;

import static org.junit.jupiter.api.Assertions.*;

class ConfigFlagMapperTest {
  @Test
  void mustMapStubSolverDomainName(){
    final var configFlag = ConfigFlagTemplates.withStubSolverDomainName();
  }
}
