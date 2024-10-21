package com.mageddo.dnsproxyserver.solver;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class SolverRemoteTest {

  @Spy
  @InjectMocks
  SolverRemote solverRemote;

//  @Test
//  void mustPingRemoteServerWhileQueryingWhenFeatureIsActive(){
//
//    // arrange
//    final var query = MessageTemplates.acmeAQuery();
//    final var answer = MessageTemplates.buildAAnswer(query);
//
//    doReturn(true).when(this.solverRemote).isPingWhileGettingQueryResponseActive();
//
//    doReturn(ResolverTemplates.googleDnsAsList())
//        .when(this.solverRemote)
//        .findResolversToUse()
//    ;
//
//    doReturn(CompletableFuture.completedFuture(answer))
//        .when(this.solverRemote)
//        .sendQueryAsyncToResolver(any());
//
//    this.excludeCircuitBreakerStrategyAndCallQueryMethodDirectly();
//
//    // act
//    final var res = this.solverRemote.handle(query);
//
//    // assert
//    assertNotNull(res);
//    verify(this.solverRemote).pingWhileGettingQueryResponse(any(), any());
//
//  }
//
//  @Test
//  void pingRemoteServerWhileQueryingDisabledByDefault(){
//
//    // act
//    final var active = this.solverRemote.isPingWhileGettingQueryResponseActive();
//
//    // assert
//    assertFalse(active);
//
//  }

  void excludeCircuitBreakerStrategyAndCallQueryMethodDirectly() {
    doAnswer(iom -> Supplier.class.cast(iom.getArgument(1)).get())
      .when(this.solverRemote)
      .queryUsingCircuitBreaker(any(), any())
    ;
  }
}
