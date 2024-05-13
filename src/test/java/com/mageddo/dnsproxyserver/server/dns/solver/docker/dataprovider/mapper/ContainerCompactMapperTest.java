package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper;

import org.junit.jupiter.api.Test;
import testing.templates.docker.ContainerTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContainerCompactMapperTest {


  @Test
  void mustBuildContainerCompact() {
    // arrange
    final var container = ContainerTemplates.buildDpsContainer();

    // act
    final var cc = ContainerCompactMapper.of(container);

    // assert
    assertNotNull(cc);
    assertTrue(cc.getDpsContainer());
    assertEquals("e7a629b149e358bcd14a49b2654937b67f26417daffa083876fb195db17e261b", cc.getId());
    assertEquals("/nice_bell", cc.getName());
  }
}
