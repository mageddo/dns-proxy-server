package com.mageddo.dnsproxyserver.docker.application;

import com.mageddo.dnsproxyserver.solver.docker.Label;

import org.junit.jupiter.api.Test;

import testing.templates.docker.ContainerTemplates;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LabelsTest {

  @Test
  void mustFindBooleanDefaultValue() {

    final var container = ContainerTemplates.buildDpsContainer();

    final var r = Labels.findBoolean(container, Label.DPS_CONTAINER, false);

    assertTrue(r);

  }

  @Test
  void mustNotFindDpsContainerEnabled() {

    final var container = ContainerTemplates.buildDpsContainer();

    final var r = Labels.findBoolean(container, Label.DPS_CONTAINER_ENABLED, false);

    assertFalse(r);

  }
}
