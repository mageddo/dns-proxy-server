package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.dnsproxyserver.server.dns.solver.Solver;
import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.Test;
import testing.ContextSupplier;
import testing.Events;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class ServerStarterCompTest {

  @Inject
  ServerStarter serverStarter;

  @Test
  void mustCreateSolverListInRightOrder(){

    // arrange

    // act
    final var names = this.serverStarter.getSolvers()
      .stream()
      .map(Solver::name)
      .toList();


    // assert
    assertEquals("[SolverSystem, SolverDocker, SolverLocalDB, SolverCachedRemote]", names.toString());
  }
}
