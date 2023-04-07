package com.mageddo.dnsproxyserver.server.dns.solver;

import com.mageddo.dnsproxyserver.config.Configs;
import com.mageddo.dnsproxyserver.quarkus.Instances;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class SolverProviderTest {

  SolverProvider provider;

  @BeforeEach
  void beforeEach(){
    this.provider = spy(new SolverProvider(Instances.of(
      new SolverMock("SolverSystem"),
      new SolverMock("SolverDocker"),
      new SolverMock("SolverLocalDB"),
      new SolverMock("SolverCachedRemote")
    )));
  }

  @Test
  void mustDisableRemoteSolversWhenNoRemoteServersOptionIsEnabled() {
    // arrange
    final var config = spy(Configs.getInstance());
    doReturn(config)
      .when(this.provider)
      .getConfig();

    doReturn(true)
      .when(config)
      .isNoRemoteServers()
    ;

    // act
    final var names = this.provider.getSolversNames();

    // assert
    assertEquals("[SolverSystem, SolverDocker, SolverLocalDB]", names.toString());
  }
}
