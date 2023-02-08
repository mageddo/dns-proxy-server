package com.mageddo.dnsproxyserver.server.dns.solver;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
class SolverRemoteTest {

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
