package com.mageddo.dnsproxyserver.config.mapper;

import com.mageddo.dnsproxyserver.config.Config;
import com.mageddo.net.IP;
import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.Test;
import testing.ContextSupplier;
import testing.Events;
import testing.templates.ConfigTemplates;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class ConfigMapperCompTest {

  @Inject
  ConfigMapper configMapper;

  @Test
  void mustMapFromDaoConfigsToCurrentConfig() {
    // arrange
    final var config = ConfigTemplates.defaultWithoutId();

    // act
    final var currentConfig = this.configMapper.mapFrom(List.of(config));

    // assert
    assertNotNull(currentConfig);
  }

  @Test
  void mustMapSolverStub() {
    // arrange
    final var theDefault = ConfigTemplates.defaultWithoutId();
    final var another = ConfigTemplates.acmeSolverStub();

    // act
    final var currentConfig = this.configMapper.mapFrom(List.of(theDefault, another));

    // assert
    assertNotNull(currentConfig);

    final var solverStub = currentConfig.getSolverStub();
    assertNotNull(solverStub);
    assertEquals("acme", solverStub.getDomainName());
  }

  @Test
  void mustMapSolverLocal(){

    final var theDefault = ConfigTemplates.defaultWithoutId();
    final var another = ConfigTemplates.acmeSolverLocal();

    final var currentConfig = this.configMapper.mapFrom(List.of(theDefault, another));

    assertNotNull(currentConfig);

    final var solverLocal = currentConfig.getSolverLocal();
    assertNotNull(solverLocal);
    assertEquals(Config.Env.DEFAULT_ENV, solverLocal.getActiveEnv());

    final var firstEnv = solverLocal.getFirst();
    assertNotNull(firstEnv);

    final var firstEntry = firstEnv.getFirstEntry();
    assertNotNull(firstEntry);
    assertEquals("acme.com", firstEntry.getHostname());
    assertEquals(Config.Entry.Type.A, firstEntry.getType());
    assertEquals(IP.of("192.168.0.3"), firstEntry.getIp());
  }
}
