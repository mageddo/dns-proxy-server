package com.mageddo.dnsproxyserver.solver;

import java.util.Arrays;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import dagger.sheath.junit.DaggerTest;
import testing.ContextSupplier;
import testing.Events;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class SolverProviderCompTest {

  @Inject
  SolverProvider provider;

  @Test
  void mustCreateSolverListInRightOrder() {

    // arrange

    // act
    final var names = this.provider.getSolversNames();

    // assert
    assertEquals(Arrays.toString(SolverProvider.solversOrder), names.toString());
  }

}
