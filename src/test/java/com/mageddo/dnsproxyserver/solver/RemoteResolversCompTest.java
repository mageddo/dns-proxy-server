package com.mageddo.dnsproxyserver.solver;

import com.mageddo.dnsproxyserver.solver.RemoteResolvers;
import dagger.sheath.junit.DaggerTest;
import org.junit.jupiter.api.Test;
import testing.ContextSupplier;
import testing.Events;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DaggerTest(initializer = ContextSupplier.class, eventsHandler = Events.class)
class RemoteResolversCompTest {

  @Inject
  RemoteResolvers remoteResolvers;

  @Test
  void mustBuildWithDefaultRemoteServer(){

    // arrange

    // act
    final var resolvers = this.remoteResolvers.resolvers();

    // assert
    assertEquals(1, resolvers.size(), String.valueOf(resolvers));
    assertEquals("[SimpleResolver [/8.8.8.8:53]]", resolvers.toString());

  }

}
