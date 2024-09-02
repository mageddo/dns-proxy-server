package com.mageddo.dnsproxyserver.solver;

import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.Test;
import testing.ContextSupplier;
import testing.Events;
import testing.templates.MessageTemplates;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class SolverRemoteCompTest {

  @Inject
  SolverRemote solver;

  @Test
  void mustSolveFromAvailableResolvers(){

    // arrange
    final var query = MessageTemplates.acmeAQuery();

    // act
    final var res = this.solver.handle(query);


    // assert
    assertNotNull(res);

  }
}
