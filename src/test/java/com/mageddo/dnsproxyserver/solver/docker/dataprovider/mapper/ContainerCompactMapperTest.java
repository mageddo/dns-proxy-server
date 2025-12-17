package com.mageddo.dnsproxyserver.solver.docker.dataprovider.mapper;

import org.junit.jupiter.api.Test;

import testing.templates.docker.ContainerTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContainerCompactMapperTest {


  @Test
  void mustBuildDpsContainerCompact() {
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

  @Test
  void mustBuildRegularContainerCompact() {
    // arrange
    final var container = ContainerTemplates.buildRegularContainerCoffeeMakerCheckout();

    // act
    final var cc = ContainerCompactMapper.of(container);

    // assert
    assertNotNull(cc);
    assertFalse(cc.getDpsContainer());
    assertEquals("e3485f240269afeb6c3adb6d1936e761a06836675c808934d97a513ad5eb8895", cc.getId());
    assertEquals("/coffee-maker-checkout_build_run_5c85fb54c596", cc.getName());
  }
}
