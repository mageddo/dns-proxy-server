package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.di.Context;
import dagger.sheath.junit.DaggerTest;
import org.apache.commons.lang3.ClassUtils;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DaggerTest(component = Context.class)
class ServerStarterTest {

  @Inject
  ServerStarter serverStarter;

  @Test
  void mustCreateSolverListInRightOrder(){

    // arrange

    // act
    final var names = this.serverStarter.getSolvers()
      .stream()
      .map(ClassUtils::getSimpleName)
      .toList();


    // assert
    assertEquals("[SolverSystem, SolverDocker, SolverLocalDB, SolverRemote]", names.toString());
  }
}
