package com.mageddo.dnsproxyserver.server.dns;

import com.mageddo.utils.dagger.TestContext;
import com.mageddo.utils.dagger.mockito.DaggerTest;
import org.apache.commons.lang3.ClassUtils;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DaggerTest(component = TestContext.class)
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
    assertEquals("[SolverSystem, SolverDocker, SolverLocalDB, SolverRemote]", names.toString());
  }
}
