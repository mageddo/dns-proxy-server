package com.mageddo.dnsproxyserver.server.dns;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.lang3.ClassUtils;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class Server0StarterTest {


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
    assertEquals("[SolverSystem, DockerSolver, RemoteSolver]", names.toString());
  }
}
