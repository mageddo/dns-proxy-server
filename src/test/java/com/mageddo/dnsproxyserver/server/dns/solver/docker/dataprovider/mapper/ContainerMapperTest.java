package com.mageddo.dnsproxyserver.server.dns.solver.docker.dataprovider.mapper;

import org.junit.jupiter.api.Test;
import testing.templates.docker.InspectContainerResponseTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContainerMapperTest {

  @Test
  void mustPutSpecifiedNetworkFirst(){
    // arrange
    final var inspect = InspectContainerResponseTemplates.withDpsLabel();

    // act
    final var container = ContainerMapper.of(inspect);

    // assert
    assertNotNull(container);
    assertEquals("shibata", container.getFirstNetworkName());
  }
}
