package com.mageddo.dnsproxyserver.solver.remote.application;

import com.mageddo.dnsproxyserver.solver.RemoteResolvers;
import com.mageddo.dnsproxyserver.solver.Resolver;
import com.mageddo.dnsproxyserver.solver.SimpleResolver;
import com.mageddo.dnsproxyserver.solver.remote.CircuitStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testing.templates.InetSocketAddressTemplates;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ResolverStatsFactoryTest {

  @Mock
  CircuitBreakerService circuitBreakerService;

  @Mock
  RemoteResolvers remoteResolvers;

  @Spy
  @InjectMocks
  ResolverStatsFactory factory;

  @Test
  void mustFindOnlyResolversValidToUse(){

    // arrange
    final var server1 = new SimpleResolver(InetSocketAddressTemplates._8_8_8_8());
    final var server2 = new SimpleResolver(InetSocketAddressTemplates._8_8_4_4());
    final var server3 = new SimpleResolver(InetSocketAddressTemplates._1_1_1_1());

    doReturn(CircuitStatus.OPEN)
      .when(this.circuitBreakerService)
      .getCircuitStatus(server1.getAddress());

    doReturn(CircuitStatus.CLOSED)
      .when(this.circuitBreakerService)
      .getCircuitStatus(server2.getAddress());

    doReturn(List.of(server1, server2, server3))
      .when(this.remoteResolvers)
      .resolvers()
    ;

    // act
    final var resolvers = this.factory.findResolversWithNonOpenCircuit()
      .stream()
      .map(Resolver::getAddress)
      .toList()
      .toString();

    // assert
    assertEquals("[/8.8.8.8:53, /8.8.4.4:53, /1.1.1.1:53]", resolvers);
  }
}
